package mx.uam.ayd.proyecto.presentacion.menuPsicologo;

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
import mx.uam.ayd.proyecto.presentacion.VentanaEncuesta;
import mx.uam.ayd.proyecto.presentacion.Horario.VentanaHorario;

/**
 * Ventana (Vista) para el menú del Psicólogo.
 * Carga el FXML /fxml/ventanaMenuPsicologo.fxml
 */
@Component
public class VentanaMenuPsicologo {

    private Stage stage;
    private ControlMenuPsicologo control;
    private boolean initialized = false;

    @FXML
    private FlowPane breadcrumbContainer;
    private BreadcrumbController breadcrumbController;
    
    @FXML
    private StackPane contentArea; // Panel central para cargar otras vistas

    public void setControl(ControlMenuPsicologo control) {
        this.control = control;
    }

    private void initializeUI() {
        if (initialized) return;
        
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }
        
        try {
            stage = new Stage();
            stage.setTitle("Centro Psicológico - Menú Psicólogo");
            
            // --- APUNTA AL NUEVO FXML ---
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaMenuPsicologo.fxml"));
            loader.setController(this);
            
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            
            stage.setMinWidth(950);
            stage.setMinHeight(700);
            
            cargarBreadcrumb();
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Carga el FXML del breadcrumb en el panel superior
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
            Platform.runLater(this::muestra);
            return;
        }
        initializeUI();
        stage.show();
    }

    // Actualiza la ruta del breadcrumb
    public void actualizaBreadcrumb(List<String> ruta) {
        if (breadcrumbController != null) {
            // Define una acción simple al hacer clic (solo imprime en consola)
            breadcrumbController.setPath(ruta, item -> System.out.println("Clic en breadcrumb: " + item));
        }
    }

    // Carga una vista (Node) en el panel central (StackPane)
    public void cargarVista(Node vista) {
        if (contentArea != null) {
            contentArea.getChildren().setAll(vista);
        } else {
            System.err.println("Error: contentArea no fue inyectado por FXML.");
        }
    }

    
    @FXML
    private void handleAgregarPaciente() {
        if (control != null) {
            control.agregarPaciente();
        }
    }
    
    @FXML
    private void handleListarPacientes() {
        if (control != null) {
            control.listarPacientes();
        }
    }

    @FXML
    private void handleEncuesta() {
        VentanaEncuesta ventana = new VentanaEncuesta();
        ventana.mostrarEncuesta();
    }

    @FXML
    private void handlePerfilCitas() {
        if (control != null) {
            control.consultarPerfilCitas();
        }
    }
    
    @FXML
    private void handleHorario(){
        if(control != null){
            control.horario();
        }
    }

    @FXML
    private void handleSalir() {
        if (control != null) {
            control.salir();
        }
    }
}