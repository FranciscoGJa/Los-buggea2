// RepositoryPerfilCitas.java
package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface RepositoryPerfilCitas extends CrudRepository<PerfilCitas, Long> {
    
    /**
     * Busca perfiles por nombre completo (búsqueda parcial case insensitive)
     */
    List<PerfilCitas> findByNombreCompletoContainingIgnoreCase(String nombreCompleto);
    
    /**
     * Busca perfiles por teléfono
     */
    List<PerfilCitas> findByTelefono(String telefono);
    
    /**
     * Verifica si existe un perfil con el nombre completo exacto
     */
    boolean existsByNombreCompleto(String nombreCompleto);
    
    /**
     * Encuentra todos los perfiles ordenados por fecha de creación descendente
     */
    List<PerfilCitas> findAllByOrderByFechaCreacionDesc();
    
    /**
     * Busca un perfil por nombre completo exacto
     */
    Optional<PerfilCitas> findByNombreCompleto(String nombreCompleto);
}