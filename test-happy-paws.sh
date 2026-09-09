#!/bin/bash
# Script de Pruebas - Happy Paws Validación y Correos
# Uso: bash test-happy-paws.sh

# ====================================================================
# CONFIGURACIÓN
# ====================================================================

# Cambia esto a tu URL de Render
BASE_URL="https://happy-paws.onrender.com"
# Para local: BASE_URL="http://localhost:8080"

# Cambia esto a tu email para pruebas
TEST_EMAIL="tu-email@gmail.com"

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# ====================================================================
# FUNCIONES AUXILIARES
# ====================================================================

print_header() {
    echo -e "\n${BLUE}════════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}\n"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}→ $1${NC}"
}

# ====================================================================
# TEST 1: VALIDAR EMAIL
# ====================================================================

test_email_validation() {
    print_header "TEST 1: VALIDACIÓN DE EMAIL"
    
    print_info "Validando: test@gmail.com"
    RESPONSE=$(curl -s "$BASE_URL/api/test/validate/email/test@gmail.com")
    
    if echo "$RESPONSE" | grep -q "Email válido"; then
        print_success "Email válido detectado correctamente"
        echo "$RESPONSE" | jq .
    else
        print_error "Validación de email falló"
        echo "$RESPONSE"
    fi
    
    print_info "Validando: email-invalido"
    RESPONSE=$(curl -s "$BASE_URL/api/test/validate/email/email-invalido")
    
    if echo "$RESPONSE" | grep -q "Email inválido"; then
        print_success "Email inválido detectado correctamente"
        echo "$RESPONSE" | jq .
    else
        print_error "Validación de email inválido falló"
        echo "$RESPONSE"
    fi
}

# ====================================================================
# TEST 2: VALIDAR TELÉFONO
# ====================================================================

test_phone_validation() {
    print_header "TEST 2: VALIDACIÓN DE TELÉFONO"
    
    print_info "Validando: 999888777"
    RESPONSE=$(curl -s "$BASE_URL/api/test/validate/phone/999888777")
    
    if echo "$RESPONSE" | grep -q "Teléfono válido"; then
        print_success "Teléfono válido detectado"
        echo "$RESPONSE" | jq .
    else
        print_error "Validación de teléfono falló"
        echo "$RESPONSE"
    fi
    
    print_info "Validando: 12345 (demasiado corto)"
    RESPONSE=$(curl -s "$BASE_URL/api/test/validate/phone/12345")
    
    if echo "$RESPONSE" | grep -q "Teléfono inválido"; then
        print_success "Teléfono inválido detectado correctamente"
        echo "$RESPONSE" | jq .
    else
        print_error "Validación de teléfono inválido falló"
        echo "$RESPONSE"
    fi
}

# ====================================================================
# TEST 3: VALIDAR DNI
# ====================================================================

test_dni_validation() {
    print_header "TEST 3: VALIDACIÓN DE DNI"
    
    print_info "Validando: 12345678"
    RESPONSE=$(curl -s "$BASE_URL/api/test/validate/dni/12345678")
    
    if echo "$RESPONSE" | grep -q "DNI válido"; then
        print_success "DNI válido detectado"
        echo "$RESPONSE" | jq .
    else
        print_error "Validación de DNI falló"
        echo "$RESPONSE"
    fi
    
    print_info "Validando: 1234567 (falta un dígito)"
    RESPONSE=$(curl -s "$BASE_URL/api/test/validate/dni/1234567")
    
    if echo "$RESPONSE" | grep -q "DNI inválido"; then
        print_success "DNI inválido detectado correctamente"
        echo "$RESPONSE" | jq .
    else
        print_error "Validación de DNI inválido falló"
        echo "$RESPONSE"
    fi
}

# ====================================================================
# TEST 4: VALIDAR RUC
# ====================================================================

