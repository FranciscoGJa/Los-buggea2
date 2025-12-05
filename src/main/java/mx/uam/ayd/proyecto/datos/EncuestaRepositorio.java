package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Encuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Encuesta.
 * 
 * <p>Proporciona operaciones CRUD estándar y consultas personalizadas
 * para acceder a las encuestas de satisfacción del paciente en la base de datos.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
@Repository
public interface EncuestaRepositorio extends JpaRepository<Encuesta, Integer> {

    /**
     * Encuentra todas las encuestas de un paciente específico.
     *
     * @param idPaciente el ID del paciente
     * @return lista de encuestas del paciente
     */
    List<Encuesta> findByIdPaciente(Long idPaciente);

    /**
     * Encuentra todas las encuestas de un psicólogo específico.
     *
     * @param idPsicologo el ID del psicólogo
     * @return lista de encuestas del psicólogo
     */
    List<Encuesta> findByIdPsicologo(int idPsicologo);

    /**
     * Encuentra encuestas dentro de un rango de fechas.
     *
     * @param fechaInicio fecha inicial
     * @param fechaFin fecha final
     * @return lista de encuestas en el rango de fechas
     */
    @Query("SELECT e FROM Encuesta e WHERE e.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Encuesta> findByFechaRange(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    /**
     * Encuentra encuestas por calificación.
     *
     * @param calificacion la calificación (1-5)
     * @return lista de encuestas con la calificación especificada
     */
    List<Encuesta> findByCalificacion(int calificacion);

    /**
     * Encuentra encuestas de un paciente específico por psicólogo.
     *
     * @param idPaciente el ID del paciente
     * @param idPsicologo el ID del psicólogo
     * @return lista de encuestas
     */
    @Query("SELECT e FROM Encuesta e WHERE e.idPaciente = :idPaciente AND e.idPsicologo = :idPsicologo")
    List<Encuesta> findByPacienteAndPsicologo(@Param("idPaciente") Long idPaciente, @Param("idPsicologo") int idPsicologo);

    /**
     * Encuentra encuestas con calificación mayor o igual a la especificada.
     *
     * @param calificacion la calificación mínima
     * @return lista de encuestas
     */
    @Query("SELECT e FROM Encuesta e WHERE e.calificacion >= :calificacion ORDER BY e.calificacion DESC")
    List<Encuesta> findByCalificacionGreaterThanEqual(@Param("calificacion") int calificacion);

    /**
     * Cuenta el total de encuestas de un paciente.
     *
     * @param idPaciente el ID del paciente
     * @return cantidad de encuestas
     */
    long countByIdPaciente(Long idPaciente);

    /**
     * Encuentra la encuesta más reciente de un paciente.
     *
     * @param idPaciente el ID del paciente
     * @return la encuesta más reciente
     */
    @Query("SELECT e FROM Encuesta e WHERE e.idPaciente = :idPaciente ORDER BY e.fecha DESC LIMIT 1")
    Optional<Encuesta> findLatestByIdPaciente(@Param("idPaciente") Long idPaciente);
}
