package mx.uam.ayd.proyecto.presentacion.menu;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Ventana principal de menú de la aplicación.
 * 
 * Esta clase representa la interfaz gráfica inicial del sistema, desde la cual 
 * el usuario puede acceder a las distintas funcionalidades:
 * <ul>
 *   <li>Agregar pacientes</li>
 *   <li>Listar pacientes</li>
 *   <li>Agregar psicólogos</li>
 *   <li>Listar psicólogos</li>
 *   <li>Salir de la aplicación</li>
 * </ul>
 * 
 * La clase se encarga de:
 * <ul>
 *   <li>Cargar y mostrar el archivo FXML correspondiente al menú principal.</li>
 *   <li>Delegar las acciones de los botones al {@link ControlMenu}.</li>
 *   <li>Gestionar el ciclo de vida de la ventana (creación, inicialización y visibilidad).</li>
 * </ul>
 * 
 * Es un bean administrado por Spring y se instancia una sola vez durante la ejecución
 * de la aplicación.
 * 
 * @author 
 */
@Component
public class VentanaMenu {

    private Stage stage;
    private ControlMenu control;
    private boolean initialized = false;

    /**
     * Constructor vacío requerido por Spring y JavaFX.
     */
    public VentanaMenu() {
        // Constructor vacio
    }
    
    /**
     * Inicializa la interfaz de usuario cargando el archivo FXML.
     * Este método se asegura de ejecutarse en el hilo de JavaFX.
     */
    private void initializeUI() {
        if (initialized) {
            return;
        }
        
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }
        
        try {
            stage = new Stage();
            stage.setTitle("Centro Psicológico - Menú Principal");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaPrincipal.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 640, 400);
            stage.setScene(scene);
            
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Establece la referencia al controlador de esta ventana.
     * 
     * @param control instancia de {@link ControlMenu}
     */
    public void setControlMenu(ControlMenu control) {
        this.control = control;
    }

    /**
     * Muestra la ventana del menú principal.
     * Se asegura de ejecutarse en el hilo de aplicación JavaFX.
     */
    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra());
            return;
        }
        
        initializeUI();
        stage.show();
    }
    
    /**
     * Acción del botón "Agregar Paciente".
     * Llama al controlador para iniciar el flujo de agregar paciente.
     */
    // Handler para botones FXML
    @FXML
    private void handleAgregarPaciente() {
        if (control != null) {
            control.agregarPaciente();
        }
    }
    
    /**
     * Acción del botón "Listar Pacientes".
     * Llama al controlador para iniciar el flujo de listado de pacientes.
     */
    @FXML
    private void handleListarPacientes() {
        if (control != null) {
            control.listarPacientes();
        }
    }

    /**
     * Acción del botón "Pagar".
     * Llama al controlador para iniciar el flujo de pago.
     */
    @FXML
    private void handlePagar(){
        if (control != null) {
            control.pagoServicio();
        }
    }
    
    /**
     * Acción del botón "Agregar Psicólogo".
     * Llama al controlador para iniciar el flujo de agregar psicólogo.
     */
    @FXML
    private void handleAgregarPsicologo() {
        if (control != null) {
            control.agregarPsicologo();
        }
    }
    
    /**
     * Acción del botón "Listar Psicólogos".
     * Llama al controlador para iniciar el flujo de listado de psicólogos.
     */
    @FXML
    private void handleListarPsicologo() {
        if (control != null) {
            control.listarPsicologo();
        }
    }
    
    /**
     * Acción del botón "Salir".
     * Llama al controlador para cerrar la aplicación.
     */
    @FXML
    private void handleSalir() {
        if (control != null) {
            control.salir();
        }
    }

    
}