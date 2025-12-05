package mx.uam.ayd.proyecto.presentacion.Satisfaccion;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.stereotype.Component;

/**
 * Ventana encargada de mostrar la pantalla de:
 *  - Listado de pacientes del psicólogo
 *  - Gráfica de evaluaciones
 *
 * Carga el FXML: /fxml/VentanaSatisfaccion.fxml
 */
@Component
public class VentanaSatisfaccion {

    /**
     * Muestra la ventana de satisfacción con estadísticas.
     *
     * @param idPsicologo ID del psicólogo que se usará para cargar datos
     */
    public void mostrar(int idPsicologo) {
        try {

            // Crear nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Satisfacción y Estadísticas del Psicólogo");

            // Icono opcional
            try {
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/Logo.png")));
            } catch (Exception e) {
                System.out.println("⚠️ No se encontró el ícono en /images/Logo.png");
            }

            // Cargar FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/VentanaSatisfaccion.fxml")
            );

            Parent root = loader.load();

            // Obtener controlador para pasar ID del psicólogo
            ControladorSatisfaccion controlador = loader.getController();
            controlador.cargarDatosPsicologo(idPsicologo);

            // Crear escena
            Scene scene = new Scene(root, 900, 650);

            // Decoración de la ventana
            stage.initStyle(StageStyle.DECORATED);

            // Evitar que el usuario cambie el tamaño
            stage.setResizable(false);

            // Asignar escena
            stage.setScene(scene);

            // Mostrar
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ ERROR: No se pudo abrir la ventana de satisfacción.");
        }
    }
}
