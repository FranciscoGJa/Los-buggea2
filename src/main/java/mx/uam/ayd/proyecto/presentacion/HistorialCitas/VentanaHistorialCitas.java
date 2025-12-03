package mx.uam.ayd.proyecto.presentacion.HistorialCitas;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;
import javafx.scene.control.Alert;
import java.io.IOException;

/*
 * Ventana para mostrar el historial de citas de un perfil.
 * Utiliza Spring para la inyección de dependencias y JavaFX para la interfaz gráfica.
 */

@Component
public class VentanaHistorialCitas {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    public void mostrar(PerfilCitas perfil) {
        try {
            System.out.println("ABRIENDO HISTORIAL PARA: " + perfil.getNombreCompleto());
            
            Stage stage = new Stage();
            stage.setTitle("Historial de Citas - " + perfil.getNombreCompleto());
            
            // Cargar FXML con fx:controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-historial-citas.fxml"));
            
            // IMPORTANTE: Usar Spring para crear el controlador
            loader.setControllerFactory(applicationContext::getBean);
            
            Scene scene = new Scene(loader.load(), 900, 700);
            stage.setScene(scene);
            
            // Obtener el controlador que Spring creó
            ControladorHistorialCitas controlador = loader.getController();
            
            if (controlador == null) {
                throw new RuntimeException("No se pudo obtener el controlador del FXML");
            }
            
            // Cargar datos del perfil
            controlador.cargarPerfil(perfil);
            
            stage.show();
            stage.toFront();
            System.out.println("Ventana de historial mostrada exitosamente");
            
        } catch (IOException e) {
            System.err.println("ERROR de IO al cargar FXML:");
            e.printStackTrace();
            mostrarError("Error de archivo", "No se pudo cargar la interfaz: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR inesperado al abrir historial:");
            e.printStackTrace();
            mostrarError("Error inesperado", "Ocurrió un error al abrir el historial: " + e.getMessage());
        }
    }

    /**
     * Devuelve la vista (`Parent`) del historial de citas para insertarla en el
     * `contentArea` de la ventana principal. No crea un Stage.
     */
    public javafx.scene.Parent getVista(PerfilCitas perfil) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-historial-citas.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        javafx.scene.Parent root = loader.load();

        // Obtener el controlador creado por Spring y pasarle el perfil
        ControladorHistorialCitas controlador = loader.getController();
        if (controlador == null) {
            throw new RuntimeException("No se pudo obtener el controlador del FXML");
        }
        controlador.cargarPerfil(perfil);
        return root;
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}