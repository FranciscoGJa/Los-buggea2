package mx.uam.ayd.proyecto.presentacion.PerfilCitas;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra());
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