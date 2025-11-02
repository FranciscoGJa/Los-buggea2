package mx.uam.ayd.proyecto.presentacion.agregarBDI;

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
 * Ventana para capturar la batería BDI-II (Inventario de Depresión de Beck).
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Cargar y mostrar la interfaz JavaFX correspondiente al BDI-II.</li>
 *   <li>Recolectar y validar las respuestas del cuestionario.</li>
 *   <li>Delegar el guardado al controlador {@link ControlAgregarBDI}.</li>
 *   <li>Mostrar mensajes informativos y de error.</li>
 * </ul>
 * </p>
 *
 * <p>Uso típico: establecer el controlador con
 * {@link #setControlAgregarBDI(ControlAgregarBDI)}, asignar el paciente con
 * {@link #setPacienteID(Long)} y llamar a {@link #muestra()}.</p>
 *
 * @version 1.0
 */
@Component
public class VentanaAgregarBDI {

    private Stage stage;
    private boolean initialized = false; 
    private ControlAgregarBDI controlAgregarBDI;

    private Long pacienteID;

    /**
     * Inyecta el controlador que gestionará el guardado del BDI-II.
     * @param controlAgregarBDI controlador asociado a esta vista
     */
    public void setControlAgregarBDI(ControlAgregarBDI controlAgregarBDI) {
        this.controlAgregarBDI = controlAgregarBDI;
    }
    
    /**
     * Inicializa la UI: carga el FXML, crea el {@link Stage} y configura la escena.
     * <p>Si no se está en el hilo de JavaFX, reintenta con {@link Platform#runLater(Runnable)}.</p>
     */
    private void initializeUI() {
        if (initialized) return;
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }
        try {
            stage = new Stage();
            stage.setTitle("Inventario de Depresion de Beck (BDI-II)");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-BDI.fxml"));
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

    /** Constructor por defecto (no inicializa componentes JavaFX). */
    public VentanaAgregarBDI() {
        // No inicializar componentes JavaFX en el constructor
    }

    /**
     * Muestra la ventana. Si aún no está inicializada, la inicializa primero.
     */
    public void muestra() {
        if (!initialized) {
            initializeUI();
        }
        stage.show();
    }

    /**
     * Cambia la visibilidad de la ventana, garantizando el uso del hilo de JavaFX.
     * <p>Si se solicita mostrar y aún no está inicializada, la inicializa.</p>
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

    /**
     * Define el identificador del paciente al que se asociará la captura.
     * @param pacienteID ID del paciente
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
     * <p>Valida respuestas, construye la lista y delega el guardado al controlador.</p>
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
            controlAgregarBDI.guardarBDI(pacienteID, respuestas, comentarios);

            muestraDialogoConMensaje("¡Batería BDI guardada!");
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
}
