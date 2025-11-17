package mx.uam.ayd.proyecto.presentacion.CrearPerfilCitas;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.io.IOException;

/*
 * Ventana para crear el perfil de citas de un paciente.
 * Permite al usuario ingresar y guardar la información necesaria.
 * Utiliza JavaFX para la interfaz gráfica y Spring para la gestión de dependencias.
 */
@Component
public class VentanaCrearPerfilCitas {
    private Stage stage;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    private ControladorCrearPerfilCitas controlador;
    private boolean initialized = false;

    public VentanaCrearPerfilCitas() {
        // Constructor vacío requerido por Spring
    }

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
            stage.setTitle("Centro Psicológico - Crear Perfil de Citas");

            // Cargar el archivo FXML
            String fxmlPath = "/fxml/ventana-CrearPerfil.fxml";
            java.net.URL fxmlUrl = getClass().getResource(fxmlPath);
            
            if (fxmlUrl == null) {
                System.err.println("ERROR: No se encontró el archivo FXML en: " + fxmlPath);
                mostrarInterfazEmergencia("Archivo FXML no encontrado: " + fxmlPath);
                return;
            }

            System.out.println("Cargando FXML de crear perfil desde: " + fxmlPath);
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            
            // Configurar el controlador manualmente
            controlador = applicationContext.getBean(ControladorCrearPerfilCitas.class);
            loader.setController(controlador);
            
            System.out.println("Controlador establecido: " + controlador.getClass().getSimpleName());
            
            Scene scene = new Scene(loader.load(), 900, 700);
            stage.setScene(scene);
            
            System.out.println("FXML de crear perfil cargado exitosamente");

            // Configurar comportamiento de cierre
            stage.setOnCloseRequest(event -> {
                event.consume();
                cerrarVentana();
            });

            initialized = true;
            
        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO al cargar la interfaz de crear perfil:");
            e.printStackTrace();
            mostrarInterfazEmergencia("Error al cargar la interfaz: " + e.getMessage());
        }
    }

    private void mostrarInterfazEmergencia(String mensaje) {
        try {
            Label label = new Label(mensaje + "\n\nPor favor, contacte al administrador del sistema.");
            label.setStyle("-fx-font-size: 14px; -fx-padding: 20px; -fx-text-fill: #c0392b;");
            
            VBox root = new VBox(label);
            root.setStyle("-fx-background-color: #f9ebea; -fx-padding: 20px;");
            
            Scene scene = new Scene(root, 600, 400);
            if (stage == null) {
                stage = new Stage();
            }
            stage.setScene(scene);
            stage.setTitle("Error - Centro Psicológico");
            
        } catch (Exception e2) {
            System.err.println("Error incluso en interfaz de emergencia: " + e2.getMessage());
        }
    }

    /**
     * Muestra la ventana de crear perfil de citas - SIN PARÁMETROS
     */
    public void muestra() {
        System.out.println("SOLICITANDO MOSTRAR VENTANA CREAR PERFIL");
        
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra());
            return;
        }

        initializeUI();
        
        if (stage != null) {
            stage.show();
            stage.toFront();
            System.out.println("Ventana de crear perfil mostrada exitosamente");
        } else {
            System.err.println("No se pudo crear la ventana de crear perfil");
            mostrarAlerta("Error", "No se pudo inicializar la ventana de crear perfil");
        }
    }

    /**
     * Cierra la ventana de crear perfil de citas
     */
    public void cerrarVentana() {
        if (stage != null) {
            stage.close();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public Stage getStage() {
        return stage;
    }
}