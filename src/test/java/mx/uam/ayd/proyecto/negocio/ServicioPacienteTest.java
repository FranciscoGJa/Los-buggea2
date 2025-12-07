package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.PacienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;

@ExtendWith(MockitoExtension.class)
class ServicioPacienteTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private ServicioPaciente servicioPaciente;

    @Test
    void testAgregarPaciente() {
        // Caso 1: Agregar paciente exitosamente
        when(pacienteRepository.findByCorreo("test@correo.com")).thenReturn(null);

        Paciente pacienteGuardado = new Paciente();
        pacienteGuardado.setNombre("Juan");
        pacienteGuardado.setCorreo("test@correo.com");
        pacienteGuardado.setTelefono("123456789");
        pacienteGuardado.setEdad(30);

        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteGuardado);

        Paciente result = servicioPaciente.agregarPaciente("Juan", "test@correo.com", "123456789", 30);

        assertNotNull(result);
        assertEquals("Juan", result.getNombre());
        assertEquals("test@correo.com", result.getCorreo());
        assertEquals("123456789", result.getTelefono());
        assertEquals(30, result.getEdad());

        // Caso 2: Nombre nulo o vacío
        assertThrows(IllegalArgumentException.class, () -> {
            servicioPaciente.agregarPaciente(null, "correo@dominio.com", "123456789", 25);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            servicioPaciente.agregarPaciente(" ", "correo@dominio.com", "123456789", 25);
        });

        // Caso 3: Correo nulo o vacío
        assertThrows(IllegalArgumentException.class, () -> {
            servicioPaciente.agregarPaciente("Juan", null, "123456789", 25);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            servicioPaciente.agregarPaciente("Juan", " ", "123456789", 25);
        });

        // Caso 4: Teléfono nulo o vacío
        assertThrows(IllegalArgumentException.class, () -> {
            servicioPaciente.agregarPaciente("Juan", "correo@dominio.com", null, 25);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            servicioPaciente.agregarPaciente("Juan", "correo@dominio.com", " ", 25);
        });

        // Caso 5: Correo duplicado
        when(pacienteRepository.findByCorreo("repetido@correo.com")).thenReturn(new Paciente());
        assertThrows(IllegalArgumentException.class, () -> {
            servicioPaciente.agregarPaciente("Pedro", "repetido@correo.com", "111222333", 40);
        });

        //caso 6: Edad negativa o muy alta
        assertThrows(IllegalArgumentException.class, () -> {
            servicioPaciente.agregarPaciente("Juan", "correo@dominio.com", "123456789", -1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            servicioPaciente.agregarPaciente("Juan", "correo@dominio.com", "123456789", 123);
        });

    }

        //Test de Recuperar todos los pacientes
    @Test
    void testRecuperarTodosLosPacientes() {
        /***   Caso 1: Lista vacía   ***/

        //Given
        when(pacienteRepository.findAll()).thenReturn(Collections.emptyList());

        //When
        List<Paciente> pacientes = servicioPaciente.recuperarTodosLosPacientes();

        //Then
        assertEquals(0, pacientes.size(), "La lista de pacientes debe tener tamaño 0");

        /***   Caso 2: Lista con pacientes   ***/

        //Given
        Paciente p1 = new Paciente();
        Paciente p2 = new Paciente();
        when(pacienteRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        //When
        pacientes = servicioPaciente.recuperarTodosLosPacientes();

        //Then
        assertEquals(2, pacientes.size(), "La lista de pacientes debe tener tamaño 2");
    }

    //Test asignar psicologo
    @Test
    void testAsignarPsicologo() {
        Paciente paciente = new Paciente();
        Psicologo psicologo = new Psicologo();
        //Given

        //When
        servicioPaciente.asignarPsicologo(paciente, psicologo);

        //Then
        assertEquals(psicologo, paciente.getPsicologo());
        verify(pacienteRepository).save(paciente);
    }

    //Test de obtener pacientes con detalles con ID existente
    @Test
    void testRecuperarPacienteCompletoConRelaciones(){
        Paciente paciente = new Paciente();
        paciente.setNombre("Test");

        //Given
        when(pacienteRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(paciente));

        //When
        Paciente resultado = servicioPaciente.obtenerPacienteConDetalles(1L);
        
        //Then
        assertNotNull(resultado);
        assertEquals("Test", resultado.getNombre());
    }

    //Test de obtener pacientes con ID inexistente
    @Test
    void testRecuperarPacienteCompletoConRelacionesInexistentes(){
        //Given
        when(pacienteRepository.findByIdWithRelations(1L)).thenReturn(Optional.empty());

        //When
        Paciente resultado = servicioPaciente.obtenerPacienteConDetalles(1L);

        //Then
        assertNull(resultado);
    }

}