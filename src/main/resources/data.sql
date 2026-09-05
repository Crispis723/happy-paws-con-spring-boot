INSERT INTO clientes (documento_tipo_codigo, numero_documento, razon_social, direccion, telefono, email)
VALUES ('CC', '10000001', 'Cliente de Prueba', 'Calle 1 # 2-3', '3000000000', 'cliente.prueba@happypaws.com')
ON CONFLICT (numero_documento) DO NOTHING;
