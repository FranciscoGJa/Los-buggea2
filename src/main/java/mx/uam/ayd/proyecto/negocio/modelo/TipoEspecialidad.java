package mx.uam.ayd.proyecto.negocio.modelo;

/**
 * Enumeración que representa las especialidades disponibles para los psicólogos.
 *
 * <p>Cada constante incluye una descripción legible que puede ser utilizada
 * en interfaces gráficas, reportes o listados.</p>
 *
 * <ul>
 *   <li>{@link #INFANTIL} — Psicología Infantil.</li>
 *   <li>{@link #MARITAL} — Terapia Marital.</li>
 *   <li>{@link #FAMILIAR} — Terapia Familiar.</li>
 *   <li>{@link #DELAMUJER} — Psicología de la Mujer.</li>
 * </ul>
 *
 * @author Tech Solutions
 * @version 1.0
 */
public enum TipoEspecialidad {
    INFANTIL("Psicologia Infantil"),
    MARITAL("Terapia Marital"),
    FAMILIAR("Terapia Familiar"),
    DELAMUJER("Psicologia de la Mujer");
    
    private final String descripcion;
    
    /**
     * Constructor de la enumeración.
     *
     * @param descripcion descripción legible de la especialidad.
     */
    TipoEspecialidad(String descripcion) {
        this.descripcion = descripcion;
    }
    
    /**
     * Devuelve la descripción legible de la especialidad.
     *
     * @return una cadena con la descripción de la especialidad.
     */
    @Override
    public String toString() {
        return descripcion;
    }
}