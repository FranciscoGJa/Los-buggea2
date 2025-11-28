package mx.uam.ayd.proyecto.presentacion.menuPsicologo;

import jakarta.annotation.PostConstruct;
import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;
import mx.uam.ayd.proyecto.presentacion.listarpacientes.ControlListarPacientes;
import mx.uam.ayd.proyecto.presentacion.agregarPaciente.ControlAgregarPaciente;
import mx.uam.ayd.proyecto.presentacion.PerfilCitas.VentanaPelfil;
import mx.uam.ayd.proyecto.presentacion.Horario.ControlHorario;
import java.util.List;

/**
 * Controlador para el menú específico del Psicólogo
 * Muestra una vista restringida sin opciones de administrador.
 */
@Component
public class ControlMenuPsicologo {

    @Autowired
    private VentanaMenuPsicologo ventana;

    // --- Dependencias permitidas---
    @Autowired
    private ControlListarPacientes controlListarPacientes;

    @Autowired
    private ControlAgregarPaciente controlAgregarPaciente;

    @Autowired
    private VentanaPelfil ventanaPelfil; // Para "Consultar Perfiles de Citas"
    @Autowired
    private ControlHorario ventanaHorario;
    private Psicologo psicologoLogueado;

    @PostConstruct
    public void init() {
        ventana.setControl(this);
    }

    /**
     * Inicia el menú del psicólogo
     * @param psicologo El psicólogo que ha iniciado sesión
     */
    public void inicia(Psicologo psicologo) {
        this.psicologoLogueado = psicologo;
        System.out.println("Iniciando menú para el psicólogo: " + psicologo.getNombre());
        ventana.muestra();
    }

    // --- Funciones Permitidas ---

    public void agregarPaciente() {
        ventana.actualizaBreadcrumb(List.of("Inicio", "Pacientes", "Agregar Paciente"));
        controlAgregarPaciente.inicia();
        ventana.cargarVista(controlAgregarPaciente.getVista());
    }

    public void listarPacientes() {
        ventana.actualizaBreadcrumb(List.of("Inicio", "Pacientes", "Listar Pacientes"));
        controlListarPacientes.inicia();
        ventana.cargarVista(controlListarPacientes.getVista());
    }

    public void consultarPerfilCitas() {
        ventanaPelfil.muestra();
    }

    public void horario(){
        ventanaHorario.iniciar();
    }

    // --- Salir ---
    public void salir() {
        System.exit(0);
    }
}