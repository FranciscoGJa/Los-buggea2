package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

/**
 * Entidad que representa una batería clínica aplicada a un paciente.
 *
 * <p>Incluye las respuestas a preguntas específicas, la fecha de aplicación,
 * la calificación obtenida, el tipo de batería, comentarios adicionales
 * y la relación con el paciente al que fue aplicada.</p>
 *
 * <p>Esta entidad es administrada por JPA y mapeada a una tabla en la base de datos.
 * Utiliza anotaciones de {@link jakarta.persistence} para definir su configuración de persistencia.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
@Entity
@Data
public class BateriaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer respuesta1;
    private Integer respuesta2;
    private Integer respuesta3;
    private Integer respuesta4;
    private Integer respuesta5;

    /*
        El decorador indica a JPA que debe mapear un campo de fecha/hora
        a un tipo que sea compatible con SQL
    */
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAplicacion;
    
    private int calificacion;
    
    @Enumerated(EnumType.STRING)
    private TipoBateria tipoDeBateria;
    
    private String comentarios;

    // Relación: Paciente llena 1 <--> 1...3 BateriasClinica
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;


}