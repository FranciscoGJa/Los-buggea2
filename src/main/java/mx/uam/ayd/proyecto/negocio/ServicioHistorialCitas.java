package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.CitaRepository;
import mx.uam.ayd.proyecto.datos.RepositoryPerfilCitas;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * Servicio para gestionar el historial de citas de los perfiles de citas.
 * Proporciona métodos para obtener, filtrar y verificar citas asociadas a un perfil.
 * Utiliza repositorios para acceder a los datos de perfiles y citas.
 */
@Service
public class ServicioHistorialCitas {
    
    @Autowired
    private RepositoryPerfilCitas perfilCitasRepository;
    
    @Autowired
    private CitaRepository citaRepository;
    
    @Autowired
    private ServicioCita servicioCita;
    
    /**
     * Obtiene el historial completo de citas de un perfil
     */
    @Transactional
    public List<Cita> obtenerHistorialPorPerfil(Long perfilId) {
        // Usar el repositorio directo de citas para evitar problemas de lazy loading
        return citaRepository.findByPerfilCitasIdPerfil(perfilId);
    }
    
    /**
     * Obtiene un perfil con todas sus citas - CORREGIDO
     */
    @Transactional
    public Optional<PerfilCitas> obtenerPerfilConHistorial(Long perfilId) {
        Optional<PerfilCitas> perfilOpt = perfilCitasRepository.findById(perfilId);
        perfilOpt.ifPresent(perfil -> {
            // Inicializar la colección lazy
            perfil.getCitas().size();
        });
        return perfilOpt;
    }
    
    /**
     * Obtiene citas usando el repositorio directamente
     */
    public List<Cita> obtenerCitasPorPerfilId(Long perfilId) {
        return citaRepository.findByPerfilCitasIdPerfil(perfilId);
    }
    
    /**
     * Obtiene citas por estado para un perfil específico
     */
    public List<Cita> obtenerCitasPorEstado(Long perfilId, String estado) {
        return citaRepository.findByPerfilCitasIdPerfilAndEstadoCita(perfilId, 
            mx.uam.ayd.proyecto.negocio.modelo.TipoConfirmacionCita.valueOf(estado));
    }
    
    /**
     * Verifica si un perfil tiene citas registradas
     */
    public boolean tieneCitasRegistradas(Long perfilId) {
        List<Cita> citas = citaRepository.findByPerfilCitasIdPerfil(perfilId);
        return !citas.isEmpty();
    }
    
    /**
     * Obtiene la última cita de un perfil
     */
    public Optional<Cita> obtenerUltimaCita(Long perfilId) {
        List<Cita> citas = citaRepository.findByPerfilCitasIdPerfil(perfilId);
        return citas.stream()
            .max((c1, c2) -> c1.getFechaCita().compareTo(c2.getFechaCita()));
    }
}