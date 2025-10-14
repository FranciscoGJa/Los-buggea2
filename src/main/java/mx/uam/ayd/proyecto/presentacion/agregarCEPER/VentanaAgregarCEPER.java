package mx.uam.ayd.proyecto.presentacion.agregarCEPER;

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
 * Ventana para capturar la batería CEPER
 * (Inventario Exploratorio de Personalidad CEPER).
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Cargar y mostrar la interfaz JavaFX para el cuestionario CEPER.</li>
 *   <li>Recolectar y validar las respuestas del usuario.</li>
 *   <li>Delegar el guardado al controlador {@link ControlAgregarCEPER}.</li>
 *   <li>Mostrar mensajes informativos y de error.</li>
 * </ul>
 * </p>
 *
 * <p>Uso típico:
 * <ol>
 *   <li>Llamar a {@link #setControlAgregarCEPER(ControlAgregarCEPER)} y {@link #setPacienteID(Long)}.</li>
 *   <li>Invocar {@link #muestra()} para abrir la ventana.</li>
 * </ol>
 * </p>
 *
 * @version 1.0
 */
@Component
public class VentanaAgregarCEPER {
    private Stage stage;
    private boolean initialized = false;
    private ControlAgregarCEPER controlAgregarCEPER;

    private Long pacienteID;

    /**
     * Asigna el controlador que gestionará el guardado de la batería CEPER.
     * @param controlAgregarCEPER controlador asociado
     */
    public void setControlAgregarCEPER(ControlAgregarCEPER controlAgregarCEPER) {
        this.controlAgregarCEPER=controlAgregarCEPER;
    }

    /**
     * Define el ID del paciente asociado a la captura.
     * @param pacienteID identificador del paciente
     */
    public void setPacienteID(Long pacienteID) {
        this.pacienteID = pacienteID;
    }

    @FXML private javafx.scene.control.ToggleGroup q1;
    @FXML private javafx.scene.control.ToggleGroup q2;
    @FXML private javafx.scene.control.ToggleGroup q3;
    @FXML private javafx.scene.control.ToggleGroup q4;
    @FXML private javafx.scene.control.ToggleGroup q5;

    /**
     * Acción del botón Guardar.
     * <p>Valida que todas las preguntas tengan respuesta, arma la lista y
     * delega el guardado al controlador.</p>
     */
    @FXML
    private void onGuard() {
        try {
            java.util.List<Integer> respuestas = java.util.Arrays.asList(
                getSelectedValue(q1),
                getSelectedValue(q2),
                getSelectedValue(q3),
                getSelectedValue(q4),
                getSelectedValue(q5)
            );

            if (respuestas.stream().anyMatch(r -> r == null)) {
                muestraDialogoConMensaje("Responde todas las preguntas antes de guardar.");
                return;
            }

            String comentarios = " ";
            controlAgregarCEPER.guardarCEPER(pacienteID, respuestas, comentarios);

            muestraDialogoConMensaje("¡Batería CEPER guardada!");
            stage.close();

        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error al guardar: " + ex.getMessage()).showAndWait();
        }
    }

    /**
     * Obtiene el valor seleccionado de un grupo de toggles.
     * @param group grupo de opciones
     * @return entero definido en el {@code userData} del toggle seleccionado; 0 si no hay selección
     */
    private Integer getSelectedValue(ToggleGroup group) {
        if (group != null && group.getSelectedToggle() != null &&
            group.getSelectedToggle().getUserData() != null) {
            return Integer.parseInt(group.getSelectedToggle().getUserData().toString());
        }
        return 0;
    }

    /**
     * Inicializa la interfaz de usuario cargando el FXML y configurando el {@link Stage}.
     * <p>Si no se está en el hilo de JavaFX, se reintenta mediante {@link Platform#runLater(Runnable)}.</p>
     */
    private void initializeUI() {
        if (initialized) return;
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }
        try {
            stage = new Stage();
            stage.setTitle("Inventario Exploratorio de Personalidad CEPER");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-CEPER.fxml"));
            loader.setController(this); // Solo si NO hay fx:controller en el FXML
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo cargar la ventana: " + e.getMessage()).showAndWait();
        }
    }

    /** Constructor por defecto. */
    public VentanaAgregarCEPER(){

    }

    /**
     * Muestra la ventana. Si no está inicializada, la inicializa primero.
     */
    public void muestra() {
        if (!initialized) {
            initializeUI();
        }
        stage.show();
    }

    /**
     * Cambia la visibilidad de la ventana garantizando la ejecución en el hilo de JavaFX.
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
     * Muestra un diálogo de información con el mensaje indicado.
     * <p>Si no se está en el hilo de JavaFX, reintenta la acción.</p>
     * @param mensaje texto a mostrar
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