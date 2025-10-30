package mx.uam.ayd.proyecto.presentacion.contestarHistorialClinico;

import java.io.IOException;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Ventana para capturar el Historial Clínico de un paciente.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Cargar y mostrar la interfaz JavaFX para el historial clínico.</li>
 *   <li>Prellenar nombre/correo y fecha actual.</li>
 *   <li>Validar entradas básicas y consentimiento.</li>
 *   <li>Delegar el guardado al {@link ControlContestarHistorialClinico}.</li>
 *   <li>Mostrar mensajes informativos/errores.</li>
 * </ul>
 * </p>
 *
 * <p>Uso típico:
 * <ol>
 *   <li>Llamar a {@link #muestra(String, String)} con nombre y correo del paciente.</li>
 *   <li>Completar el formulario y presionar “Guardar”.</li>
 * </ol>
 * </p>
 *
 * @version 1.0
 */
@Component
public class VentanaContestarHistorialClinico {
    private Stage stage;
    private boolean initialized = false;
    private ControlContestarHistorialClinico controlContestarHistorialClinico;

    @FXML
    private TextField textFieldNombre;
    
    @FXML
    private TextField textFieldCorreo;
    
    @FXML
    private TextField textFieldMotivo;
    
    @FXML
    private TextField textFieldConsumo;
    
    @FXML
    private TextField textFieldDescripcion;
    
    @FXML
    private TextField textFieldDia;
    
    @FXML
    private TextField textFieldMes;
    
    @FXML
    private TextField textFieldAnio;
    
    @FXML
    private CheckBox checkBoxConsentimiento;

    /**
     * Asigna el controlador asociado a esta vista.
     * @param controlContestarHistorialClinico controlador de la ventana
     */
    public void setControlContestarHistorialClinico(ControlContestarHistorialClinico controlContestarHistorialClinico){
        this.controlContestarHistorialClinico = controlContestarHistorialClinico;
    }

    /**
     * Inicializa la interfaz: carga el FXML, crea el {@link Stage} y configura la escena.
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
            stage.setTitle("Historial Clinico: Centro de Bienestar Psicologico");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaHistorialClinico.fxml"));
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
    public VentanaContestarHistorialClinico() {
        // No inicializar componentes JavaFX en el constructor
    }

    /**
     * Limpia los campos del formulario.
     */
    private void limpiarCampos() {
        textFieldNombre.clear();
        textFieldCorreo.clear();
        textFieldMotivo.clear();
        textFieldConsumo.clear();
        textFieldDescripcion.clear();
        textFieldDia.clear();
        textFieldMes.clear();
        textFieldAnio.clear();
        checkBoxConsentimiento.setSelected(false);
    }

    /**
     * Prellena nombre/correo y la fecha actual (día, mes, año).
     * @param nombre nombre del paciente
     * @param correo correo del paciente
     */
    private void llenarCamposPorDefecto(String nombre, String correo) {
    // Nombre y correo pasados como argumento
        textFieldNombre.setText(nombre);
        textFieldCorreo.setText(correo);
    
    // Día, mes y año actuales
        java.time.LocalDate hoy = java.time.LocalDate.now();
        textFieldDia.setText(String.valueOf(hoy.getDayOfMonth()));
        textFieldMes.setText(String.valueOf(hoy.getMonthValue()));
        textFieldAnio.setText(String.valueOf(hoy.getYear()));
    }

    /**
     * Muestra la ventana, limpia el formulario y prellena datos por defecto.
     * @param nombre nombre del paciente
     * @param correo correo del paciente
     */
    public void muestra(String nombre, String correo) {
        if (!initialized) {
            initializeUI();
        }
        limpiarCampos();
        llenarCamposPorDefecto(nombre, correo);
        textFieldNombre.setEditable(false);
        textFieldCorreo.setEditable(false);
        textFieldAnio.setEditable(false);
        textFieldDia.setEditable(false);
        textFieldMes.setEditable(false);
        stage.show();
    }

    /**
     * Cambia la visibilidad de la ventana.
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

    /**
     * Acción del botón Guardar.
     * <p>Valida campos obligatorios y consentimiento; si todo es correcto,
     * delega el guardado al controlador.</p>
     */
    @FXML
    private void handleGuardar(){

        StringBuilder errores = new StringBuilder();
        String motivo = textFieldMotivo.getText().trim();
        String consumo = textFieldConsumo.getText().trim();
        String descripcion = textFieldDescripcion.getText().trim();
        boolean consentimiento = checkBoxConsentimiento.isSelected();

        // Validar campos vacíos
        if (motivo.isEmpty()) errores.append("• El motivo no debe estar vacío.\n");
        if (consumo.isEmpty()) errores.append("• El consumo no debe estar vacío.\n");
        if (descripcion.isEmpty()) errores.append("• La descripción no debe estar vacía.\n");

        // Validar consentimiento
        if (!consentimiento) {
            errores.append("• Debe aceptar el consentimiento para continuar.\n");
        }

        // Mostrar errores si los hay
        if (errores.length() > 0) {
            muestraDialogoConMensaje("Por favor corrija los siguientes errores:\n\n" + errores.toString());
            return;
        }

        // Si pasa todas las validaciones, llama al controlador
        controlContestarHistorialClinico.guardarHistorialClinico(textFieldNombre.getText(), 
                                                                textFieldCorreo.getText(), 
                                                                textFieldMotivo.getText(), 
                                                                textFieldConsumo.getText(), 
                                                                textFieldDescripcion.getText(), 
                                                                checkBoxConsentimiento.isSelected());
    }
}

