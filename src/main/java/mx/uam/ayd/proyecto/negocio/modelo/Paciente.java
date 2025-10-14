package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList; 
import java.util.List;

/**
 * Entidad que representa a un paciente dentro del sistema.
 *
 * <p>Contiene la información personal del paciente como nombre, teléfono,
 * correo y edad, así como sus relaciones con el psicólogo asignado,
 * su historial clínico, las baterías clínicas que ha respondido
 * y sus citas programadas.</p>
 *
 * <p>Incluye un constructor por defecto que inicializa las listas
 * de baterías clínicas y citas para evitar valores nulos.</p>
 *
 * <p>Esta entidad está mapeada a una tabla en la base de datos
 * mediante anotaciones de JPA.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
@Entity
@Data
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String telefono;
    private String correo;
    private int edad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psicologo_id")
    private Psicologo psicologo;

    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private HistorialClinico historialClinico;

    @OneToMany(mappedBy = "paciente", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<BateriaClinica> bateriasClinicas;

    @OneToMany(mappedBy = "paciente", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Cita> citas;

    /**
     * Constructor por defecto que inicializa las listas
     * de baterías clínicas y citas como listas vacías.
     */
    public Paciente(){
        this.bateriasClinicas = new ArrayList<>();
        this.citas = new ArrayList<>();
    }
}