test_ruc_validation() {
    print_header "TEST 4: VALIDACIÓN DE RUC"
    
    print_info "Validando: 12345678901"
    RESPONSE=$(curl -s "$BASE_URL/api/test/validate/ruc/12345678901")
    
    if echo "$RESPONSE" | grep -q "RUC válido"; then
        print_success "RUC válido detectado"
        echo "$RESPONSE" | jq .
    else
        print_error "Validación de RUC falló"
        echo "$RESPONSE"
    fi
    
    print_info "Validando: 1234567890 (falta un dígito)"
    RESPONSE=$(curl -s "$BASE_URL/api/test/validate/ruc/1234567890")
    
    if echo "$RESPONSE" | grep -q "RUC inválido"; then
        print_success "RUC inválido detectado correctamente"
        echo "$RESPONSE" | jq .
    else
        print_error "Validación de RUC inválido falló"
        echo "$RESPONSE"
    fi
}

# ====================================================================
# TEST 5: VALIDAR FECHA
# ====================================================================

test_date_validation() {
    print_header "TEST 5: VALIDACIÓN DE FECHA"
    
    print_info "Validando: 25/12/2024"
    RESPONSE=$(curl -s "$BASE_URL/api/test/validate/date/25/12/2024")
    
    if echo "$RESPONSE" | grep -q "Fecha válida"; then
        print_success "Fecha válida detectada"
        echo "$RESPONSE" | jq .
    else
        print_error "Validación de fecha falló"
        echo "$RESPONSE"
    fi
    
    print_info "Validando: 31/02/2024 (febrero no tiene 31 días)"
    RESPONSE=$(curl -s "$BASE_URL/api/test/validate/date/31/02/2024")
    
    if echo "$RESPONSE" | grep -q "Fecha inválida"; then
        print_success "Fecha inválida detectada correctamente"
        echo "$RESPONSE" | jq .
    else
        print_error "Validación de fecha inválida falló"
        echo "$RESPONSE"
    fi
}

# ====================================================================
# TEST 6: VALIDAR NÚMERO
# ====================================================================

test_number_validation() {
    print_header "TEST 6: VALIDACIÓN DE NÚMERO"
    
    print_info "Validando: 100.50"
    RESPONSE=$(curl -s "$BASE_URL/api/test/validate/number/100.50")
    
    if echo "$RESPONSE" | grep -q "Número positivo válido"; then
        print_success "Número positivo detectado"
        echo "$RESPONSE" | jq .
    else
        print_error "Validación de número falló"
        echo "$RESPONSE"
    fi
    
    print_info "Validando: -50 (negativo)"
    RESPONSE=$(curl -s "$BASE_URL/api/test/validate/number/-50")
    
    if echo "$RESPONSE" | grep -q "no es positivo"; then
        print_success "Número negativo detectado correctamente"
        echo "$RESPONSE" | jq .
    else
        print_error "Validación de número negativo falló"
        echo "$RESPONSE"
    fi
}

# ====================================================================
# TEST 7: VALIDAR PORCENTAJE
# ====================================================================

test_percentage_validation() {
    print_header "TEST 7: VALIDACIÓN DE PORCENTAJE"
    
    print_info "Validando: 50"
    RESPONSE=$(curl -s "$BASE_URL/api/test/validate/percentage/50")
    
    if echo "$RESPONSE" | grep -q "Porcentaje válido"; then
        print_success "Porcentaje válido detectado"
        echo "$RESPONSE" | jq .
    else
        print_error "Validación de porcentaje falló"
        echo "$RESPONSE"
    fi
    
    print_info "Validando: 150 (fuera de rango)"
    RESPONSE=$(curl -s "$BASE_URL/api/test/validate/percentage/150")
    
    if echo "$RESPONSE" | grep -q "fuera de rango"; then
        print_success "Porcentaje fuera de rango detectado correctamente"
        echo "$RESPONSE" | jq .
    else
        print_error "Validación de porcentaje fuera de rango falló"
        echo "$RESPONSE"
    fi
}

# ====================================================================
# TEST 8: ENVIAR CORREO SIMPLE
# ====================================================================

