package mx.uam.ayd.proyecto.negocio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.HistorialClinicoRepository;
import mx.uam.ayd.proyecto.datos.PacienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.negocio.modelo.HistorialClinico;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioHistorialClinicoTest {

    @Mock
    PacienteRepository pacienteRepository;

    @Mock
    HistorialClinicoRepository historialClinicoRepository;

    @InjectMocks
    ServicioHistorialClinico service;

    //Metodo de agregar Historial clínico

    //Caso 1: El paciente no esta en BD
    @Test
    void deberiaLanzarExcepcionCuandoPacienteNoExiste() {
        when(pacienteRepository.findByCorreo("test@mail.com")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.guardarHistorialClinico("Juan", "test@mail.com", "motivo", "no", "desc", true);
        });

        assertTrue(ex.getMessage().contains("No existe un paciente"));
        verify(pacienteRepository).findByCorreo("test@mail.com");
        verifyNoInteractions(historialClinicoRepository);
    }

    //Caso 2: El nombre del paciente no coincide con el escrito en el historial clínico

    @Test
    void deberiaLanzarExcepcionCuandoNombreNoCoincide() {
        Paciente paciente = new Paciente();
        paciente.setNombre("Pedro");
        when(pacienteRepository.findByCorreo("test@mail.com")).thenReturn(paciente);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.guardarHistorialClinico("Juan", "test@mail.com", "motivo", "no", "desc", true);
        });

        assertTrue(ex.getMessage().contains("El nombre no coincide"));
        verify(pacienteRepository).findByCorreo("test@mail.com");
        verifyNoInteractions(historialClinicoRepository);
    }

    //Caso 3: El paciente no confirma el concentimiento

    @Test
    void deberiaLanzarExcepcionCuandoConsentimientoNoAceptado() {
        Paciente paciente = new Paciente();
        paciente.setNombre("Juan");
        when(pacienteRepository.findByCorreo("test@mail.com")).thenReturn(paciente);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.guardarHistorialClinico("Juan", "test@mail.com", "motivo", "no", "desc", false);
        });

        assertTrue(ex.getMessage().contains("Acepte el consentimiento"));
        verify(pacienteRepository).findByCorreo("test@mail.com");
        verifyNoInteractions(historialClinicoRepository);
    }

    //Caso 4: se guarda de manera correcta el historial clínico

    @Test
    void deberiaGuardarHistorialCorrectamente() {
        Paciente paciente = new Paciente();
        paciente.setNombre("Juan");
        when(pacienteRepository.findByCorreo("test@mail.com")).thenReturn(paciente);

        HistorialClinico historial = new HistorialClinico();
        when(historialClinicoRepository.save(any(HistorialClinico.class))).thenReturn(historial);

        HistorialClinico resultado = service.guardarHistorialClinico(
                "Juan", "test@mail.com", "motivo", "no", "desc", true
        );

        assertNotNull(resultado);
        verify(pacienteRepository).findByCorreo("test@mail.com");
        verify(historialClinicoRepository).save(any(HistorialClinico.class));
    }

    // Metodo obtenerHistorialFormateado

    //Caso 1: el paciente existe y tiene asignado un historial clinico 

    @Test
    void deberiaRetornarHistorialFormateadoCuandoExiste() {
        
        ServicioHistorialClinico service = new ServicioHistorialClinico();
        Paciente paciente = new Paciente();

        HistorialClinico historialMock = mock(HistorialClinico.class);
        when(historialMock.toStringFormateado()).thenReturn("Historial formateado");
        paciente.setHistorialClinico(historialMock);
    
        String resultado = service.obtenerHistorialFormateado(paciente);

        assertEquals("Historial formateado", resultado);
        verify(historialMock).toStringFormateado();
    }

    
    // Caso 2: El paciente no tiene un historial asociado

    @Test
    void deberiaRetornarMensajeCuandoNoHayHistorial() {

        Paciente paciente = new Paciente(); // sin historial
        ServicioHistorialClinico service = new ServicioHistorialClinico();

        String resultado = service.obtenerHistorialFormateado(paciente);

        assertEquals("No hay un historial clínico registrado para este paciente.", resultado);
    }
}
