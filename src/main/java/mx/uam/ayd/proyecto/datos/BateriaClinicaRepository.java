package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.BateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.negocio.modelo.TipoBateria;

import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar operaciones de persistencia sobre la entidad {@link BateriaClinica}.
 * 
 * <p>Proporciona métodos para consultar baterías clínicas asociadas a pacientes específicos,
 * así como búsquedas filtradas por tipo de batería.</p>
 * 
 * <p>Extiende {@link CrudRepository}, por lo que también hereda operaciones CRUD estándar.</p>
 * 
 * @author Tech Solutions
 * @version 1.0
 */
public interface BateriaClinicaRepository extends CrudRepository<BateriaClinica, Integer> {

    /**
     * Recupera todas las baterías clínicas asociadas a un paciente.
     *
     * @param paciente el paciente del cual se desean obtener las baterías clínicas.
     *                 No debe ser {@code null}.
     * @return una lista con las baterías clínicas encontradas.
     *         Si el paciente no tiene baterías registradas, la lista estará vacía.
     */
    public List<BateriaClinica> findByPaciente(Paciente paciente);

    /**
     * Busca una batería clínica asociada a un paciente y a un tipo específico de batería.
     *
     * @param paciente el paciente propietario de la batería clínica. No debe ser {@code null}.
     * @param tipoBateria el tipo de batería clínica a buscar. No debe ser {@code null}.
     * @return un {@link Optional} que contiene la batería clínica si existe,
     *         o vacío si no se encontró ninguna que coincida con los criterios.
     */
    public Optional<BateriaClinica> findByPacienteAndTipoDeBateria(Paciente paciente, TipoBateria tipoBateria);
}