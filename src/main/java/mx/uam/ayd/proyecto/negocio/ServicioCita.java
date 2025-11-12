package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.CitaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioCita {
    
    @Autowired
    private CitaRepository citaRepository;
    
    @Autowired
    private ServicioPerfilCitas servicioPerfilCitas;
    
    /**
     * Crea una nueva cita para un perfil existente
     */
    public Cita crearCita(Long perfilCitasId, Integer psicologoId, 
                         LocalDate fechaCita, LocalTime horaCita, 
                         String detallesPaciente) {
        
        Optional<PerfilCitas> perfilOpt = servicioPerfilCitas.obtenerPerfilPorId(perfilCitasId);
        if (perfilOpt.isEmpty()) {
            throw new IllegalArgumentException("Perfil de citas no encontrado");
        }
        
        PerfilCitas perfil = perfilOpt.get();
        
        // Verificar que la fecha no sea en el pasado
        if (fechaCita.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se pueden agendar citas en fechas pasadas");
        }
        
        // Verificar disponibilidad (implementación básica)
        if (existeCitaEnFechaYHora(psicologoId, fechaCita, horaCita)) {
            throw new IllegalArgumentException("El psicólogo ya tiene una cita en esa fecha y hora");
        }
        
        Cita cita = new Cita();
        cita.setPerfilCitas(perfil);
        cita.setPsicologo(perfil.getPsicologo());
        cita.setFechaCita(fechaCita);
        cita.setHoraCita(horaCita);
        cita.setDetallesAdicionalesPaciente(detallesPaciente);
        cita.setEstadoCita(TipoConfirmacionCita.PENDIENTE);
        
        // Agregar la cita al perfil
        perfil.agregarCita(cita);
        
        return citaRepository.save(cita);
    }
    
    /**
     * Crea un perfil de citas para un paciente existente y agenda una cita
     */
    public Cita crearPerfilYCita(Paciente paciente, Psicologo psicologo, 
                                String direccion, String ocupacion,
                                LocalDate fechaCita, LocalTime horaCita, 
                                String detallesPaciente) {
        
        // Crear perfil de citas para el paciente
        PerfilCitas perfil = servicioPerfilCitas.crearPerfilDesdePaciente(
            paciente, psicologo, direccion, ocupacion);
        
        // Crear la cita
        return crearCita(perfil.getIdPerfil(), psicologo.getId(), fechaCita, horaCita, detallesPaciente);
    }
    
    /**
     * Crea un perfil de citas para un paciente nuevo (sin registro previo) y agenda cita
     */
    public Cita crearPerfilCompletoYCita(String nombreCompleto, int edad, String sexo, 
                                       String direccion, String ocupacion, 
                                       String telefono, String email,
                                       Psicologo psicologo,
                                       LocalDate fechaCita, LocalTime horaCita, 
                                       String detallesPaciente) {
        
        // Crear perfil de citas completo
        PerfilCitas perfil = servicioPerfilCitas.crearPerfilCitas(
            nombreCompleto, edad, sexo, direccion, ocupacion, telefono, email, psicologo);
        
        // Crear la cita
        return crearCita(perfil.getIdPerfil(), psicologo.getId(), fechaCita, horaCita, detallesPaciente);
    }
    
    /**
     * Verifica si existe una cita para un psicólogo en fecha y hora específicas
     */
    private boolean existeCitaEnFechaYHora(Integer psicologoId, LocalDate fecha, LocalTime hora) {
        List<Cita> citas = citaRepository.findByPsicologoIdAndFechaCitaAndHoraCita(
            psicologoId, fecha, hora);
        return !citas.isEmpty();
    }
    
    /**
     * Obtiene todas las citas de un perfil
     */
    public List<Cita> obtenerCitasPorPerfil(Long perfilCitasId) {
        return citaRepository.findByPerfilCitasIdPerfil(perfilCitasId);
    }
    
    /**
     * Obtiene citas por psicólogo
     */
    public List<Cita> obtenerCitasPorPsicologo(Integer psicologoId) {
        return citaRepository.findByPsicologoId(psicologoId);
    }
    
    /**
     * Obtiene citas por fecha
     */
    public List<Cita> obtenerCitasPorFecha(LocalDate fecha) {
        return citaRepository.findByFechaCita(fecha);
    }
    
    /**
     * Cambia el estado de una cita
     */
    public Cita cambiarEstadoCita(Integer citaId, TipoConfirmacionCita nuevoEstado, String motivo) {
        Optional<Cita> citaOpt = citaRepository.findById(citaId);
        if (citaOpt.isEmpty()) {
            throw new IllegalArgumentException("Cita no encontrada");
        }
        
        Cita cita = citaOpt.get();
        cita.setEstadoCita(nuevoEstado);
        
        if (nuevoEstado == TipoConfirmacionCita.CANCELADA && motivo != null) {
            cita.setMotivoCancelacion(motivo);
        }
        
        return citaRepository.save(cita);
    }
    
    /**
     * Agrega nota post-sesión a una cita concluida
     */
    public Cita agregarNotaSesion(Integer citaId, String notaPostSesion) {
        Optional<Cita> citaOpt = citaRepository.findById(citaId);
        if (citaOpt.isEmpty()) {
            throw new IllegalArgumentException("Cita no encontrada");
        }
        
        Cita cita = citaOpt.get();
        if (cita.getEstadoCita() != TipoConfirmacionCita.CONCLUIDA) {
            throw new IllegalArgumentException("Solo se pueden agregar notas a citas concluidas");
        }
        
        cita.setNotaPostSesion(notaPostSesion);
        return citaRepository.save(cita);
    }
}