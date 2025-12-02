package mx.uam.ayd.proyecto.presentacion.menu;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;

import mx.uam.ayd.proyecto.presentacion.BreadcrumbController;
//  Import necesario para abrir la ventana de encuesta
import mx.uam.ayd.proyecto.presentacion.VentanaEncuesta;
//abre ventana de pago
//import mx.uam.ayd.proyecto.presentacion.Pago.VentanaPago;
import mx.uam.ayd.proyecto.presentacion.Pago.VentanaPagoEfectivo;
import org.springframework.beans.factory.annotation.Autowired;

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
 *   <li>Responder Encuesta</li>
 *   <li>Consultar Perfiles de Citas</li>
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
 */
@Component
public class VentanaMenu {

    private Stage stage;
    private ControlMenu control;
    private boolean initialized = false;
    @Autowired
    private VentanaPagoEfectivo ventanaPagoEfectivo;
    @FXML
    private FlowPane breadcrumbContainer;
    private BreadcrumbController breadcrumbController;// Controlador del breadcrumb
    @FXML
    private StackPane contentArea; // Este es el StackPane del centro de la ventana

    /**
     * Constructor vacío requerido por Spring y JavaFX.
     */
    public VentanaMenu() {
        // Constructor vacío
    }

    // Método para inyectar el controlador
    public void setControlMenu(ControlMenu control) {
        this.control = control;
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
            System.out.println(getClass().getResource("/fxml/ventanaPrincipal.fxml"));

            loader.setController(this);
            
            // Cargar el FXML sin forzar dimensiones - usa las del FXML
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            
            // Configurar tamaños mínimos
            stage.setMinWidth(950);
            stage.setMinHeight(700);
            
            cargarBreadcrumb();
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Carga el componente breadcrumb en el placeholder designado
    private void cargarBreadcrumb() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/breadcrumb.fxml"));
        Node breadcrumbNode = loader.load();
        breadcrumbController = loader.getController();
        breadcrumbContainer.getChildren().add(breadcrumbNode);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra());
            return;
        }
        
        initializeUI();
        stage.show();
    }
     // Actualiza el breadcrumb con la ruta proporcionada
    public void actualizaBreadcrumb(List<String> ruta) {
        breadcrumbController.setPath(ruta, this::handleBreadcrumbClick);
    }
    //Vamos a regresar a la ruta seleccionada del breadcrumb
    private void handleBreadcrumbClick(String item) {
        // Si se hace click en 'Inicio', cargamos la vista principal (principal.fxml)
        if ("Inicio".equalsIgnoreCase(item)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/principal.fxml"));
                Node vistaPrincipal = loader.load();
                if (contentArea != null) {
                    contentArea.getChildren().setAll(vistaPrincipal);
                }

                // Actualizar breadcrumb a la ruta inicial
                actualizaBreadcrumb(java.util.List.of("Inicio"));
            } catch (IOException e) {
                e.printStackTrace();
                // Fallback: limpiar el contentArea
                if (contentArea != null) contentArea.getChildren().clear();
            }
        }
    }
    // Carga una nueva vista en el área de contenido central
    public void cargarVista(Node vista) {
    if (contentArea != null) {
        contentArea.getChildren().setAll(vista);
    } else {
        System.err.println("contentArea no está inicializado");
    }
}

    
    // =======================================================
    // Handlers (métodos vinculados a los botones del menú)
    // =======================================================

    /**
     * Acción del botón "Agregar Paciente".
     * Llama al controlador para iniciar el flujo de agregar paciente.
     */
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
     * Acción del botón "Responder Encuesta".
     * Abre la ventana que contiene el formulario HTML.
     */
    @FXML
    private void handleEncuesta() {
        // Abre la ventana creada para mostrar la encuesta HTML
        VentanaEncuesta ventana = new VentanaEncuesta();
        ventana.mostrarEncuesta();
    }

    //@FXML
    /*private void handlePagar() {
        //abre la ventana de pago
        VentanaPago ventanaPago = new VentanaPago();
        ventanaPago.mostrar();
    }*/

    @FXML
    private void handlePagoEfectivo() {
        // Mostrar la vista de pago embebida en el contentArea y actualizar breadcrumb
        try {
            javafx.scene.Parent vista = ventanaPagoEfectivo.getVista();
            this.actualizaBreadcrumb(java.util.List.of("Inicio", "Pago", "Efectivo"));
            this.cargarVista(vista);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: mostrar en ventana independiente
            ventanaPagoEfectivo.mostrar();
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
     * Acción del botón "Consultar Perfiles".
     * Llama al controlador para iniciar el flujo de perfiles de citas.
     */
    @FXML
    private void handlePerfilCitas() {
        if (control != null) {
            control.consultarPerfilCitas();
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