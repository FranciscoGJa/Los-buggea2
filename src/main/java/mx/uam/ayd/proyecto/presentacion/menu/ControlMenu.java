package mx.uam.ayd.proyecto.presentacion.menu;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.presentacion.listarpacientes.ControlListarPacientes;
import mx.uam.ayd.proyecto.presentacion.agregarPsicologo.ControlAgregarPsicologo;
import mx.uam.ayd.proyecto.presentacion.listarPsicologo.ControlListarPsicologo;
import mx.uam.ayd.proyecto.presentacion.agregarPaciente.ControlAgregarPaciente;

/**
 * Controlador principal del menú de la aplicación.
 * 
 * Esta clase actúa como punto central de navegación entre las distintas funcionalidades
 * del sistema. Recibe las acciones del usuario desde {@link VentanaMenu} y redirige el flujo
 * a los controladores correspondientes.
 * 
 * Sus responsabilidades incluyen:
 * <ul>
 *   <li>Inicializar la conexión con la vista del menú.</li>
 *   <li>Delegar la apertura de cada módulo según la acción seleccionada.</li>
 *   <li>Permitir la salida de la aplicación.</li>
 * </ul>
 * 
 * Es un bean administrado por Spring y se instancia una sola vez durante el ciclo de vida
 * de la aplicación.
 * 
 * @author 
 */
@Component
public class ControlMenu {

    private final VentanaMenu ventana;
    
    private final ControlListarPacientes controlListarPacientes;
    private final ControlAgregarPaciente controlAgregarPaciente;
    private final ControlAgregarPsicologo controlAgregarPsicologo;
    private final ControlListarPsicologo controlListarPsicologo;
    
    @FXML
    private StackPane contentArea;
    /**
     * Constructor que inyecta todas las dependencias necesarias para gestionar las opciones del menú.
     * 
     * @param ventana vista principal del menú
     * @param controlListarPacientes controlador para la funcionalidad de listar pacientes
     * @param controlAgregarPsicologo controlador para la funcionalidad de agregar psicólogos
     * @param controlListarPsicologo controlador para la funcionalidad de listar psicólogos
     * @param controlAgregarPaciente controlador para la funcionalidad de agregar pacientes
     */
    @Autowired
    public ControlMenu(
            VentanaMenu ventana,
            ControlListarPacientes controlListarPacientes,
            ControlAgregarPsicologo controlAgregarPsicologo,
            ControlListarPsicologo controlListarPsicologo,
            ControlAgregarPaciente controlAgregarPaciente
        ) {
        this.ventana = ventana;
        this.controlListarPacientes = controlListarPacientes;
        this.controlAgregarPsicologo = controlAgregarPsicologo;
        this.controlListarPsicologo = controlListarPsicologo;
        this.controlAgregarPaciente = controlAgregarPaciente;
    }
    
    /**
     * Inicializa la conexión entre este controlador y la ventana de menú.
     * Se ejecuta automáticamente tras la construcción del bean por Spring.
     */
    @PostConstruct
    public void init() {
        ventana.setControlMenu(this);
    }
    
    /**
     * Inicia la visualización del menú principal.
     */
    public void inicia() {
        ventana.muestra();
    }
    public void cargarVista(Node nuevaVista) {
    System.out.println("Cargando vista: " + nuevaVista);
    contentArea.getChildren().setAll(nuevaVista);
}

    /**
     * Abre el flujo para agregar un nuevo paciente.
     */
    public void agregarPaciente() {
    // Actualiza el breadcrumb
    ventana.actualizaBreadcrumb(List.of("Inicio", "Pacientes", "Agregar Paciente"));
    
    // Inicializa el módulo (si tiene método getVista() que retorna un Node)
    controlAgregarPaciente.inicia();
    
    // Carga la vista en el StackPane del menú
    ventana.cargarVista(controlAgregarPaciente.getVista());
}

   public void listarPacientes() {
    ventana.actualizaBreadcrumb(List.of("Inicio", "Pacientes", "Listar Pacientes"));
    controlListarPacientes.inicia();
    ventana.cargarVista(controlListarPacientes.getVista());
}

public void agregarPsicologo() {
    ventana.actualizaBreadcrumb(List.of("Inicio", "Psicólogos", "Agregar Psicólogo"));
    controlAgregarPsicologo.inicia();
    ventana.cargarVista(controlAgregarPsicologo.getVista());
}

public void listarPsicologo() {
    ventana.actualizaBreadcrumb(List.of("Inicio", "Psicólogos", "Listar Psicólogos"));
    controlListarPsicologo.inicia();
    ventana.cargarVista(controlListarPsicologo.getVista());
}

    
    /**
     * Finaliza la ejecución de la aplicación.
     */
    public void salir() {
        System.exit(0);
    }
}