package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.CitaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioCalendario {
    
    @Autowired
    private CitaRepository citaRepository;
    
    /**
     * Verifica la disponibilidad de un psicólogo en fecha y hora específicas
     */
    public boolean verificarDisponibilidad(Integer psicologoId, LocalDate fecha, LocalTime hora) {
        List<Cita> citasExistentes = citaRepository.findByPsicologoIdAndFechaCitaAndHoraCita(
            psicologoId, fecha, hora);
        return citasExistentes.isEmpty();
    }
    
    /**
     * Obtiene horarios disponibles para un psicólogo en una fecha específica
     */
    public List<LocalTime> obtenerHorariosDisponibles(Integer psicologoId, LocalDate fecha) {
        List<LocalTime> horariosOcupados = new ArrayList<>();
        List<Cita> citasDelDia = citaRepository.findByPsicologoIdAndFechaCita(psicologoId, fecha);
        
        for (Cita cita : citasDelDia) {
            horariosOcupados.add(cita.getHoraCita());
        }
        
        // Generar horarios de trabajo (9:00 - 18:00)
        List<LocalTime> horariosDisponibles = new ArrayList<>();
        for (int hora = 9; hora <= 18; hora++) {
            LocalTime horario = LocalTime.of(hora, 0);
            if (!horariosOcupados.contains(horario)) {
                horariosDisponibles.add(horario);
            }
        }
        
        return horariosDisponibles;
    }
    
    /**
    * Obtiene todas las citas asignadas a un psicólogo
    */
    public List<Cita> obtenerCitasPorPsicologoYFecha(Integer psicologoId, LocalDate fecha) {
    return citaRepository.findByPsicologoIdAndFechaCita(psicologoId, fecha);
    }


}