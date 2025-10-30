/*package mx.uam.ayd.proyecto.presentacion.PerfilCitas;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class VentanaPelfil {
    private Stage stage;
    private  ControladorPerfil control;
    private boolean initialized = false;

    /**
     * Constructor vacío requerido por Spring y JavaFX.
     */

     /*public VentanaPelfil(){
        // vacio
     }*/

     /**
     * Inicializa la interfaz de usuario cargando el archivo FXML.
     * Este método se asegura de ejecutarse en el hilo de JavaFX.
     */

    /* private void initialized (){
        if (initialized){
            return;
        }

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Centro Psicológico - Perfil de Citas");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/main/resources/fxml/ventana-PerfiCitas.fxml"));
            loader.setController(this);
            Scene scene = new Scene (loader.load(), width:640, height:400);
            stage.setScene(scene);

            initialized= true;
        }catch(IOException e){
            e.printStackTrace();
        }
     }

}*/
