package mx.uam.ayd.proyecto.presentacion.principal;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import java.io.IOException;
import javafx.scene.control.PasswordField;

import javafx.scene.control.Button;

/**
 * Ventana de login para el Centro Psicológico.
 * 
 * <p>Esta clase representa la interfaz gráfica de autenticación
 * inicial del sistema. Sus responsabilidades incluyen:</p>
 * <ul>
 *   <li>Mostrar el formulario de ingreso de credenciales.</li>
 *   <li>Gestionar la visibilidad de la contraseña (modo oculto/visible).</li>
 *   <li>Delegar la validación y autenticación de credenciales al 
 *       {@link ControlPrincipalCentro}.</li>
 *   <li>Mostrar mensajes de error cuando las credenciales no son válidas.</li>
 * </ul>
 * 
 * <p>La ventana está construida con JavaFX y vinculada a un archivo
 * FXML para su diseño visual.</p>
 * 
 * @author 
 */
@Component
public class VentanaPrincipalCentro {

    @FXML
    private TextField textFieldUsuario;
    
    @FXML private PasswordField passwordFieldOculto;
    @FXML private TextField passwordFieldVisible;
    @FXML private Button btnTogglePassword;
    private boolean mostrando = false;

    /**
     * Inicializa la lógica de sincronización entre el campo de contraseña visible
     * y el campo oculto. Este método es invocado automáticamente por JavaFX
     * después de cargar el FXML.
     */
    @FXML
    private void initialize() {
        // Sincroniza ambos campos para que siempre tengan el mismo texto
        if (passwordFieldVisible != null && passwordFieldOculto != null) {
            passwordFieldVisible.textProperty().bindBidirectional(passwordFieldOculto.textProperty());
        }
    }

    /**
     * Alterna la visibilidad de la contraseña entre modo oculto y texto plano.
     */
    // Ncesitamos manejar el cambio entre una version y otra
    @FXML
    private void togglePasswordVisibility() {
        mostrando = !mostrando;
        passwordFieldVisible.setVisible(mostrando);
        passwordFieldVisible.setManaged(mostrando);
        passwordFieldOculto.setVisible(!mostrando);
        passwordFieldOculto.setManaged(!mostrando);
        if (btnTogglePassword != null) {
            // No puede procesar emoji por eso usamos prefijo
            btnTogglePassword.setText(mostrando ? "O" : "V");
        }
    }

    private Stage stage;
    private ControlPrincipalCentro control;
    private boolean initialized = false;

    /**
     * Constructor por defecto.  
     * No inicializa la interfaz hasta que se invoque {@link #muestra()}.
     */
    public VentanaPrincipalCentro() {
        // Constructor vacío
    }

    /**
     * Inicializa la interfaz gráfica de la ventana en el hilo de aplicación JavaFX.
     * Carga el archivo FXML y configura el {@link Stage}.
     */
    private void initializeUI() {
        if (initialized) {
            return;
        }
        
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }
        
        try {
            stage = new Stage();
            stage.setTitle("Login - Centro Psicológico TechSolutions");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaLogin.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 640, 400);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm()); 
            stage.setScene(scene);
            
            // Cerrar aplicación al cerrar ventana de login
            stage.setOnCloseRequest(e -> Platform.exit());
            
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Establece el controlador para esta ventana
     * 
     * @param control el controlador principal del centro
     */
    public void setControlPrincipalCentro(ControlPrincipalCentro control) {
        this.control = control;
    }

    /**
     * Muestra la ventana de login.
     * Si la interfaz no está inicializada, la carga antes de mostrarla.
     */
    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra());
            return;
        }
        
        initializeUI();
        stage.show();
    }

    /**
     * Maneja el evento del botón "Ingresar".
     * Recupera las credenciales y las envía al controlador para su autenticación.
     */
    @FXML
    private void handleIngresar() {
        String usuario = textFieldUsuario.getText();
        String contrasena = passwordFieldOculto.getText();
        
        if (control != null) {
            control.autenticar(usuario, contrasena);
        }
    }

    /**
     * Muestra un mensaje de error en una ventana de diálogo
     * 
     * @param mensaje el mensaje de error a mostrar
     */
    public void mostrarError(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de autenticación");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    /**
     * Cierra la ventana de login
     */
    public void cerrarLogin() {
        if (stage != null) {
            stage.close();
        }
    }
}