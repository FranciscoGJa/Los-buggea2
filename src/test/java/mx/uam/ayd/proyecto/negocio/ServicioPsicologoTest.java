package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    // Variables de prueba reutilizables
    private Paciente paciente1;
    private Paciente paciente2;
    private Psicologo psicologo1;
    private Psicologo psicologo2;
    private Psicologo psicologo3;

    @BeforeEach
    void setUp() {
        // Configurar pacientes de prueba
        paciente1 = new Paciente();
        paciente1.setNombre("Paciente 1");
        paciente1.setEdad(15); // Menor de 18
        
        paciente2 = new Paciente();
        paciente2.setNombre("Paciente 2");
        paciente2.setEdad(25); // Mayor de 18

        // Configurar psicólogos de prueba
        psicologo1 = new Psicologo();
        psicologo1.setNombre("Juan Pérez");
        psicologo1.setCorreo("juan@correo.com");
        psicologo1.setTelefono("555-1234");
        psicologo1.setEspecialidad(TipoEspecialidad.FAMILIAR);

        psicologo2 = new Psicologo();
        psicologo2.setNombre("María García");
        psicologo2.setCorreo("maria@correo.com");
        psicologo2.setTelefono("555-5678");
        psicologo2.setEspecialidad(TipoEspecialidad.INFANTIL);

        psicologo3 = new Psicologo();
        psicologo3.setNombre("Carlos López");
        psicologo3.setCorreo("carlos@correo.com");
        psicologo3.setTelefono("555-9012");
        psicologo3.setEspecialidad(TipoEspecialidad.DELAMUJER);
    }

    // ========== PRUEBAS PARA OBTENER PACIENTES CON CUESTIONARIOS ==========

    @Test
    @DisplayName("Regresa los pacientes con cuestionarios")
    void testObtenerPacientesConCuestionarios_filtraCorrectamente() {
        // Given 
        BateriaClinica bateria1 = new BateriaClinica();

        paciente1.setBateriasClinicas(Arrays.asList(bateria1)); // Paciente con baterías clínicas

        // When
        when(pacienteRepository.findAll()).thenReturn(Arrays.asList(paciente1));    

        // Then
        assertEquals(1, servicio.obtenerPacientesConCuestionarios().size());
    }

    @Test 
    @DisplayName("Regresa una lista vacía si el paciente no tiene cuestionarios")
    void testObtenerPacientesConCuestionarios_regresaVacia() {
        // Given 
        paciente1.setBateriasClinicas(Collections.emptyList()); // Paciente sin baterías clínicas
    
        // When
        when(pacienteRepository.findAll()).thenReturn(Arrays.asList(paciente1));    
    
        // Then
        assertEquals(0, servicio.obtenerPacientesConCuestionarios().size());
    }

    @Test
    @DisplayName("Regresa lista vacía cuando no hay pacientes en la base de datos")
    void testObtenerPacientesConCuestionarios_sinPacientes() {
        // Given
        when(pacienteRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Paciente> resultado = servicio.obtenerPacientesConCuestionarios();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
  @Test
    @DisplayName("El psicólogo ya se encuentra registrado por correo")
    public void testAgregarPsicologo_repetido() {
        // Given 
        psicologo1.setEspecialidad(TipoEspecialidad.FAMILIAR);
         
        // When
        when(psicologoRepository.findByCorreo("correo@gmail.com")).thenReturn(psicologo1);
        
        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            servicio.agregarPsicologo("Juan", "correo@gmail.com", "5571238291", TipoEspecialidad.FAMILIAR);
        });
        
        // Verificar que no se guardó ni se envió correo
        verify(psicologoRepository, never()).save(any(Psicologo.class));
        verify(correoService, never()).enviarCredenciales(anyString(), anyString(), anyString());
    }
 @Test
    @DisplayName("Se registró un psicólogo con un correo correctamente")
    public void testAgregarPsicologo_correcto() {
        // Given 
        String nombre = "Pedro";
        String correo = "correo@outlook.com";
        String telefono = "5571238292";
        

        // Configurar que no existe un psicólogo con ese correo
        when(psicologoRepository.findByCorreo(correo)).thenReturn(null);
        
        // Configurar el save para que devuelva un psicólogo con los datos
        when(psicologoRepository.save(any(Psicologo.class))).thenAnswer(invocation -> {
            Psicologo psicologoGuardado = invocation.getArgument(0);
        
            return psicologoGuardado;
        });

        // When
        Psicologo resultado = servicio.agregarPsicologo(nombre, correo, telefono, null);

        // Then
        assertNotNull(resultado);
        assertEquals(nombre, resultado.getNombre());
        assertEquals(correo, resultado.getCorreo());
        assertEquals(telefono, resultado.getTelefono());
    }

    @Test
    @DisplayName("listarPsicologos - Debe retornar todos los psicólogos registrados")
    void testListarPsicologos_conPsicologosExistentes() {
        // Given
        List<Psicologo> psicologosEsperados = Arrays.asList(psicologo1, psicologo2, psicologo3);
        
        when(psicologoRepository.findAll()).thenReturn(psicologosEsperados);

        // When
        List<Psicologo> resultado = servicio.listarPsicologos();

        // Then
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
        assertEquals("María García", resultado.get(1).getNombre());
        assertEquals("Carlos López", resultado.get(2).getNombre());
        
        verify(psicologoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerPsicologosPorEdadPaciente - Paciente menor de 18 años retorna solo psicólogos infantiles")
    void testObtenerPsicologosPorEdadPaciente_pacienteMenor18() {
        // Given
        paciente1.setEdad(15); // Menor de 18 años
        
        List<Psicologo> psicologosInfantiles = Arrays.asList(psicologo2); // María es infantil
        
        when(psicologoRepository.findByEspecialidad(TipoEspecialidad.INFANTIL))
            .thenReturn(psicologosInfantiles);

        // When
        List<Psicologo> resultado = servicio.obtenerPsicologosPorEdadPaciente(paciente1);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("María García", resultado.get(0).getNombre());
        assertEquals(TipoEspecialidad.INFANTIL, resultado.get(0).getEspecialidad());
           
    }
    @Test
    @DisplayName("obtenerPsicologosPorEdadPaciente - Paciente de 18 años retorna psicólogos no infantiles")
    void testObtenerPsicologosPorEdadPaciente_pacienteExactamente18() {
        // Given
        paciente1.setEdad(18); // Exactamente 18 años
        
        List<Psicologo> psicologosNoInfantiles = Arrays.asList(psicologo1, psicologo3); // Juan y Carlos
        
        when(psicologoRepository.findByEspecialidadNot(TipoEspecialidad.INFANTIL))
            .thenReturn(psicologosNoInfantiles);

        // When
        List<Psicologo> resultado = servicio.obtenerPsicologosPorEdadPaciente(paciente1);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().noneMatch(p -> p.getEspecialidad() == TipoEspecialidad.INFANTIL));
        
        verify(psicologoRepository, times(1)).findByEspecialidadNot(TipoEspecialidad.INFANTIL);
        verify(psicologoRepository, never()).findByEspecialidad(any());
    }

    @Test
    @DisplayName("obtenerPsicologosPorEdadPaciente - Paciente mayor de 18 años retorna psicólogos no infantiles")
    void testObtenerPsicologosPorEdadPaciente_pacienteMayor18() {
        // Given
        paciente1.setEdad(25); // Mayor de 18 años
        
        List<Psicologo> psicologosNoInfantiles = Arrays.asList(psicologo1, psicologo3);
        
        when(psicologoRepository.findByEspecialidadNot(TipoEspecialidad.INFANTIL))
            .thenReturn(psicologosNoInfantiles);

        // When
        List<Psicologo> resultado = servicio.obtenerPsicologosPorEdadPaciente(paciente1);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        
        verify(psicologoRepository, times(1)).findByEspecialidadNot(TipoEspecialidad.INFANTIL);
    }

   


}