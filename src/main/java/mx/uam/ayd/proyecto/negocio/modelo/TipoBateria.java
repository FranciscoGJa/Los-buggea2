package mx.uam.ayd.proyecto.negocio.modelo;

/**
 * Enumeración que representa los diferentes tipos de baterías clínicas
 * que pueden ser aplicadas a un paciente.
 *
 * <p>Cada constante incluye una descripción legible para su representación
 * en interfaces de usuario o reportes.</p>
 *
 * <ul>
 *   <li>{@link #CEPER} — Cuestionario CEPER.</li>
 *   <li>{@link #BDI_II} — Inventario de Depresión de Beck (BDI-II).</li>
 *   <li>{@link #BAI} — Inventario de Ansiedad de Beck (BAI).</li>
 * </ul>
 *
 * @author Tech Solutions
 * @version 1.0
 */
public enum TipoBateria {
    CEPER("CEPER"),
    BDI_II("BDI-II"),
    BAI("BAI");

    private final String descripcion;

    /**
     * Constructor de la enumeración.
     *
     * @param descripcion descripción legible del tipo de batería.
     */
    TipoBateria(String descripcion){
        this.descripcion = descripcion;
    }

    /**
     * Devuelve la descripción legible del tipo de batería.
     *
     * @return una cadena con la descripción del tipo de batería.
     */
    @Override
    public String toString() {
        return descripcion;
    }
}