package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface RepositoryPerfilCitas extends CrudRepository<PerfilCitas, Long> {
    
    List<PerfilCitas> findByNombreCompletoContainingIgnoreCase(String nombreCompleto);
    
    List<PerfilCitas> findByTelefono(String telefono);
    
    boolean existsByNombreCompleto(String nombreCompleto);
    
    List<PerfilCitas> findAllByOrderByFechaCreacionDesc();
    
    Optional<PerfilCitas> findByNombreCompleto(String nombreCompleto);
    
    // Nuevos métodos para relaciones
    List<PerfilCitas> findByPaciente(Paciente paciente);
    
    List<PerfilCitas> findByPsicologo(Psicologo psicologo);
    
    Optional<PerfilCitas> findByPacienteAndPsicologo(Paciente paciente, Psicologo psicologo);
    
    List<PerfilCitas> findByPsicologoId(Integer psicologoId);
    
    boolean existsByPaciente(Paciente paciente);
    
    // Método para buscar por paciente ID
    List<PerfilCitas> findByPacienteId(Long pacienteId);
}