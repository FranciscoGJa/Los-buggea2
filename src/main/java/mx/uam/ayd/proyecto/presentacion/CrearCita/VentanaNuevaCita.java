package mx.uam.ayd.proyecto.presentacion.CrearCita;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;
import javafx.scene.control.Alert;

/*
 * Esta clase representa la ventana para crear una nueva cita.
 * Permite al usuario ingresar los detalles de la cita y
 * mostrar la interfaz correspondiente.
 * Utiliza JavaFX para la interfaz gráfica y Spring para la gestión de dependencias.
 * Contiene métodos para mostrar y cerrar la ventana.
 */

@Component
public class VentanaNuevaCita {
    
    private Stage stage;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    private ControladorNuevaCita controlador;
    
    public void mostrar(PerfilCitas perfil) {
        try {
            if (stage == null) {
                stage = new Stage();
                stage.setTitle("Nueva Cita - " + perfil.getNombreCompleto());
                
                // PATRÓN COMÚN: Cargar FXML con fx:controller y setControllerFactory
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-nueva-cita.fxml"));
                
                // Usar setControllerFactory para que Spring gestione el controlador
                loader.setControllerFactory(applicationContext::getBean);
                
                Scene scene = new Scene(loader.load(), 900, 800);
                
                // Aplicar CSS manualmente (siguiendo el patrón de las otras ventanas)
                String cssPath = getClass().getResource("/css/style.css").toExternalForm();
                scene.getStylesheets().add(cssPath);
                System.out.println("CSS aplicado manualmente a ventana nueva cita: " + cssPath);
                
                stage.setScene(scene);
                
                // Obtener el controlador después de cargar
                controlador = loader.getController();
                
                if (controlador == null) {
                    throw new RuntimeException("No se pudo obtener el controlador del FXML");
                }
                
                stage.setOnCloseRequest(event -> {
                    event.consume();
                    cerrar();
                });
            }
            
            // Cargar datos del perfil
            controlador.cargarPerfil(perfil);
            
            stage.show();
            stage.toFront();
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar la ventana de nueva cita: " + e.getMessage());
        }
    }
    
    public void cerrar() {
        if (stage != null) {
            stage.close();
        }
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}