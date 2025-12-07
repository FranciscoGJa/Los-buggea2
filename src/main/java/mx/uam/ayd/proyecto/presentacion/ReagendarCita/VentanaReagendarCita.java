package mx.uam.ayd.proyecto.presentacion.ReagendarCita;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;

import java.net.URL;

/**
 * Ventana para reagendar una cita existente.
 */
@Component
public class VentanaReagendarCita {

    @Autowired
    private ApplicationContext applicationContext;

    private Stage stage;

    /**
     * Muestra la ventana de reagendar cita.
     *
     * @param perfil Perfil de citas al que pertenece la cita.
     * @param cita   Cita seleccionada en el historial.
     */
    public void mostrar(PerfilCitas perfil, Cita cita) {
        try {
            System.out.println("[VentanaReagendarCita] Abriendo ventana para cita ID: "
                    + (cita != null ? cita.getIdCita() : "null")
                    + " fecha " + (cita != null ? cita.getFechaCita() : "null"));

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ventana-reagendar-cita.fxml"));

            // Usar Spring para crear el controlador
            loader.setControllerFactory(applicationContext::getBean);

            Parent root = loader.load();

            // Obtener el controlador real del FXML
            ControladorReagendarCita controlador = loader.getController();

            // Pasar los datos de la cita y el perfil
            controlador.inicializarDatos(perfil, cita);

            Scene scene = new Scene(root);

            // ====== CSS global opcional ======
            try {
                URL css = getClass().getResource("/fxml/css/style.css");
                if (css == null) {
                    css = getClass().getResource("/css/style.css");
                }
                if (css != null) {
                    scene.getStylesheets().add(css.toExternalForm());
                } else {
                    System.out.println("[VentanaReagendarCita] CSS global no encontrado, se continúa sin estilos extra.");
                }
            } catch (Exception e) {
                System.out.println("[VentanaReagendarCita] Error al cargar CSS (ignorado): " + e);
            }
            // ==================================

            stage = new Stage();
            stage.setTitle("Reagendar cita");
            stage.setScene(scene);

            // Espera a que se cierre para que el historial pueda recargarse después
            stage.showAndWait();

        } catch (Exception e) {
            System.out.println("[VentanaReagendarCita] ERROR inesperado:");
            e.printStackTrace();
        }
    }

    public void cerrar() {
        if (stage != null) {
            stage.close();
        }
    }
}
