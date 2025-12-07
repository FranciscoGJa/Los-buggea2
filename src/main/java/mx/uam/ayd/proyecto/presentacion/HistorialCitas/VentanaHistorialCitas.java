package mx.uam.ayd.proyecto.presentacion.HistorialCitas;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
public class VentanaHistorialCitas {

    @Autowired
    private ApplicationContext applicationContext;

    private Stage stage;

    /**
     * Devuelve la vista para incrustarla en el menú del psicólogo.
     */
    public Parent getVista(PerfilCitas perfil) throws Exception {
        
        String fxmlPath = "/fxml/ventana-historial-citas.fxml";
        URL fxmlUrl = getClass().getResource(fxmlPath);

        if (fxmlUrl == null) {
            System.err.println("[VentanaHistorialCitas] ERROR: No se encontró el FXML en: " + fxmlPath);
            return new VBox(new Label("Error al cargar historial de citas."));
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        loader.setControllerFactory(applicationContext::getBean);

        Parent root = loader.load();

        ControladorHistorialCitas controlador = loader.getController();
        controlador.cargarPerfil(perfil);

        return root;
    }

    /**
     * Muestra la ventana de historial en un Stage independiente
     * (por ejemplo, para el menú de administrador).
     */
    public void mostrar(PerfilCitas perfil) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> mostrar(perfil));
            return;
        }

        try {
            String fxmlPath = "/fxml/ventana-historial-citas.fxml";
            URL fxmlUrl = getClass().getResource(fxmlPath);

            if (fxmlUrl == null) {
                System.err.println("[VentanaHistorialCitas] ERROR: No se encontró el FXML en: " + fxmlPath);
                if (stage == null) {
                    stage = new Stage();
                }
                stage.setScene(new Scene(new VBox(new Label("Error al cargar historial de citas.")), 600, 400));
                stage.show();
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setControllerFactory(applicationContext::getBean);

            Parent root = loader.load();
            ControladorHistorialCitas controlador = loader.getController();
            controlador.cargarPerfil(perfil);

            if (stage == null) {
                stage = new Stage();
                stage.setTitle("Historial de citas - " + perfil.getNombreCompleto());
                stage.setMinWidth(900);
                stage.setMinHeight(600);
            }

            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.show();
            stage.toFront();

        } catch (Exception e) {
            System.err.println("[VentanaHistorialCitas] Error al mostrar historial: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