test_send_simple_email() {
    print_header "TEST 8: ENVIAR CORREO SIMPLE"
    
    print_info "Enviando correo a: $TEST_EMAIL"
    print_info "Asunto: Prueba Happy Paws"
    print_info "Cuerpo: Este es un correo de prueba"
    
    RESPONSE=$(curl -s -X POST "$BASE_URL/api/test/email/send" \
        -G \
        --data-urlencode "to=$TEST_EMAIL" \
        --data-urlencode "subject=Prueba Happy Paws" \
        --data-urlencode "body=Este es un correo de prueba")
    
    if echo "$RESPONSE" | grep -q "Correo enviado exitosamente"; then
        print_success "Correo enviado correctamente"
        echo "$RESPONSE"
    else
        print_error "No se pudo enviar el correo"
        echo "$RESPONSE"
    fi
    
    print_info "Revisa tu buzón de correos para confirmar que llegó"
}

# ====================================================================
# TEST 9: ENVIAR CORREO HTML
# ====================================================================

test_send_html_email() {
    print_header "TEST 9: ENVIAR CORREO HTML"
    
    print_info "Enviando correo HTML a: $TEST_EMAIL"
    
    RESPONSE=$(curl -s -X POST "$BASE_URL/api/test/email/send-html?to=$TEST_EMAIL")
    
    if echo "$RESPONSE" | grep -q "Correo HTML enviado exitosamente"; then
        print_success "Correo HTML enviado correctamente"
        echo "$RESPONSE"
    else
        print_error "No se pudo enviar el correo HTML"
        echo "$RESPONSE"
    fi
    
    print_info "Revisa tu buzón para ver el correo formateado"
}

# ====================================================================
# TEST 10: EMAIL INVÁLIDO
# ====================================================================

test_invalid_email() {
    print_header "TEST 10: VALIDAR RECHAZO DE EMAIL INVÁLIDO"
    
    print_info "Intentando enviar a email inválido: email-invalido"
    
    RESPONSE=$(curl -s -X POST "$BASE_URL/api/test/email/send" \
        -G \
        --data-urlencode "to=email-invalido" \
        --data-urlencode "subject=Test" \
        --data-urlencode "body=Test")
    
    if echo "$RESPONSE" | grep -q "no es válido"; then
        print_success "Email inválido rechazado correctamente"
        echo "$RESPONSE"
    else
        print_error "Sistema no validó email inválido correctamente"
        echo "$RESPONSE"
    fi
}

# ====================================================================
# MAIN - EJECUTAR TODOS LOS TESTS
# ====================================================================

main() {
    clear
    
    echo -e "${BLUE}"
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║   HAPPY PAWS - SUITE DE PRUEBAS                           ║"
    echo "║   Validación de Campos y Correos                           ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
    
    print_info "URL Base: $BASE_URL"
    print_info "Email de Prueba: $TEST_EMAIL"
    
    # Verificar conectividad
    print_header "VERIFICANDO CONECTIVIDAD"
    if curl -s "$BASE_URL/api/test/validate/email/test@test.com" > /dev/null 2>&1; then
        print_success "Servidor accesible"
    else
        print_error "No se puede conectar al servidor en $BASE_URL"
        print_info "Verifica que la URL sea correcta y que Render esté en línea"
        exit 1
    fi
    
    # Ejecutar tests
    test_email_validation
    test_phone_validation
    test_dni_validation
    test_ruc_validation
    test_date_validation
    test_number_validation
    test_percentage_validation
    test_send_simple_email
    test_send_html_email
    test_invalid_email
    
    # Resumen final
    print_header "✓ SUITE DE PRUEBAS COMPLETADA"
    echo -e "${GREEN}"
    echo "Si todos los tests pasaron correctamente:"
    echo "✓ La validación de campos está funcionando"
    echo "✓ El servicio de correos está configurado"
    echo "✓ Los recordatorios se enviarán automáticamente"
    echo -e "${NC}"
    
    print_info "Próximos pasos:"
    print_info "1. Crea una cita de prueba en la aplicación"
    print_info "2. Verifica que recibas correo de confirmación"
    print_info "3. Los recordatorios se enviarán automáticamente 24h y 1h antes"
}

# Ejecutar main
main
