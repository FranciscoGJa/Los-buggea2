package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.HistorialClinico;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar operaciones de persistencia sobre la entidad {@link HistorialClinico}.
 *
 * <p>Permite realizar búsquedas de historiales clínicos por paciente, psicólogo y estado de consentimiento,
 * además de heredar las operaciones CRUD estándar de {@link org.springframework.data.repository.CrudRepository CrudRepository}.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
public interface HistorialClinicoRepository extends CrudRepository<HistorialClinico, Long> {

    /**
     * Encuentra el historial clínico asociado a un paciente específico.
     *
     * @param paciente el paciente del cual se busca el historial; no debe ser {@code null}.
     * @return un {@link Optional} que contiene el historial clínico si existe, o vacío si no se encuentra.
     */
    Optional<HistorialClinico> findByPaciente(Paciente paciente);

    // Nuevos métodos basados en las relaciones establecidas:

    /**
     * Encuentra todos los historiales clínicos accedidos por un psicólogo específico.
     *
     * @param psicologo el psicólogo del cual se buscan los historiales; no debe ser {@code null}.
     * @return una lista de historiales clínicos; si no hay coincidencias, la lista estará vacía.
     */
    List<HistorialClinico> findByPsicologo(Psicologo psicologo);

    /**
     * Encuentra historiales clínicos filtrados por el estado de consentimiento y psicólogo asignado.
     *
     * @param consentimientoAceptado el estado del consentimiento (true si fue aceptado).
     * @param psicologo el psicólogo asignado; no debe ser {@code null}.
     * @return una lista de historiales clínicos que cumplen con los criterios; si no hay coincidencias, la lista estará vacía.
     */
    List<HistorialClinico> findByConsentimientoAceptadoAndPsicologo(boolean consentimientoAceptado, Psicologo psicologo);
}