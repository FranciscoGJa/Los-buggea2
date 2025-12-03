package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Date;

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

@ExtendWith(MockitoExtension.class)
class ServicioBateriaClinicaTest {

    @Mock
    private BateriaClinicaRepository bateriaClinicaRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private ServicioBateriaClinica servicio;

     //Prueba: Registrar una batería totalmente nueva cuando el paciente existe y no tiene una batería previa de ese tipo.

    @Test
    void testRegistrarBateria_Nueva_Exito() {
        // Given: Un paciente existente y respuestas válidas (suma 1+2+3+4+5 = 15)
        Long pacienteId = 1L;
        TipoBateria tipo = TipoBateria.BAI;
        List<Integer> respuestas = Arrays.asList(1, 2, 3, 4, 5);
        String comentarios = "Paciente estable";
        Paciente pacienteMock = new Paciente();
        
        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(pacienteMock));
        when(bateriaClinicaRepository.findByPacienteAndTipoDeBateria(pacienteMock, tipo))
            .thenReturn(Optional.empty()); // No existe batería previa
        
        // When: Simulamos el guardado. Asignamos ID Long para evitar errores de tipo.
        when(bateriaClinicaRepository.save(any(BateriaClinica.class))).thenAnswer(invocation -> {
            BateriaClinica b = invocation.getArgument(0);
            b.setId(100L); // Simulamos que la BD asigna el ID 100
            return b;
        });

        BateriaClinica resultado = servicio.registrarBateria(pacienteId, tipo, respuestas, comentarios);

        // Then: Se debe crear una nueva batería con la calificación calculada correctamente
        assertNotNull(resultado);
        assertEquals(15, resultado.getCalificacion(), "La calificación debe ser la suma de las respuestas");
        assertEquals(comentarios, resultado.getComentarios());
        assertEquals(pacienteMock, resultado.getPaciente());
        verify(bateriaClinicaRepository).save(any(BateriaClinica.class));
    }

  
     // Prueba: Actualizar una batería existente si el paciente ya tenía una del mismo tipo registrada.
  
    @Test
    void testRegistrarBateria_ActualizarExistente_Exito() {
        // Given: Un paciente con una batería previa ya guardada
        Long pacienteId = 1L;
        TipoBateria tipo = TipoBateria.BAI;
        List<Integer> nuevasRespuestas = Arrays.asList(0, 0, 0, 0, 0); // Nueva suma = 0
        Paciente pacienteMock = new Paciente();
        
        BateriaClinica bateriaExistente = new BateriaClinica();
        bateriaExistente.setId(50L); // ID existente para simular actualización
        bateriaExistente.setPaciente(pacienteMock);
        bateriaExistente.setCalificacion(10); // Calificación vieja

        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(pacienteMock));
        when(bateriaClinicaRepository.findByPacienteAndTipoDeBateria(pacienteMock, tipo))
            .thenReturn(Optional.of(bateriaExistente)); // Devuelve la existente 
        
        when(bateriaClinicaRepository.save(bateriaExistente)).thenReturn(bateriaExistente);

        // When: Se intenta registrar la batería nuevamente
        BateriaClinica resultado = servicio.registrarBateria(pacienteId, tipo, nuevasRespuestas, "Actualizado");

        // Then: Se debe actualizar la instancia existente (mismo ID) y recalcular calificación
        assertEquals(50L, resultado.getId());
        assertEquals(0, resultado.getCalificacion()); // Se actualizó de 10 a 0
        assertEquals("Actualizado", resultado.getComentarios());
        verify(bateriaClinicaRepository).save(bateriaExistente);
    }

     //Prueba: Verificar que lanza excepción si el paciente no existe.

    @Test
    void testRegistrarBateria_PacienteNoEncontrado() {
        // Given: Un ID de paciente que no existe en el repositorio
        Long pacienteId = 20L;
        List<Integer> respuestas = Arrays.asList(1, 1, 1, 1, 1);
        // When: Al llamar al servicio, el repositorio devuelve empty
        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.empty());

        // Then: Se espera una IllegalArgumentException
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrarBateria(pacienteId, TipoBateria.BAI, respuestas, "Test");
        });

        assertEquals("Paciente no encontrado " + pacienteId, ex.getMessage());
    }

    
     // Prueba: Validar manejo de argumentos inválidos (Respuestas nulas o tamaño incorrecto).
     
    @Test
    void testRegistrarBateria_ArgumentosInvalidos() {
        // Given: Una lista de respuestas con tamaño incorrecto (solo 3 respuestas)
        List<Integer> respuestasIncorrectas = Arrays.asList(1, 2, 3);

        // When: Se llama al método con argumentos inválidos
        assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrarBateria(1L, TipoBateria.BAI, respuestasIncorrectas, "Test");
        });
        // Then: Verificamos que se lance la excepción 
        assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrarBateria(null, TipoBateria.BAI, Arrays.asList(1,2,3,4,5), "Test");
        });
    }

    
     //Prueba: Guardar comentarios en una batería existente.
    
    @Test
    void testGuardarComentarios_Exito() {
        // Given: Una batería existente
        BateriaClinica bateria = new BateriaClinica();
        bateria.setComentarios("Comentario viejo");
        String nuevoComentario = "Nuevo análisis clínico";

        when(bateriaClinicaRepository.save(bateria)).thenReturn(bateria);

        // When: Se llama a guardarComentarios
        BateriaClinica resultado = servicio.guardarComentarios(bateria, nuevoComentario);

        // Then: El comentario se actualiza y se persiste
        assertEquals(nuevoComentario, resultado.getComentarios());
        verify(bateriaClinicaRepository).save(bateria);
    }

     // Prueba: Validar que no se pueda guardar comentario en batería nula.
     
    @Test
    void testGuardarComentarios_BateriaNula() {
        // Given: Bateria nula
        BateriaClinica bateria = null;

        // When / Then: Se espera excepción al intentar guardar comentarios
        assertThrows(IllegalArgumentException.class, () -> 
            servicio.guardarComentarios(bateria, "Comentario")
        );
    }

     // Prueba: Obtener detalles de una batería y evitar errores de texto exacto

    @Test
    void testObtenerDetallesBateria_Exito() {
        // Given: Una batería con datos completos
        BateriaClinica bateria = new BateriaClinica();
        bateria.setTipoDeBateria(TipoBateria.BDI_II);
        bateria.setCalificacion(15);
        bateria.setFechaAplicacion(new Date());

        // When: Solicitamos los detalles
        String detalles = servicio.obtenerDetallesBateria(bateria);

        // Then: Verificamos que contenga los datos clave
        assertNotNull(detalles);
        
        // Usamos toString() para asegurar que buscamos exactamente el nombre
        assertTrue(detalles.contains(TipoBateria.BDI_II.toString()), 
                "Debe contener el nombre del tipo de batería: " + TipoBateria.BDI_II);
        
        assertTrue(detalles.contains("15"), "Debe contener la calificación numérica");
        assertTrue(detalles.contains("Fecha de Aplicación:"), "Debe contener la etiqueta de la fecha");
    }
    
     //Prueba: Obtener detalles cuando se pasa null.
     
    @Test
    void testObtenerDetallesBateria_Null() {
        // Given: Ninguna batería seleccionada (null)
        // When: Llamamos al método
        String detalles = servicio.obtenerDetallesBateria(null);
        
        // Then: Mensaje de error esperado
        assertEquals("No se ha seleccionado ninguna batería.", detalles);
    }
}