package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

/**
 * Entidad que representa a un psicólogo en el sistema.
 *
 * <p>Contiene datos personales como nombre, correo electrónico, teléfono
 * y su especialidad. Además, mantiene las relaciones con los pacientes
 * que atiende y con los historiales clínicos que ha registrado o consultado.</p>
 *
 * <p>Esta entidad está mapeada a la base de datos mediante anotaciones JPA
 * y permite persistir la información de los psicólogos junto con sus relaciones.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
@Entity
@Data
public class Psicologo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String correo;
    private String telefono;
    private TipoEspecialidad especialidad;

    // Relación: Psicologo atiende 1 <--> 0...* Paciente
    @OneToMany(mappedBy = "psicologo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Paciente> pacientes;

    // Relación: Psicologo accede 1 <--> 1...* HistorialClinico
    @OneToMany(mappedBy = "psicologo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialClinico> historialesClinico;
}
