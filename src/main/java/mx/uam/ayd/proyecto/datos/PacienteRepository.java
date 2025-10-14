package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

/**
 * Repositorio para gestionar operaciones de persistencia sobre la entidad {@link Paciente}.
 *
 * <p>Proporciona métodos para buscar pacientes por correo electrónico o por rango de edad,
 * además de heredar las operaciones CRUD estándar de {@link org.springframework.data.repository.CrudRepository CrudRepository}.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
public interface PacienteRepository extends CrudRepository<Paciente, Long> {

    /**
     * Encuentra un paciente por su correo electrónico.
     *
     * @param correo el correo a buscar; no debe ser {@code null}.
     * @return el paciente encontrado o {@code null} si no existe uno con ese correo.
     */
    Paciente findByCorreo(String correo);

    /**
     * Encuentra pacientes dentro de un rango de edad específico.
     *
     * @param edad1 edad mínima (inclusive).
     * @param edad2 edad máxima (inclusive).
     * @return una lista de pacientes en el rango de edad; si no hay coincidencias, la lista estará vacía.
     */
    List<Paciente> findByEdadBetween(int edad1, int edad2);
}