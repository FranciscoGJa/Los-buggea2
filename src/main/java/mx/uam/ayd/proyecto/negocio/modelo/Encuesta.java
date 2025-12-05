package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

/**
 * Entidad que representa una encuesta de satisfacción del paciente.
 *
 * <p>Contiene la información de la encuesta incluyendo datos del paciente,
 * psicólogo, fecha de la consulta, calificación y comentarios del paciente.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Encuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encuestas")
    private int idEncuestas;

    @Column(name = "id_paciente")
    private Long idPaciente;

    @Column(name = "id_psicologo")
    private int idPsicologo;

    @Column(name = "nombre_paciente")
    private String nombrePaciente;

    @Column(name = "nombre_psicologo")
    private String nombrePsicologo;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "calificacion")
    private int calificacion;

    @Column(name = "comentarios", length = 1000)
    private String comentarios;

    // Relaciones opcionales con las entidades
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", insertable = false, updatable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_psicologo", insertable = false, updatable = false)
    private Psicologo psicologo;

    List<Encuesta> obtenerEncuestasPorPsicologo(int idPsicologo);
    
}
