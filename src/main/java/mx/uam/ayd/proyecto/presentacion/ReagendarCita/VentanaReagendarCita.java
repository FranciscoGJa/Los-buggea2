package mx.uam.ayd.proyecto.presentacion.ReagendarCita;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import javafx.scene.control.Alert;
import java.io.IOException;

/**
 * Clase que representa la ventana para reagendar una cita.
 * Utiliza JavaFX para la interfaz gráfica y Spring para la gestión de dependencias.
 */

@Component
public class VentanaReagendarCita {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    public void mostrar(Cita cita) {
        try {
            System.out.println("ABRIENDO VENTANA REAGENDAR PARA CITA ID: " + cita.getId());
            System.out.println("Paciente: " + (cita.getPerfilCitas() != null ? cita.getPerfilCitas().getNombreCompleto() : "null"));
            System.out.println("Psicólogo: " + (cita.getPsicologo() != null ? cita.getPsicologo().getNombre() : "null"));
            
            Stage stage = new Stage();
            stage.setTitle("Reagendar Cita - " + 
                (cita.getPerfilCitas() != null ? cita.getPerfilCitas().getNombreCompleto() : "Paciente"));
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-reagendar-cita.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            
            Scene scene = new Scene(loader.load(), 700, 600);
            
            String cssPath = getClass().getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            System.out.println("CSS aplicado manualmente a ventana reagendar: " + cssPath);
            
            stage.setScene(scene);
            
            ControladorReagendarCita controlador = loader.getController();
            controlador.setCitaSeleccionada(cita);
            
            stage.show();
            stage.toFront();
            System.out.println("Ventana de reagendar mostrada exitosamente");
            
        } catch (IOException e) {
            System.err.println("ERROR de IO al cargar FXML de reagendar:");
            e.printStackTrace();
            mostrarError("Error de archivo", "No se pudo cargar la interfaz de reagendar: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR inesperado al abrir ventana de reagendar:");
            e.printStackTrace();
            mostrarError("Error inesperado", "Ocurrió un error al abrir la ventana de reagendar: " + e.getMessage());
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