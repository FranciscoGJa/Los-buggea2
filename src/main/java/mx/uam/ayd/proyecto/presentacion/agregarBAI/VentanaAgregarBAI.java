package mx.uam.ayd.proyecto.presentacion.agregarBAI;

import java.io.IOException;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/**
 * Ventana para capturar la batería BAI (Inventario de Ansiedad de Beck).
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Cargar y mostrar la interfaz JavaFX para la batería BAI.</li>
 *   <li>Recolectar las respuestas del usuario y validar que estén completas.</li>
 *   <li>Delegar el guardado de resultados al controlador {@link ControlAgregarBAI}.</li>
 *   <li>Mostrar mensajes informativos y de error.</li>
 * </ul>
 * </p>
 *
 * <p>Uso: invocar {@link #setControlAgregarBAI(ControlAgregarBAI)}, 
 * {@link #setPacienteID(Long)} y finalmente {@link #muestra()} para desplegar la ventana.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
@Component
public class VentanaAgregarBAI {
    
    private Stage stage;
    private boolean initialized = false;
    private ControlAgregarBAI controlAgregarBAI;

    private Long pacienteID;

    /**
     * Inyecta el controlador que realizará el guardado de resultados.
     * @param controlAgregarBAI controlador para la batería BAI
     */
    public void setControlAgregarBAI(ControlAgregarBAI controlAgregarBAI) {
        this.controlAgregarBAI = controlAgregarBAI;
    }

    /**
     * Define el ID del paciente al que se asociará la captura.
     * @param pacienteID identificador del paciente
     */
    public void setPacienteID(Long pacienteID) {
        this.pacienteID=pacienteID;
    }

    @FXML private javafx.scene.control.ToggleGroup q1;
    @FXML private javafx.scene.control.ToggleGroup q2;
    @FXML private javafx.scene.control.ToggleGroup q3;
    @FXML private javafx.scene.control.ToggleGroup q4;
    @FXML private javafx.scene.control.ToggleGroup q5;

    /**
     * Acción del botón Guardar.
     * <p>Valida que todas las preguntas tengan respuesta, arma la lista de respuestas
     * y delega el guardado al controlador.</p>
     */
    @FXML
    private void onGuard() {
        try {
            java.util.List<Integer> respuesta = java.util.Arrays.asList(
                getSelectedValue(q1),
                getSelectedValue(q2),
                getSelectedValue(q3),
                getSelectedValue(q4),
                getSelectedValue(q5)
            );

            if (respuesta.stream().anyMatch(r -> r == null)) {
                muestraDialogoConMensaje("Responde todas las preguntas antes de guardar.");
                return;
            }

            String comentarios = " ";
            controlAgregarBAI.guardarBAI(pacienteID, respuesta, comentarios);

            muestraDialogoConMensaje("¡Batería BAI guardada!");
            stage.close();

        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error al guardar "+ ex.getMessage()).showAndWait();
        }
    }

    /**
     * Obtiene el valor seleccionado de un grupo de toggles.
     * @param group grupo de opciones
     * @return valor entero definido en el {@code userData} del toggle seleccionado; 0 si no hay selección
     */
    private Integer getSelectedValue(ToggleGroup group) {
        if (group != null && group.getSelectedToggle() != null &&
            group.getSelectedToggle().getUserData() != null) {
            return Integer.parseInt(group.getSelectedToggle().getUserData().toString());
        }
        return 0;
    }

    /**
     * Inicializa la interfaz de usuario (carga el FXML y configura el {@link Stage}).
     * <p>Si no se está en el hilo de JavaFX, reintenta mediante {@link Platform#runLater(Runnable)}.</p>
     */
    private void initializeUI() {
        if (initialized) return;
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }
        try {
            stage = new Stage();
            stage.setTitle("Inventario de Ansiedad de Beck (BAI)");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-BAI.fxml"));
            loader.setController(this); // Solo si NO hay fx:controller en el FXML
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo cargar la ventana: " + e.getMessage()).showAndWait();
        }
    }

    /** Constructor por defecto requerido por Spring/JavaFX. */
    public VentanaAgregarBAI() {
        
    }

    /**
     * Muestra la ventana. Si no ha sido inicializada, la inicializa primero.
     */
    public void muestra() {
        if (!initialized) {
            initializeUI();
        }
        stage.show();
    }

    /**
     * Cambia la visibilidad de la ventana.
     * <p>Garantiza la ejecución en el hilo de JavaFX y crea la UI si es necesario.</p>
     * @param visible {@code true} para mostrar; {@code false} para ocultar
     */
    public void setVisible(boolean visible) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.setVisible(visible));
            return;
        }

        if (!initialized) {
            if (visible) {
                initializeUI();
            } else {
                return;
            }
        }

        if (visible) {
            stage.show();
        } else {
            stage.hide();
        }
    }

    /**
     * Muestra un diálogo informativo con el mensaje indicado.
     * <p>Si no se ejecuta en el hilo de JavaFX, reprograma la acción.</p>
     * @param mensaje contenido a mostrar en la alerta
     */
    public void muestraDialogoConMensaje(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDialogoConMensaje(mensaje));
            return;
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}