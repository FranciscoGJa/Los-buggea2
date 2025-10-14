package mx.uam.ayd.proyecto.negocio.modelo;

/**
 * Enumeración que representa los diferentes estados de confirmación
 * que puede tener una cita.
 *
 * <p>Cada estado incluye una descripción legible para facilitar su uso
 * en interfaces de usuario y reportes.</p>
 *
 * <ul>
 *   <li>{@link #PENDIENTE} — La cita está programada pero aún no confirmada.</li>
 *   <li>{@link #CONFIRMADA} — La cita ha sido confirmada por las partes.</li>
 *   <li>{@link #CANCELADA} — La cita fue cancelada.</li>
 *   <li>{@link #CONCLUIDA} — La cita ya se llevó a cabo.</li>
 * </ul>
 *
 * @author Tech Solutions
 * @version 1.0
 */
public enum TipoConfirmacionCita {
    PENDIENTE("Pendiente"),
    CONFIRMADA("Confirmada"),
    CANCELADA("Cancelada"),
    CONCLUIDA("Concluida");

    private final String descripcion;

    /**
     * Constructor de la enumeración.
     *
     * @param descripcion descripción legible del estado de la cita.
     */
    TipoConfirmacionCita(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Devuelve la descripción legible del estado de la cita.
     *
     * @return una cadena con la descripción del estado.
     */
    @Override
    public String toString() {
        return descripcion;
    }
}