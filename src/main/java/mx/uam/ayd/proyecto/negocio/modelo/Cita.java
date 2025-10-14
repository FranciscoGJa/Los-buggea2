package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

/**
 * Entidad que representa una cita agendada entre un paciente y un psicólogo.
 *
 * <p>Incluye la fecha y hora de la cita, el estado de confirmación, detalles adicionales,
 * notas posteriores a la sesión, y el motivo de cancelación en caso de aplicar.</p>
 *
 * <p>Está relacionada con un {@link Paciente} y utiliza anotaciones de JPA
 * para su mapeo a la base de datos.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
@Entity
@Data
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Temporal(TemporalType.DATE)
    private Date fechaCita;

    @Temporal(TemporalType.TIME)
    private Date horaCita;

    @Enumerated(EnumType.STRING)
    private TipoConfirmacionCita estadoCita;
    
    private String detallesAdicionalesPsicologo;
    private String detallesAdicionalesPaciente;
    private String notaPostSesion;
    private String motivoCancelacion;

    // Relación con Paciente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;
}