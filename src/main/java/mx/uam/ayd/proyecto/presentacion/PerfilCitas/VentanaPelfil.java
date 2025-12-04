package mx.uam.ayd.proyecto.presentacion.PerfilCitas;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
    import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class VentanaPelfil {

    private Stage stage;

    @Autowired
    private ApplicationContext applicationContext;

    private ControladorPerfil controlador;

    public VentanaPelfil() {
        // Constructor vacío
    }

    /**
     * Devuelve la vista (Parent) de este componente sin crear un Stage.
     * Permite insertar la interfaz dentro del `contentArea` de la ventana principal.
     */
    public Parent getVista() {
    try {
        String fxmlPath = "/fxml/ventana-PerfiCitas.fxml";
        java.net.URL fxmlUrl = getClass().getResource(fxmlPath);

        if (fxmlUrl == null) {
            System.err.println("ERROR: No se encontró el archivo FXML en: " + fxmlPath);
            VBox root = new VBox(new Label("Error al cargar la vista de Perfil de Citas."));
            return root;
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);

        // ✔ Controller manejado por Spring
        loader.setControllerFactory(applicationContext::getBean);

        Parent root = loader.load();

        controlador = loader.getController();
        if (controlador != null) controlador.limpiarCampos();

        return root;

    } catch (Exception e) {
        System.err.println("ERROR al cargar la vista de Perfil de Citas: " + e.getMessage());
        e.printStackTrace();
        return new VBox(new Label("Error al cargar la vista de Perfil de Citas."));
    }
}


    /**
     * Muestra la ventana de perfil de citas en un Stage independiente.
     */
    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::muestra);
            return;
        }

        try {
            if (stage == null) {
                stage = new Stage();
                stage.setTitle("Centro Psicológico - Perfil de Citas");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-PerfiCitas.fxml"));
                loader.setControllerFactory(applicationContext::getBean);

                Scene scene = new Scene(loader.load(), 900, 700);
                scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

                stage.setScene(scene);

                controlador = loader.getController();

                stage.setOnCloseRequest(event -> {
                    event.consume();
                    cerrarVentana();
                });
            }

            if (controlador != null) {
                controlador.limpiarCampos();
            }

            stage.show();
            stage.toFront();

        } catch (Exception e) {
            System.err.println("Error al mostrar la ventana de Perfil de Citas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cerrarVentana() {
        if (stage != null) {
            stage.close();
        }
    }

    public Stage getStage() {
        return stage;
    }
}
