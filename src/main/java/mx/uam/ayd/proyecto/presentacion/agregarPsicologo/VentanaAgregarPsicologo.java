package mx.uam.ayd.proyecto.presentacion.agregarPsicologo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.modelo.TipoEspecialidad;
import java.io.IOException;

/**
 * Ventana para registrar nuevos psicólogos.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Cargar y mostrar la interfaz JavaFX para el alta de psicólogos.</li>
 *   <li>Validar los datos ingresados por el usuario.</li>
 *   <li>Delegar la creación de psicólogos al {@link ControlAgregarPsicologo}.</li>
 *   <li>Mostrar mensajes de éxito o error.</li>
 * </ul>
 * </p>
 *
 * <p>Flujo típico:
 * <ol>
 *   <li>Mostrar la ventana con {@link #muestra()}.</li>
 *   <li>Ingresar datos y presionar "Agregar psicólogo".</li>
 *   <li>Si las validaciones son correctas, delegar al controlador para registrar.</li>
 * </ol>
 * </p>
 *
 * @version 1.0
 */
@Component
public class VentanaAgregarPsicologo {

    @FXML
    private TextField textFieldNombre;
    
    @FXML
    private TextField textFieldCorreo;
    
    @FXML
    private TextField textFieldTelefono;
    
    @FXML
    private ComboBox<TipoEspecialidad> comboBoxEspecialidad;

    private Stage stage;
    private ControlAgregarPsicologo controlAgregarPsicologo;
    private boolean initialized = false;

    /** Constructor por defecto. */
    public VentanaAgregarPsicologo() {
        //constructor vacio
    }

    /**
     * Inicializa la interfaz de usuario cargando el FXML y creando el Stage.
     * <p>Si no se está en el hilo de JavaFX, la acción se reprograma con {@link Platform#runLater(Runnable)}.</p>
     */
    private void initializeUI(){
        if (initialized) {
            return;
        }
        
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }
        
        try {
            stage = new Stage();
            stage.setTitle("Centro Psicológico - Agregar Psicologo");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaAgregarPsicologo.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 640, 400);
            stage.setScene(scene);

            inicializarComboBox();
            
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Asigna el controlador que gestionará las acciones de esta vista.
     * @param control controlador de la ventana
     */
    public void setControlAgregarPsicologo(ControlAgregarPsicologo control){
        this.controlAgregarPsicologo = control;
    }

    /**
     * Muestra la ventana. Si no está inicializada, la prepara primero.
     * <p>Se limpia el formulario antes de mostrar.</p>
     */
    public void muestra(){
        if (!Platform.isFxApplicationThread()){
            Platform.runLater(() -> this.muestra());
            return;
        }

        initializeUI();
        limpiarCampos();
        stage.show();
    }

    /**
     * Inicializa el ComboBox con las especialidades disponibles.
     */
    private void inicializarComboBox() {
        if (comboBoxEspecialidad != null) {
            comboBoxEspecialidad.setItems(FXCollections.observableArrayList(TipoEspecialidad.values()));
            comboBoxEspecialidad.setPromptText("Seleccione una espacialidad");
        }
    }

    /**
     * Maneja el evento de agregar un psicólogo desde la UI.
     * <p>Valida los campos y, si todo es correcto, delega el registro al controlador.</p>
     */
    @FXML
    private void handleAgregarPsicologo(){
        String nombre = textFieldNombre.getText();
        String correo = textFieldCorreo.getText();
        String telefono = textFieldTelefono.getText();
        TipoEspecialidad especialidad = comboBoxEspecialidad.getValue();

        // Validaciones 
        if (nombre == null || nombre.trim().isEmpty()){
            mostrarError("El nombre es obligatorio");
            return;
        }

        if (correo == null || correo.trim().isEmpty()) {
            mostrarError("El correo es obligatorio");
            return;
        }

        if (!correo.contains("@") || !correo.contains(".")) {
            mostrarError("El formato del correo no es válido");
            return;
        }

        if (telefono == null || telefono.trim().isEmpty()) {
            mostrarError("El teléfono es obligatorio");
            return;
        }

        if (especialidad == null) {
            mostrarError("Debe seleccionar una especialidad");
            return;
        }

        if (controlAgregarPsicologo != null){
            controlAgregarPsicologo.agregarPsicologo(nombre, correo, telefono, especialidad);
        }
    }

    /**
     * Muestra un mensaje de error en una alerta modal.
     * @param mensaje texto del error
     */
    public void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de validacion");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje de éxito en una alerta modal.
     * @param mensaje texto del éxito
     */
    public void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Limpia los campos del formulario.
     */
    public void limpiarCampos() {
        if (!initialized) return;
        if (textFieldNombre != null) textFieldNombre.clear();
        if (textFieldCorreo != null) textFieldCorreo.clear();
        if (textFieldTelefono != null) textFieldTelefono.clear();
        if (comboBoxEspecialidad != null) comboBoxEspecialidad.setValue(null);
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
     * Maneja el evento de cancelar, cerrando la ventana.
     */
    @FXML
    private void handleCancelar() {
        if (stage != null) {
            stage.close();
        }
    }
}

