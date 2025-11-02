// ServicioPerfilCitas.java - CORREGIDO
package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;
import mx.uam.ayd.proyecto.datos.RepositoryPerfilCitas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioPerfilCitas {
    
    @Autowired
    private RepositoryPerfilCitas repositoryPerfilCitas;
    
    /**
     * Crea un nuevo perfil de citas
     */
    public PerfilCitas crearPerfilCitas(String nombreCompleto, int edad, String sexo, 
                                       String direccion, String ocupacion, 
                                       String telefono, String email) {
        
        // Validar que no exista un perfil con el mismo nombre
        if (repositoryPerfilCitas.existsByNombreCompleto(nombreCompleto)) {
            throw new IllegalArgumentException("Ya existe un perfil con el nombre: " + nombreCompleto);
        }
        
        // Validar edad
        if (edad < 1 || edad > 120) {
            throw new IllegalArgumentException("La edad debe estar entre 1 y 120 años");
        }
        
        // Validar campos obligatorios
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio");
        }
        
        if (sexo == null || sexo.trim().isEmpty()) {
            throw new IllegalArgumentException("El sexo es obligatorio");
        }
        
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección es obligatoria");
        }
        
        if (ocupacion == null || ocupacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La ocupación es obligatoria");
        }
        
        // Crear y guardar el nuevo perfil
        PerfilCitas perfil = new PerfilCitas(nombreCompleto.trim(), edad, sexo.trim(), direccion.trim(), ocupacion.trim());
        
        if (telefono != null && !telefono.trim().isEmpty()) {
            perfil.setTelefono(telefono.trim());
        }
        
        if (email != null && !email.trim().isEmpty()) {
            perfil.setEmail(email.trim());
        }
        
        return repositoryPerfilCitas.save(perfil);
    }
    
    /**
     * Obtiene todos los perfiles de citas
     */
    public List<PerfilCitas> obtenerTodosLosPerfiles() {
        return repositoryPerfilCitas.findAllByOrderByFechaCreacionDesc();
    }
    
    /**
     * Busca perfiles por nombre (búsqueda parcial)
     */
    public List<PerfilCitas> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return obtenerTodosLosPerfiles();
        }
        return repositoryPerfilCitas.findByNombreCompletoContainingIgnoreCase(nombre.trim());
    }
    
    /**
     * Busca perfiles por teléfono
     */
    public List<PerfilCitas> buscarPorTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return obtenerTodosLosPerfiles();
        }
        return repositoryPerfilCitas.findByTelefono(telefono.trim());
    }
    
    /**
     * Busca perfiles por nombre o teléfono - CORREGIDO
     */
    public List<PerfilCitas> buscarPorNombreOTelefono(String nombre, String telefono) {
        boolean tieneNombre = nombre != null && !nombre.trim().isEmpty();
        boolean tieneTelefono = telefono != null && !telefono.trim().isEmpty();
        
        if (tieneNombre && tieneTelefono) {
            // Buscar por ambos criterios
            List<PerfilCitas> resultadosPorNombre = buscarPorNombre(nombre);
            List<PerfilCitas> resultadosPorTelefono = buscarPorTelefono(telefono);
            
            // Combinar evitando duplicados
            for (PerfilCitas perfil : resultadosPorTelefono) {
                if (!resultadosPorNombre.contains(perfil)) {
                    resultadosPorNombre.add(perfil);
                }
            }
            return resultadosPorNombre;
        } else if (tieneNombre) {
            return buscarPorNombre(nombre);
        } else if (tieneTelefono) {
            return buscarPorTelefono(telefono);
        } else {
            return obtenerTodosLosPerfiles();
        }
    }
    
    /**
     * Obtiene un perfil por su ID
     */
    public Optional<PerfilCitas> obtenerPerfilPorId(Long id) {
        return repositoryPerfilCitas.findById(id);
    }
    
    /**
     * Obtiene un perfil por nombre exacto
     */
    public Optional<PerfilCitas> obtenerPerfilPorNombreExacto(String nombreCompleto) {
        return repositoryPerfilCitas.findByNombreCompleto(nombreCompleto);
    }
    
    /**
     * Actualiza un perfil existente
     */
    public PerfilCitas actualizarPerfil(Long id, String nombreCompleto, int edad, String sexo, 
                                       String direccion, String ocupacion, 
                                       String telefono, String email) {
        
        Optional<PerfilCitas> perfilOpt = repositoryPerfilCitas.findById(id);
        if (perfilOpt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el perfil con ID: " + id);
        }
        
        PerfilCitas perfil = perfilOpt.get();
        
        // Validar edad
        if (edad < 1 || edad > 120) {
            throw new IllegalArgumentException("La edad debe estar entre 1 y 120 años");
        }
        
        // Actualizar campos
        perfil.setNombreCompleto(nombreCompleto.trim());
        perfil.setEdad(edad);
        perfil.setSexo(sexo.trim());
        perfil.setDireccion(direccion.trim());
        perfil.setOcupacion(ocupacion.trim());
        perfil.setTelefono(telefono != null ? telefono.trim() : null);
        perfil.setEmail(email != null ? email.trim() : null);
        
        return repositoryPerfilCitas.save(perfil);
    }
    
    /**
     * Elimina un perfil por su ID
     */
    public boolean eliminarPerfil(Long id) {
        if (repositoryPerfilCitas.existsById(id)) {
            repositoryPerfilCitas.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Verifica si existe un perfil con el nombre dado
     */
    public boolean existePerfilConNombre(String nombreCompleto) {
        return repositoryPerfilCitas.existsByNombreCompleto(nombreCompleto);
    }
    
    /**
     * Cuenta el total de perfiles registrados
     */
    public long contarPerfiles() {
        return repositoryPerfilCitas.count();
    }
}