package com.Happypaws.demo.model;

/**
 * Estados posibles de una cita, del agendamiento al cierre.
 *
 * Flujo típico:
 *   AGENDADA -> CONFIRMADA -> EN_ESPERA -> EN_CONSULTA -> ATENDIDA
 * con salidas posibles a CANCELADA o NO_ASISTIO en cualquier punto
 * antes de ATENDIDA. No se fuerza esta secuencia a nivel de código:
 * el personal puede saltar pasos (por ejemplo, ir directo de AGENDADA
 * a ATENDIDA), pero sí se controla que nadie mueva una cita para
 * "atrás" una vez que ya se marcó ATENDIDA, CANCELADA o NO_ASISTIO
 * (ver AppointmentService.cambiarEstado).
 */
public enum EstadoCita {

    AGENDADA("Agendada", "#f1c40f"),
    CONFIRMADA("Confirmada", "#3498db"),
    EN_ESPERA("En espera", "#e67e22"),
    EN_CONSULTA("En consulta", "#9b59b6"),
    ATENDIDA("Atendida", "#2ecc71"),
    CANCELADA("Cancelada", "#e74c3c"),
    NO_ASISTIO("No asistió", "#2c3e50");

    private final String etiqueta;
    private final String colorHex;

    EstadoCita(String etiqueta, String colorHex) {
        this.etiqueta = etiqueta;
        this.colorHex = colorHex;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getColorHex() {
        return colorHex;
    }

    /** Estados desde los que ya no tiene sentido volver a "mover" la cita. */
    public boolean esFinal() {
        return this == ATENDIDA || this == CANCELADA || this == NO_ASISTIO;
    }
}
