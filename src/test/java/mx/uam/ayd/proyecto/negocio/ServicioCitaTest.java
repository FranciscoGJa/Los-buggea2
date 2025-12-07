package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.CitaRepository;
import mx.uam.ayd.proyecto.datos.PsicologoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ServicioCita
 */
@ExtendWith(MockitoExtension.class)
class ServicioCitaTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private ServicioPerfilCitas servicioPerfilCitas;

    @Mock
    private PsicologoRepository psicologoRepository;

    @InjectMocks
    private ServicioCita servicioCita;
    
    private Psicologo psicologo;
    
    private PerfilCitas perfilCitas;
    
    private Cita cita;
    
    private LocalDate fechaFutura;

    @BeforeEach
    void setUp() {
        fechaFutura = LocalDate.now().plusDays(1);
        
        // Psicólogo de prueba
        psicologo = new Psicologo();
        psicologo.setId(1);
        psicologo.setNombre("Dr. Juan Pérez");
        psicologo.setEspecialidad(TipoEspecialidad.INFANTIL);

        // Paciente de prueba
        Paciente paciente = new Paciente();
        paciente.setId(100L);
        paciente.setNombre("Ana García");
        paciente.setEdad(30);

        // Perfil de citas de prueba
        perfilCitas = new PerfilCitas();
        perfilCitas.setIdPerfil(50L);
        perfilCitas.setPsicologo(psicologo);
        perfilCitas.setPaciente(paciente);

        // Cita existente de prueba
        cita = new Cita();
        cita.setId(200);
        cita.setPsicologo(psicologo);
        cita.setPerfilCitas(perfilCitas);
        cita.setFechaCita(fechaFutura);
        cita.setHoraCita(LocalTime.of(10, 0));
        cita.setEstadoCita(TipoConfirmacionCita.PENDIENTE);
    }

    // ========== PRUEBAS PARA CREAR CITA ==========

    @Test
    void crearCita_CuandoDatosSonValidos_DeberiaCrearCita() {
        // Given
        when(servicioPerfilCitas.obtenerPerfilPorId(50L))
                .thenReturn(Optional.of(perfilCitas));

        when(psicologoRepository.findById(1))
                .thenReturn(Optional.of(psicologo));

        when(citaRepository.findByPsicologoIdAndFechaCitaAndHoraCita(
                eq(1), any(LocalDate.class), any(LocalTime.class))
        ).thenReturn(Collections.emptyList());

        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        // When
        Cita resultado = servicioCita.crearCita(
                50L, 1,
                fechaFutura, LocalTime.of(10, 0), "Detalles"
        );

        // Then
        assertNotNull(resultado);
        assertEquals(TipoConfirmacionCita.PENDIENTE, resultado.getEstadoCita());
        verify(citaRepository).save(any(Cita.class));
    }
    

    @Test
    void crearCita_CuandoPerfilNoExiste_DeberiaLanzarExcepcion() {
        // Given
        when(servicioPerfilCitas.obtenerPerfilPorId(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.crearCita(999L, 1, fechaFutura, 
                    LocalTime.of(10, 0), "Detalles");
        });
        assertEquals("Perfil de citas no encontrado", ex.getMessage());
    }

    @Test
    void crearCita_CuandoFechaEsPasada_DeberiaLanzarExcepcion() {
        // Given
        when(servicioPerfilCitas.obtenerPerfilPorId(50L)).thenReturn(Optional.of(perfilCitas));
        when(psicologoRepository.findById(1)).thenReturn(Optional.of(psicologo));

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.crearCita(50L, 1, LocalDate.now().minusDays(1), 
                    LocalTime.of(10, 0), "Detalles");
        });
        assertEquals("No se pueden agendar citas en fechas pasadas", ex.getMessage());
    }

    @Test
    void crearCita_CuandoHorarioEstaOcupado_DeberiaLanzarExcepcion() {
        // Given
        when(servicioPerfilCitas.obtenerPerfilPorId(50L)).thenReturn(Optional.of(perfilCitas));
        when(citaRepository.findByPsicologoIdAndFechaCitaAndHoraCita(eq(1), eq(fechaFutura), eq(LocalTime.of(10, 0))))
                .thenReturn(List.of(cita));
        when(psicologoRepository.findById(1)).thenReturn(Optional.of(psicologo));

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.crearCita(50L, 1, fechaFutura, 
                    LocalTime.of(10, 0), "Detalles");
        });
        assertEquals("El psicólogo ya tiene una cita en esa fecha y hora", ex.getMessage());
    }

    // ========== PRUEBAS PARA GESTIONAR CITAS ==========

    @Test
    void obtenerCitasPorPerfil_CuandoPerfilTieneCitas_DeberiaRetornarLista() {
        // Given
        when(citaRepository.findByPerfilCitasIdPerfil(50L)).thenReturn(List.of(cita, cita));

        // When
        List<Cita> resultado = servicioCita.obtenerCitasPorPerfil(50L);

        // Then
        assertEquals(2, resultado.size());
        verify(citaRepository).findByPerfilCitasIdPerfil(50L);
    }

    @Test
    void obtenerCitasPorPerfil_CuandoPerfilNoTieneCitas_DeberiaRetornarListaVacia() {
        // Given
        when(citaRepository.findByPerfilCitasIdPerfil(50L)).thenReturn(Collections.emptyList());

        // When
        List<Cita> resultado = servicioCita.obtenerCitasPorPerfil(50L);

        // Then
        assertTrue(resultado.isEmpty());
    }

    // ========== PRUEBAS PARA CAMBIAR ESTADO ==========

    @Test
    void cambiarEstadoCita_CuandoCitaExiste_DeberiaActualizarEstado() {
        // Given
        when(citaRepository.findById(200)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        // When
        Cita resultado = servicioCita.cambiarEstadoCita(200, 
                TipoConfirmacionCita.CONFIRMADA, null);

        // Then
        assertEquals(TipoConfirmacionCita.CONFIRMADA, resultado.getEstadoCita());
        assertNull(resultado.getMotivoCancelacion());
    }

    @Test
    void cambiarEstadoCita_CuandoSeCancelaConMotivo_DeberiaGuardarMotivo() {
        // Given
        when(citaRepository.findById(200)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        // When
        Cita resultado = servicioCita.cambiarEstadoCita(200, 
                TipoConfirmacionCita.CANCELADA, "Paciente enfermo");

        // Then
        assertEquals(TipoConfirmacionCita.CANCELADA, resultado.getEstadoCita());
        assertEquals("Paciente enfermo", resultado.getMotivoCancelacion());
    }

    @Test
    void cambiarEstadoCita_CuandoCitaNoExiste_DeberiaLanzarExcepcion() {
        // Given
        when(citaRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.cambiarEstadoCita(999, TipoConfirmacionCita.CONFIRMADA, null);
        });
        assertEquals("Cita no encontrada", ex.getMessage());
    }

    // ========== PRUEBAS PARA NOTAS POST-SESIÓN ==========

    @Test
    void agregarNotaSesion_CuandoCitaEstaConcluida_DeberiaAgregarNota() {
        // Given
        cita.setEstadoCita(TipoConfirmacionCita.CONCLUIDA);
        when(citaRepository.findById(200)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        // When
        Cita resultado = servicioCita.agregarNotaSesion(200, "Nota importante");

        // Then
        assertEquals("Nota importante", resultado.getNotaPostSesion());
    }

    @Test
    void agregarNotaSesion_CuandoCitaNoEstaConcluida_DeberiaLanzarExcepcion() {
        // Given (cita está PENDIENTE por defecto)
        when(citaRepository.findById(200)).thenReturn(Optional.of(cita));

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.agregarNotaSesion(200, "Nota");
        });
        assertEquals("Solo se pueden agregar notas a citas concluidas", ex.getMessage());
    }

    // ========== PRUEBAS PARA REAGENDAR ==========

    @Test
    void reagendarCita_CuandoDatosSonValidos_DeberiaCrearNuevaCita() {
        // Given
        LocalDate nuevaFecha = fechaFutura.plusDays(1);
        LocalTime nuevaHora = LocalTime.of(11, 0);
        
        when(citaRepository.findById(200)).thenReturn(Optional.of(cita));
        when(citaRepository.findByPsicologoIdAndFechaCitaAndHoraCita(eq(1), eq(nuevaFecha), eq(nuevaHora)))
                .thenReturn(Collections.emptyList());
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        // When
        Cita resultado = servicioCita.reagendarCita(200, nuevaFecha, nuevaHora, "Motivo");

        // Then
        assertNotNull(resultado);
        verify(citaRepository, times(2)).save(any(Cita.class));
    }

    @Test
    void reagendarCita_CuandoNuevaFechaEsPasada_DeberiaLanzarExcepcion() {
        // Given
        when(citaRepository.findById(200)).thenReturn(Optional.of(cita));

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.reagendarCita(200, LocalDate.now().minusDays(1), 
                    LocalTime.of(11, 0), "Motivo");
        });
        assertEquals("No se pueden reagendar citas en fechas pasadas", ex.getMessage());
    }

    // ========== PRUEBAS DE CONSULTA ==========

    @Test
    void obtenerCitasPorPsicologo_CuandoPsicologoTieneCitas_DeberiaRetornarLista() {
        // Given
        when(citaRepository.findByPsicologoId(1)).thenReturn(List.of(cita));

        // When
        List<Cita> resultado = servicioCita.obtenerCitasPorPsicologo(1);

        // Then
        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).getPsicologo().getId());
    }

    @Test
    void obtenerCitasPorFecha_CuandoHayCitasEnFecha_DeberiaRetornarLista() {
        // Given - Usar la fecha de la cita existente
        LocalDate fecha = fechaFutura;
        cita.setFechaCita(fecha);
        when(citaRepository.findByFechaCita(fecha)).thenReturn(List.of(cita));

        // When
        List<Cita> resultado = servicioCita.obtenerCitasPorFecha(fecha);

        // Then
        assertEquals(1, resultado.size());
        assertEquals(fecha, resultado.get(0).getFechaCita());
    }
}