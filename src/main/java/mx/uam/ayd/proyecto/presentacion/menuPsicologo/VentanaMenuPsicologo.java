package mx.uam.ayd.proyecto.presentacion.menuPsicologo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;
import mx.uam.ayd.proyecto.presentacion.BreadcrumbController;
import mx.uam.ayd.proyecto.presentacion.VentanaEncuesta;

/**
 * Ventana (Vista) para el menú del Psicólogo.
 * Carga el FXML /fxml/ventanaMenuPsicologo.fxml.
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

            // Cargar el FXML principal
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

    /**
     * Muestra la ventana del menú.
     */
    public void muestra() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ventanaMenuPsicologo.fxml")
            );
            loader.setController(this);

            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/css/style.css").toExternalForm()
            );

            stage = new Stage();
            stage.setTitle("Centro Psicológico - Menú Psicólogo");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Actualiza la ruta del breadcrumb
    public void actualizaBreadcrumb(List<String> ruta) {
        if (breadcrumbController != null) {
            breadcrumbController.setPath(ruta, this::handleBreadcrumbClick);
        }
    }

    // Maneja clicks en el breadcrumb
    private void handleBreadcrumbClick(String item) {
        if ("Inicio".equalsIgnoreCase(item)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/principal.fxml"));
                Node vistaPrincipal = loader.load();
                if (contentArea != null) {
                    contentArea.getChildren().setAll(vistaPrincipal);
                }
                actualizaBreadcrumb(List.of("Inicio"));
            } catch (IOException e) {
                e.printStackTrace();
                if (contentArea != null) contentArea.getChildren().clear();
            }
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

    // --- Acciones del menú ---

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
    private void handleHorario() {
        if (control != null) {
            control.horario();
        }
    }

    @FXML
    private void handleEjerciciosRespiracion() {
        if (control != null) {
            control.ejerciciosRespiracion();
        }
    }

    @FXML
    private void handleMaterialDidactico() {
        if (control != null) {
            control.mostrarMaterialDidactico();
        }
    }

    @FXML
    private void handleAgendaPsicologo() {
        if (control != null) {
            control.abrirAgendaPsicologo();
        }
    }

    @FXML
    private void handleSalir() {
        if (control != null) {
            control.salir();
        }
    }

    /**
     * Carga un FXML arbitrario en el panel central.
     */
    public void cargarVista(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent vista = loader.load();
            contentArea.getChildren().setAll(vista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
