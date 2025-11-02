package mx.uam.ayd.proyecto.presentacion.PerfilCitas;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
/*
 * Esta ventana carga una interfaz diseñada con fxml
 * La ventana te permite escribir el nombre o telefono para buscar su perfil de citas
 * Si no coinciden los datos lanza una ventana de advertencia
 * Tambien se óuede crear un perfil de citas nuevo para lo que se utiliza un boton que lanza una
 * nueva ventana.
 * La ventana muestra los datos en en una tabla
 */

@Component
public class VentanaPelfil {
    private Stage stage;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    private ControladorPerfil controlador;
    private boolean initialized = false;

    public VentanaPelfil() {
        // Constructor vacío
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
            stage.setTitle("Centro Psicológico - Perfil de Citas");

            // Cargar el archivo FXML
            String fxmlPath = "/fxml/ventana-PerfiCitas.fxml";
            java.net.URL fxmlUrl = getClass().getResource(fxmlPath);
            
            if (fxmlUrl == null) {
                System.err.println("ERROR: No se encontró el archivo FXML en: " + fxmlPath);
                crearInterfazEmergencia();
                return;
            }

            System.out.println("Cargando FXML desde: " + fxmlPath);
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            
            // Obtener el controlador del contexto de Spring y establecerlo
            controlador = applicationContext.getBean(ControladorPerfil.class);
            loader.setController(controlador);
            
            System.out.println("Controlador establecido: " + controlador);
            
            Scene scene = new Scene(loader.load(), 800, 600);
            stage.setScene(scene);
            
            System.out.println("FXML cargado exitosamente");

            // Configurar comportamiento de cierre
            stage.setOnCloseRequest(event -> {
                event.consume();
                cerrarVentana();
            });

            initialized = true;
            
        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO al cargar la interfaz:");
            e.printStackTrace();
            crearInterfazEmergenciaConError(e);
        }
    }

    /**
     * Crea una interfaz de emergencia con detalles del error
     */
    private void crearInterfazEmergenciaConError(Exception error) {
        try {
            String mensajeError = "No se pudo cargar la interfaz de Perfil de Citas\n\n" +
                "Error: " + error.getMessage() + "\n\n" +
                "Posibles soluciones:\n" +
                "1. Verificar que el archivo FXML no tenga 'fx:controller'\n" +
                "2. Limpiar y reconstruir el proyecto\n" +
                "3. Verificar que el controlador esté correctamente configurado en Spring";

            Label label = new Label(mensajeError);
            label.setStyle("-fx-font-size: 14px; -fx-padding: 20px; -fx-text-fill: #c0392b;");
            
            VBox root = new VBox(label);
            root.setStyle("-fx-background-color: #f9ebea; -fx-padding: 20px; -fx-border-color: #c0392b; -fx-border-width: 2px;");
            
            Scene scene = new Scene(root, 700, 400);
            stage.setScene(scene);
            stage.setTitle("Error - Centro Psicológico");
            stage.show();
            
        } catch (Exception e2) {
            System.err.println("Error incluso en interfaz de emergencia: " + e2.getMessage());
        }
    }

    /**
     * Crea una interfaz básica de emergencia
     */
    private void crearInterfazEmergencia() {
        crearInterfazEmergenciaConError(new Exception("Archivo FXML no encontrado"));
    }

    /**
     * Muestra la ventana de perfil de citas
     */
    public void muestra() {
        System.out.println("=== SOLICITANDO MOSTRAR VENTANA PERFIL ===");
        
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra());
            return;
        }

        initializeUI();
        
        if (stage != null) {
            stage.show();
            stage.toFront();
            
            // Limpiar campos al mostrar la ventana
            if (controlador != null) {
                controlador.limpiarCampos();
            }
            System.out.println("✓ Ventana mostrada exitosamente");
        } else {
            System.err.println("✗ No se pudo crear la ventana");
        }
    }

    /**
     * Cierra la ventana de perfil de citas
     */
    public void cerrarVentana() {
        if (stage != null) {
            stage.close();
        }
    }

    public Stage getStage() {
        return stage;
    }
}