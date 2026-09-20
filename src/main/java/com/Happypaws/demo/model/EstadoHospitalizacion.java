package com.Happypaws.demo.model;

/**
 * Estado clínico de una mascota hospitalizada. A diferencia de
 * {@link EstadoCita}, aquí no hay un flujo lineal esperado: una mascota
 * puede pasar de ESTABLE a CRITICO y volver, tantas veces como haga
 * falta, según cada control. Solo DADO_DE_ALTA y FALLECIDO cierran la
 * hospitalización.
 */
public enum EstadoHospitalizacion {

    EN_OBSERVACION("En observación", "#f39c12"),
    ESTABLE("Estable", "#2ecc71"),
    CRITICO("Crítico", "#e74c3c"),
    RECUPERANDOSE("Recuperándose", "#3498db"),
    DADO_DE_ALTA("Dado de alta", "#7f8c8d"),
    FALLECIDO("Falleció", "#2c3e50");

    private final String etiqueta;
    private final String colorHex;

    EstadoHospitalizacion(String etiqueta, String colorHex) {
        this.etiqueta = etiqueta;
        this.colorHex = colorHex;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getColorHex() {
        return colorHex;
    }

    /** Estados que cierran la hospitalización (ya no se agregan más controles). */
    public boolean esFinal() {
        return this == DADO_DE_ALTA || this == FALLECIDO;
    }
}
