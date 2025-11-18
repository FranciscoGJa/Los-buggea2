package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mx.uam.ayd.proyecto.datos.PacienteRepository;
import mx.uam.ayd.proyecto.datos.PsicologoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.CorreoService; 
import mx.uam.ayd.proyecto.negocio.modelo.BateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;
import mx.uam.ayd.proyecto.negocio.modelo.TipoBateria;
import mx.uam.ayd.proyecto.negocio.modelo.TipoEspecialidad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServicioPsicologoTest {
    
    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private PsicologoRepository psicologoRepository;

    @Mock
    private CorreoService correoService; 

    @InjectMocks
    private ServicioPsicologo servicio;

    // Prueba: Filtrado de pacientes con cuestionarios
    @Test
    void testObtenerPacientesConCuestionarios_filtraCorrectamente(){
        Paciente sinBaterias = new Paciente();
        sinBaterias.setNombre("Ana");
        sinBaterias.setBateriasClinicas(Collections.emptyList());

        Paciente conBaterias = new Paciente();
        conBaterias.setNombre("Luis");
        BateriaClinica bc = new BateriaClinica();
        bc.setTipoDeBateria(TipoBateria.CEPER);
        conBaterias.setBateriasClinicas(Arrays.asList(bc));

        Paciente bateriasNulas = new Paciente();
        bateriasNulas.setNombre("Mar");
        bateriasNulas.setBateriasClinicas(null);

        when(pacienteRepository.findAll()).thenReturn(Arrays.asList(sinBaterias, conBaterias, bateriasNulas));

        List<Paciente> resultado = servicio.obtenerPacientesConCuestionarios();

        assertEquals(1, resultado.size());
        assertEquals("Luis", resultado.get(0).getNombre());
    }

    // Prueba: Agregar psicólogo exitosamente
    @Test
    void testObtenerPacientesConCuestionarios_listaVacia() {
        String nombre = "Juan";
        String correo = "juan@demo.com";
        String tel = "555-0002";
        TipoEspecialidad esp = TipoEspecialidad.FAMILIAR;

        when(psicologoRepository.findByCorreo(correo)).thenReturn(null);
        when(psicologoRepository.save(any(Psicologo.class))).thenAnswer(inv -> inv.getArgument(0));

        Psicologo p = servicio.agregarPsicologo(nombre, correo, tel, esp);

        assertNotNull(p);
        assertEquals(nombre, p.getNombre());
        assertEquals(correo, p.getCorreo());
        assertEquals(tel, p.getTelefono());
        assertEquals(esp, p.getEspecialidad());
        
        verify(correoService, times(1)).enviarCredenciales(any(), any(), any()); 
    }

    // Prueba: No se puede agregar psicólogo con correo duplicado
    @Test
    void testAgregarPsicologo_correoDuplicado() {
        String correo = "duplicado@demo.com";
        when(psicologoRepository.findByCorreo(correo)).thenReturn(new Psicologo());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            servicio.agregarPsicologo("Nombre", correo, "1111", TipoEspecialidad.MARITAL));

        assertTrue(ex.getMessage().toLowerCase().contains("correo"));
        
        verify(correoService, times(0)).enviarCredenciales(any(), any(), any());
    }

    // Prueba: Validación de parámetros inválidos
    @Test
    void testAgregarPsicologo_parametrosInvalidos() {
        // Nombre nulo
        assertThrows(IllegalArgumentException.class, () ->
            servicio.agregarPsicologo(null, "a@h.com", "1", TipoEspecialidad.DELAMUJER));
        
        // Nombre vacío
        assertThrows(IllegalArgumentException.class, () ->
            servicio.agregarPsicologo("   ", "a@h.com", "1", TipoEspecialidad.DELAMUJER));

        // Correo nulo
        assertThrows(IllegalArgumentException.class, () ->
            servicio.agregarPsicologo("Juan", null, "1", TipoEspecialidad.FAMILIAR));
        
        // Correo vacío
        assertThrows(IllegalArgumentException.class, () ->
            servicio.agregarPsicologo("Juan", "   ", "1", TipoEspecialidad.FAMILIAR));

        // Teléfono nulo
        assertThrows(IllegalArgumentException.class, () ->
            servicio.agregarPsicologo("Juan", "a@a.com", null, TipoEspecialidad.INFANTIL));
        
        // Teléfono vacío
        assertThrows(IllegalArgumentException.class, () ->
            servicio.agregarPsicologo("Juan", "a@a.com", "   ", TipoEspecialidad.INFANTIL));

        verify(correoService, times(0)).enviarCredenciales(any(), any(), any());
    }

    // Prueba: Obtención de psicólogos para menores de 18 años
    @Test
    void testObtenerPsicologosPorEdadPaciente_menoresDe18() {
        Paciente menor = new Paciente();
        menor.setEdad(10);

        Psicologo inf1 = new Psicologo(); 
        inf1.setEspecialidad(TipoEspecialidad.INFANTIL);
        Psicologo inf2 = new Psicologo(); 
        inf2.setEspecialidad(TipoEspecialidad.INFANTIL);

        when(psicologoRepository.findByEspecialidad(TipoEspecialidad.INFANTIL))
            .thenReturn(Arrays.asList(inf1, inf2));

        List<Psicologo> lista = servicio.obtenerPsicologosPorEdadPaciente(menor);

        assertEquals(2, lista.size());
        assertTrue(lista.stream().allMatch(p -> p.getEspecialidad() == TipoEspecialidad.INFANTIL));
    }

    // Prueba: Obtención de psicólogos para mayores o iguales a 18 años
    @Test
    void testObtenerPsicologosPorEdadPaciente_mayorOIgual18() {
        Paciente mayor = new Paciente();
        mayor.setEdad(18);

        Psicologo fam = new Psicologo(); 
        fam.setEspecialidad(TipoEspecialidad.FAMILIAR);
        Psicologo mar = new Psicologo(); 
        mar.setEspecialidad(TipoEspecialidad.MARITAL);

        when(psicologoRepository.findByEspecialidadNot(TipoEspecialidad.INFANTIL))
            .thenReturn(Arrays.asList(fam, mar));

        List<Psicologo> lista = servicio.obtenerPsicologosPorEdadPaciente(mayor);

        assertEquals(2, lista.size());
        assertTrue(lista.stream().noneMatch(p -> p.getEspecialidad() == TipoEspecialidad.INFANTIL));
    }
}