package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;
import mx.uam.ayd.proyecto.negocio.modelo.TipoEspecialidad;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

/**
 * Repositorio para gestionar operaciones de persistencia sobre la entidad {@link Psicologo}.
 *
 * <p>Proporciona métodos de búsqueda por teléfono, correo electrónico y especialidad,
 * así como consultas sobre psicólogos con pacientes o historiales clínicos asignados.
 * Extiende {@link org.springframework.data.repository.CrudRepository CrudRepository},
 * heredando operaciones CRUD estándar.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
public interface PsicologoRepository extends CrudRepository<Psicologo, Integer> {

    /**
     * Encuentra un psicólogo por su número de teléfono.
     *
     * @param telefono el teléfono a buscar; no debe ser {@code null}.
     * @return el psicólogo encontrado o {@code null} si no existe uno con ese teléfono.
     */
    Psicologo findByTelefono(String telefono);


    /**
     * Encuentra todos los psicólogos de una especialidad específica.
     *
     * @param especialidad la especialidad a buscar; no debe ser {@code null}.
     * @return una lista de psicólogos con la especialidad indicada; si no hay coincidencias, la lista estará vacía.
     */
    List<Psicologo> findByEspecialidad(TipoEspecialidad especialidad);

    /**
     * Encuentra todos los psicólogos que no tienen la especialidad indicada.
     *
     * @param especialidad la especialidad a excluir; no debe ser {@code null}.
     * @return una lista de psicólogos que no son de la especialidad indicada; si no hay coincidencias, la lista estará vacía.
     */
    List<Psicologo> findByEspecialidadNot(TipoEspecialidad especialidad);

    /**
     * Encuentra un psicólogo por su correo electrónico.
     *
     * @param correo el correo a buscar; no debe ser {@code null}.
     * @return el psicólogo encontrado o {@code null} si no existe uno con ese correo.
     */
    Psicologo findByCorreo(String correo);

    Psicologo findByUsuario(String usuario);



    /**
     * Encuentra todos los psicólogos que tienen pacientes asignados.
     *
     * @return una lista de psicólogos con pacientes; si no hay coincidencias, la lista estará vacía.
     */
    List<Psicologo> findByPacientesIsNotEmpty();

    /**
     * Encuentra todos los psicólogos que han accedido a historiales clínicos.
     *
     * @return una lista de psicólogos con historiales clínicos; si no hay coincidencias, la lista estará vacía.
     */
    List<Psicologo> findByHistorialesClinicoIsNotEmpty();
}