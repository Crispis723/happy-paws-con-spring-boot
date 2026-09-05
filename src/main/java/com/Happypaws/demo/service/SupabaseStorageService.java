package com.Happypaws.demo.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Cliente minimalista de Supabase Storage (REST API), usado desde el backend
 * para que los archivos del historial clinico sobrevivan a los redeploys de
 * Render (el disco local de Render es efimero en el plan free).
 *
 * IMPORTANTE: usa la "service_role key" de Supabase, NO la "anon key".
 * La service_role key se salta las políticas de Row Level Security y debe
 * tratarse como un secreto de servidor: nunca debe llegar al navegador ni a
 * codigo de frontend. Aqui solo se usa desde el backend Java.
 *
 * Requiere crear de antemano un bucket PRIVADO en el dashboard de Supabase
 * (Storage -> New bucket). Por defecto se asume el nombre "historial-clinico".
 */
@Service
public class SupabaseStorageService {

    private final String baseUrl;
    private final String serviceKey;
    private final String bucket;
    private final HttpClient httpClient;
    private final boolean localMode;
    private final Path localStorageRoot;

    public SupabaseStorageService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.service-key}") String serviceKey,
            @Value("${supabase.storage.bucket:historial-clinico}") String bucket) {
        boolean missingSupabaseConfig = supabaseUrl == null || supabaseUrl.isBlank() || serviceKey == null || serviceKey.isBlank();
        this.localMode = missingSupabaseConfig;
        this.baseUrl = missingSupabaseConfig ? null : (supabaseUrl.endsWith("/") ? supabaseUrl.substring(0, supabaseUrl.length() - 1) : supabaseUrl);
        this.serviceKey = serviceKey;
        this.bucket = bucket;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.localStorageRoot = Paths.get(System.getProperty("user.dir"), "storage", bucket);
    }

    /** Sube (o reemplaza) un archivo dentro del bucket. Devuelve el "path" guardado. */
    public String subir(String path, byte[] contenido, String contentType) throws IOException {
        if (localMode) {
            Path destino = resolverLocal(path);
            Files.createDirectories(destino.getParent());
            Files.write(destino, contenido);
            return path;
        }

        String url = baseUrl + "/storage/v1/object/" + bucket + "/" + path;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + serviceKey)
                    .header("apikey", serviceKey)
                    .header("Content-Type", contentType != null && !contentType.isBlank() ? contentType : "application/octet-stream")
                    .header("x-upsert", "true")
                    .PUT(BodyPublishers.ofByteArray(contenido))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new IOException("Supabase Storage rechazo la subida (HTTP " + response.statusCode() + "): " + response.body());
            }
            return path;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IOException("Subida interrumpida", ex);
        }
    }

    /** Descarga el contenido binario de un archivo del bucket. */
    public byte[] descargar(String path) throws IOException {
        if (localMode) {
            return Files.readAllBytes(resolverLocal(path));
        }

        String url = baseUrl + "/storage/v1/object/" + bucket + "/" + path;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + serviceKey)
                    .header("apikey", serviceKey)
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() >= 300) {
                throw new IOException("Supabase Storage no pudo entregar el archivo (HTTP " + response.statusCode() + ")");
            }
            return response.body();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IOException("Descarga interrumpida", ex);
        }
    }

    /** Elimina un archivo del bucket. No lanza excepcion si ya no existe (no bloquea la operacion de negocio). */
    public void eliminar(String path) {
        if (path == null || path.isBlank()) {
            return;
        }
        if (localMode) {
            try {
                Files.deleteIfExists(resolverLocal(path));
            } catch (IOException ignored) {
                // Si no se puede borrar el archivo local, no bloqueamos la operacion.
            }
            return;
        }

        try {
            String url = baseUrl + "/storage/v1/object/" + bucket + "/" + path;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Bearer " + serviceKey)
                    .header("apikey", serviceKey)
                    .DELETE()
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception ignored) {
            // Si no se puede eliminar el archivo fisico en Supabase, no bloqueamos la operacion.
        }
    }

    private Path resolverLocal(String path) {
        return localStorageRoot.resolve(path.replace("/", java.io.File.separator));
    }
}
