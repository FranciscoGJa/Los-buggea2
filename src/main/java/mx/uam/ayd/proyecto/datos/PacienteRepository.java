package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    /**
     * Busca un paciente por ID y carga INMEDIATAMENTE sus relaciones
     * (Baterías e Historial) para evitar problemas de carga perezosa.
     * * @param id el identificador del paciente.
     * @return un Optional con el paciente completo si existe.
     */
    @Query("SELECT p FROM Paciente p " +
           "LEFT JOIN FETCH p.bateriasClinicas " +
           "LEFT JOIN FETCH p.historialClinico " +
           "WHERE p.id = :id")
    Optional<Paciente> findByIdWithRelations(@Param("id") Long id);
}