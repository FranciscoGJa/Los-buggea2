package mx.uam.ayd.proyecto.negocio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.BateriaClinicaRepository;
import mx.uam.ayd.proyecto.datos.PacienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.BateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.negocio.modelo.TipoBateria;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioBateriaClinicaTest {

    // Aquí definimos los mocks y el servicio que vamos a probar.
    @Mock
    private BateriaClinicaRepository bateriaClinicaRepository;
    @Mock
    private PacienteRepository pacienteRepository;
    @InjectMocks
    private ServicioBateriaClinica servicioBateriaClinica;

    //region Pruebas para registrarBateria()

    @Test
    void deberiaLanzarExcepcionCuandoPacienteIDEsNulo() {
        // Para esta validación simple, no se necesita preparación.
        // Ejecutamos el método y, al mismo tiempo,
        // verificamos que lance la excepción esperada (IllegalArgumentException).
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioBateriaClinica.registrarBateria(null, TipoBateria.CEPER, Arrays.asList(1, 2, 3, 4, 5), "Comentarios");
        });

        // Verificamos que el mensaje de la excepción sea exactamente el que esperamos.
        assertEquals("pacienteID obligatorio", ex.getMessage());

        // Nos aseguramos de que no hubo ninguna interacción con los repositorios,
        // ya que el error de validación debe ocurrir antes.
        verifyNoInteractions(pacienteRepository, bateriaClinicaRepository);
    }

    @Test
    void deberiaLanzarExcepcionCuandoTipoEsNulo() {
        // Aquí también no necesitamos preparar nada, ya que solo verificamos una validación.
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioBateriaClinica.registrarBateria(1L, null, Arrays.asList(1, 2, 3, 4, 5), "Comentarios");
        });
        assertEquals("Tipo es obligatorio", ex.getMessage());
        verifyNoInteractions(pacienteRepository, bateriaClinicaRepository);
    }

    @Test
    void deberiaLanzarExcepcionCuandoRespuestasNoSonCinco() {
        // Aquí verificamos que si las respuestas no son exactamente 5, se lance una excepción.
        // No necesitamos preparar nada, ya que es una validación simple.
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioBateriaClinica.registrarBateria(1L, TipoBateria.BAI, Arrays.asList(1, 2, 3), "Comentarios");
        });
        assertEquals("Se requieren las 5 respuestas", ex.getMessage());
        verifyNoInteractions(pacienteRepository, bateriaClinicaRepository);
    }

    @Test
    void deberiaLanzarExcepcionCuandoPacienteNoSeEncuentra() {
        
        // Cuando se busque cualquier paciente por ID, debe devolver un "Optional" vacío, simulando que no lo encontró.
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Verificamos que si se intenta registrar una batería para un paciente que no existe, se lance una excepción.
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioBateriaClinica.registrarBateria(99L, TipoBateria.BDI_II, Arrays.asList(1, 1, 1, 1, 1), "Comentarios");
        });
        assertTrue(ex.getMessage().contains("Paciente no encontrado"));

        // Verificamos que el servicio sí intentó buscar al paciente.
        verify(pacienteRepository).findById(99L);
        // Verificamos que no se intentó guardar nada.
        verifyNoInteractions(bateriaClinicaRepository);
    }
    
    @Test
    void deberiaRegistrarNuevaBateriaCorrectamente() {
        // Arrange: Creamos los datos de prueba.
        Paciente paciente = new Paciente();
        paciente.setId(1L);
        List<Integer> respuestas = Arrays.asList(3, 4, 2, 5, 1); // Suma = 15
        
        // Configuramos el comportamiento de los mocks.
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(bateriaClinicaRepository.findByPacienteAndTipoDeBateria(paciente, TipoBateria.CEPER)).thenReturn(Optional.empty());
        when(bateriaClinicaRepository.save(any(BateriaClinica.class))).thenAnswer(invocation -> {
            BateriaClinica bateriaGuardada = invocation.getArgument(0);
            bateriaGuardada.setId(100); // Simulamos que la BD le asigna un ID.
            return bateriaGuardada;
        });

        // Ejecutamos el método a probar.
        BateriaClinica resultado = servicioBateriaClinica.registrarBateria(1L, TipoBateria.CEPER, respuestas, "Comentarios de prueba");

        // Verificamos que los resultados son los esperados.
        assertNotNull(resultado);
        assertEquals(100, resultado.getId());
        assertEquals(paciente, resultado.getPaciente());
        assertEquals(15, resultado.getCalificacion()); // Verificamos la lógica de negocio (la suma).
        
        // Verificamos que la interacción final (guardar) realmente ocurrió.
        verify(bateriaClinicaRepository).save(any(BateriaClinica.class));
    }

    @Test
    void deberiaActualizarBateriaExistenteCorrectamente() {
        //Creamos los datos de prueba.     
        Paciente paciente = new Paciente();
        paciente.setId(1L);
        BateriaClinica bateriaExistente = new BateriaClinica();
        bateriaExistente.setId(50);
        bateriaExistente.setPaciente(paciente);
        bateriaExistente.setTipoDeBateria(TipoBateria.BAI);
        bateriaExistente.setCalificacion(10); // Calificación antigua
        List<Integer> nuevasRespuestas = Arrays.asList(5, 5, 5, 5, 5); // Nueva suma = 25

        // Configuramos mocks para simular que el paciente y la batería ya existen.
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(bateriaClinicaRepository.findByPacienteAndTipoDeBateria(paciente, TipoBateria.BAI)).thenReturn(Optional.of(bateriaExistente));
        when(bateriaClinicaRepository.save(any(BateriaClinica.class))).thenReturn(bateriaExistente);

        BateriaClinica resultado = servicioBateriaClinica.registrarBateria(1L, TipoBateria.BAI, nuevasRespuestas, "Nuevos comentarios");

        // Verificamos que el resultado sea el esperado.
        assertNotNull(resultado);
        assertEquals(50, resultado.getId()); // El ID no debe cambiar.
        assertEquals(25, resultado.getCalificacion()); // La calificación debe actualizarse.
        verify(bateriaClinicaRepository).save(bateriaExistente);
    }
    //region Pruebas para guardarComentarios()

    @Test
    void deberiaLanzarExcepcionSiBateriaEsNulaEnGuardarComentarios() {
        // Verificamos que si se intenta guardar comentarios en una batería nula, se lance una excepción.
        // No se necesita preparación, ya que el caso es simple.
        // Ejecutamos el método y verificamos que lance la excepción esperada.
        // También verificamos que el mensaje de la excepción sea el correcto.
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioBateriaClinica.guardarComentarios(null, "Comentarios");
        });
        assertEquals("La batería no puede ser nula.", ex.getMessage());
        verifyNoInteractions(bateriaClinicaRepository);
    }

    @Test
    void deberiaGuardarComentariosCorrectamente() {
        // Creamos un objeto BateriaClinica de prueba.
        BateriaClinica bateria = new BateriaClinica();
        when(bateriaClinicaRepository.save(any(BateriaClinica.class))).thenReturn(bateria);
        BateriaClinica resultado = servicioBateriaClinica.guardarComentarios(bateria, "Nuevos comentarios actualizados");

        assertNotNull(resultado);
        assertEquals("Nuevos comentarios actualizados", resultado.getComentarios());
        verify(bateriaClinicaRepository).save(bateria);
    }

    //Pruebas para obtenerDetallesBateria()

    @Test
    void deberiaRetornarMensajeCuandoBateriaEsNula() {
        String resultado = servicioBateriaClinica.obtenerDetallesBateria(null);
        assertEquals("No se ha seleccionado ninguna batería.", resultado);
    }

    @Test
    void deberiaRetornarDetallesFormateadosCorrectamente() {
        // Definimos los datos que usaremos para construir nuestro objeto de prueba.
        TipoBateria tipoEsperado = TipoBateria.BDI_II;
        int puntajeEsperado = 22;

        // Creamos el objeto BateriaClinica con los datos de prueba.
        BateriaClinica bateria = new BateriaClinica();
        bateria.setTipoDeBateria(tipoEsperado);
        bateria.setFechaAplicacion(new Date());
        bateria.setCalificacion(puntajeEsperado);

        // Llamamos al método del servicio para que genere el string de detalles.
        String resultado = servicioBateriaClinica.obtenerDetallesBateria(bateria);
        
        // Creamos variables con los fragmentos de texto que esperamos.
        String textoTipoEsperado = "Detalles para la batería: " + tipoEsperado.toString();
        String textoPuntajeEsperado = "Puntaje: " + puntajeEsperado;
        String textoFechaEsperado = "Fecha de Aplicación:";

        // Usamos assertTrue con .contains() para verificar que cada fragmento esté presente en el resultado.
        // Añadimos mensajes de error personalizados para saber exactamente qué parte falló si la prueba no pasa.
        assertTrue(resultado.contains(textoTipoEsperado), "El texto del tipo de batería no coincide.");
        assertTrue(resultado.contains(textoPuntajeEsperado), "El texto del puntaje no coincide.");
        assertTrue(resultado.contains(textoFechaEsperado), "El texto de la fecha no aparece.");
    }
}