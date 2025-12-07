package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.negocio.modelo.TipoConfirmacionCita;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends CrudRepository<Cita, Integer> {

    // Métodos existentes (mantener compatibilidad)
    List<Cita> findByPaciente(Paciente paciente);
    List<Cita> findByEstadoCita(TipoConfirmacionCita estadoCita);
    List<Cita> findByFechaCita(LocalDate fechaCita);
    List<Cita> findByPacienteAndEstadoCita(Paciente paciente, TipoConfirmacionCita estadoCita);
    
    // Nuevos métodos para el sistema integrado con PerfilCitas
    List<Cita> findByPerfilCitasIdPerfil(Long perfilCitasId);
    List<Cita> findByPsicologoId(Integer psicologoId);
    List<Cita> findByPsicologoIdAndFechaCita(Integer psicologoId, LocalDate fechaCita);//este 
    
    List<Cita> findByPsicologoIdAndFechaCitaAndHoraCita(Integer psicologoId, LocalDate fechaCita, LocalTime horaCita);
    List<Cita> findByPerfilCitasIdPerfilAndEstadoCita(Long perfilCitasId, TipoConfirmacionCita estadoCita);
    List<Cita> findByPsicologoAndEstadoCita(Psicologo psicologo, TipoConfirmacionCita estadoCita);


    // Método para buscar citas por paciente y psicólogo
    List<Cita> findByPacienteAndPsicologo(Paciente paciente, Psicologo psicologo);
    /**
     * Busca citas por fecha cargando TODAS las relaciones necesarias
     */
    @Query("SELECT c FROM Cita c " +
           "LEFT JOIN FETCH c.perfilCitas " +
           "LEFT JOIN FETCH c.psicologo " +
           "WHERE c.fechaCita = :fecha")
    List<Cita> findByFechaCitaWithRelations(@Param("fecha") LocalDate fecha);
    
    /**
     * Busca una cita por ID cargando TODAS las relaciones
     */
    @Query("SELECT c FROM Cita c " +
           "LEFT JOIN FETCH c.perfilCitas " +
           "LEFT JOIN FETCH c.psicologo " +
           "WHERE c.id = :id")
    Cita findByIdWithRelations(@Param("id") int id);

     @Query("""
       SELECT c FROM Cita c
       LEFT JOIN FETCH c.psicologo
       LEFT JOIN FETCH c.perfilCitas
       WHERE c.perfilCitas.idPerfil = :perfilId
       """)
       List<Cita> cargarCitasConRelaciones(@Param("perfilId") Long perfilId);  

       @Query("SELECT c FROM Cita c " +
       "LEFT JOIN FETCH c.perfilCitas  " +
       "LEFT JOIN FETCH c.psicologo " +
       "WHERE c.psicologo.id = :psicologoId AND c.fechaCita = :fecha")
       List<Cita> findByPsicologoIdAndFechaCitaWithRelations(@Param("psicologoId") Integer psicologoId,
                                                      @Param("fecha") LocalDate fechaCita);
}