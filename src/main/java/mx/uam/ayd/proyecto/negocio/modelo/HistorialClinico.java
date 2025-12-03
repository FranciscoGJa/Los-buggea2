package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Entidad que representa el historial clínico de un paciente.
 *
 * <p>Contiene la información médica y psicológica registrada por un psicólogo,
 * incluyendo observaciones, motivo de consulta, consumo de sustancias, y
 * estado del consentimiento informado.</p>
 *
 * <p>El historial clínico está asociado de manera uno a uno con un {@link Paciente}
 * y puede estar vinculado a un {@link Psicologo} que haya trabajado con el paciente.</p>
 *
 * <p>Esta clase incluye un método para generar una versión formateada en texto
 * de toda la información contenida en el historial.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
@Entity
@Data
public class HistorialClinico {

    @Id
    private Long id; // Mismo ID que el Paciente

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psicologo_id")
    private Psicologo psicologo;
    
    private String observaciones;

    @Temporal(TemporalType.DATE)
    private Date fechaElaboracion;

    private boolean consentimientoAceptado;

    private String motivo;
    private String consumoDrogas;
    private String descripcionDrogas;

    /**
     * Convierte los datos del historial clínico en un formato de texto legible para presentación.
     *
     * <p>Incluye fecha de elaboración, motivo de consulta, consumo de sustancias,
     * observaciones del psicólogo y el estado del consentimiento informado.</p>
     *
     * @return una cadena con la información del historial clínico formateada.
     */
    @Transient
    public String toStringFormateado() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        sb.append("Fecha de Elaboración: ");
        if (fechaElaboracion != null) {
            sb.append(formatter.format(fechaElaboracion));
        } else {
            sb.append("No especificada");
        }
        sb.append("\n\n");

        sb.append("========================================\n");
        sb.append("MOTIVO DE LA CONSULTA\n");
        sb.append("========================================\n");
        sb.append(motivo != null ? motivo : "No especificado.").append("\n\n");

        sb.append("========================================\n");
        sb.append("CONSUMO DE SUSTANCIAS\n");
        sb.append("========================================\n");
        sb.append(consumoDrogas != null ? consumoDrogas : "No especificado.").append("\n");

        if (descripcionDrogas != null && !descripcionDrogas.trim().isEmpty()) {
            sb.append("\nDescripción: \n");
            sb.append(descripcionDrogas).append("\n");
        }
        sb.append("\n");

        sb.append("========================================\n");
        sb.append("OBSERVACIONES DEL PSICÓLOGO\n");
        sb.append("========================================\n");
        sb.append(observaciones != null ? observaciones : "Sin observaciones.").append("\n\n");
        
        sb.append("Consentimiento Informado Aceptado: ").append(consentimientoAceptado ? "Sí" : "No");

        return sb.toString();
    }
}