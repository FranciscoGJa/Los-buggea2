package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.CitaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicioCalendarioTest {

    @Mock
    private CitaRepository citaRepository;

    @InjectMocks
    private ServicioCalendario servicioCalendario;

    private Integer psicologoId = 1;
    private LocalDate fecha;
    private LocalTime hora;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fecha = LocalDate.of(2026, 10, 23);
        hora = LocalTime.of(10, 0);
    }

    
    //  TEST 1: verificarDisponibilidad (caso disponible)

    @Test
    public void testVerificarDisponibilidad_CuandoNoHayCitas() {

        when(citaRepository.findByPsicologoIdAndFechaCitaAndHoraCita(psicologoId, fecha, hora))
                .thenReturn(new ArrayList<>());

        boolean disponible = servicioCalendario.verificarDisponibilidad(psicologoId, fecha, hora);

        assertTrue(disponible);
        verify(citaRepository, times(1))
                .findByPsicologoIdAndFechaCitaAndHoraCita(psicologoId, fecha, hora);
    }

    
    //  TEST 2: verificarDisponibilidad (caso ocupado)
    
    @Test
    public void testVerificarDisponibilidad_CuandoExisteCita() {

        Cita cita = new Cita();
        List<Cita> citas = List.of(cita);

        when(citaRepository.findByPsicologoIdAndFechaCitaAndHoraCita(psicologoId, fecha, hora))
                .thenReturn(citas);

        boolean disponible = servicioCalendario.verificarDisponibilidad(psicologoId, fecha, hora);

        assertFalse(disponible);
    }

   
    //  TEST 3: obtenerHorariosDisponibles (horarios libres)
    
    @Test
    public void testObtenerHorariosDisponibles_SinCitas() {

        when(citaRepository.findByPsicologoIdAndFechaCita(psicologoId, fecha))
                .thenReturn(new ArrayList<>());

        List<LocalTime> disponibles = servicioCalendario.obtenerHorariosDisponibles(psicologoId, fecha);

        assertEquals(10, disponibles.size());  
        assertTrue(disponibles.contains(LocalTime.of(9, 0)));
        assertTrue(disponibles.contains(LocalTime.of(18, 0)));
    }

    
    //  TEST 4: obtenerHorariosDisponibles (con horas ocupadas)
    
    @Test
    public void testObtenerHorariosDisponibles_ConHorasOcupadas() {

        Cita cita = new Cita();
        cita.setHoraCita(LocalTime.of(10, 0));

        List<Cita> citas = List.of(cita);

        when(citaRepository.findByPsicologoIdAndFechaCita(psicologoId, fecha))
                .thenReturn(citas);

        List<LocalTime> disponibles = servicioCalendario.obtenerHorariosDisponibles(psicologoId, fecha);

        assertFalse(disponibles.contains(LocalTime.of(10, 0)));  
        assertEquals(9, disponibles.size());
    }

   
    //  TEST 5: obtenerCitasPorPsicologoYFecha
    
    @Test
    public void testObtenerCitasPorPsicologoYFecha() {

        Cita cita1 = new Cita();
        Cita cita2 = new Cita();
        List<Cita> lista = List.of(cita1, cita2);

        when(citaRepository.findByPsicologoIdAndFechaCita(psicologoId, fecha))
                .thenReturn(lista);

        List<Cita> resultado = servicioCalendario.obtenerCitasPorPsicologoYFecha(psicologoId, fecha);

        assertEquals(2, resultado.size());
        verify(citaRepository, times(1))
                .findByPsicologoIdAndFechaCita(psicologoId, fecha);
    }

    
    //  TEST 6: Cobertura del constructor (línea 13)
    
    @Test
    public void testConstructor() {
        ServicioCalendario servicio = new ServicioCalendario();
        assertNotNull(servicio);
    }
}
