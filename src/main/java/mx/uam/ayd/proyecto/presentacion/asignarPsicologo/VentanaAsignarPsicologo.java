package mx.uam.ayd.proyecto.presentacion.asignarPsicologo;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Ventana para asignar un psicólogo a un paciente.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Cargar y mostrar la interfaz JavaFX para la asignación.</li>
 *   <li>Mostrar la lista de psicólogos disponibles en una tabla.</li>
 *   <li>Permitir la selección de un psicólogo y delegar la asignación al {@link ControlAsignarPsicologo}.</li>
 *   <li>Mostrar mensajes informativos al usuario.</li>
 * </ul>
 * </p>
 *
 * <p>Flujo típico:
 * <ol>
 *   <li>Invocar {@link #muestra(Paciente, List)} para abrir la ventana con la lista de psicólogos.</li>
 *   <li>Seleccionar un psicólogo y presionar el botón de asignar.</li>
 *   <li>La acción se delega al controlador, que realiza la asignación.</li>
 * </ol>
 * </p>
 *
 * @version 1.0
 */
@Slf4j
@Component
public class VentanaAsignarPsicologo {
    private Stage stage;
    private boolean initialized = false;
    private ControlAsignarPsicologo controlAsignarPsicologo;

    @FXML
    private TableView<Psicologo> tableViewPsicologos;

    @FXML
    private TableColumn<Psicologo, Integer> tableColumnID;

    @FXML
    private TableColumn<Psicologo, String> tableColumnNombre;

    @FXML
    private TableColumn<Psicologo, String> tableColumnCorreo;

    @FXML
    private TableColumn<Psicologo, String> tableColumnTelefono;

    @FXML
    private TableColumn<Psicologo, String> tableColumnEspecialidad;

    /**
     * Asigna el controlador que gestionará las acciones de esta ventana.
     * @param controlAsignarPsicologo controlador de la ventana
     */
    public void setControlAsignarPsicologo(ControlAsignarPsicologo controlAsignarPsicologo) {
        this.controlAsignarPsicologo = controlAsignarPsicologo;
    }

    /**
     * Inicializa la UI: carga el FXML, crea el Stage y configura la tabla.
     * <p>Si no se está en el hilo de JavaFX, la acción se reprograma con {@link Platform#runLater(Runnable)}.</p>
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
            stage.setTitle("Asignar Psicólogo");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaAsignarPsicologo.fxml"));
            loader.setController(this);
            
            stage.setScene(new Scene(loader.load()));
            
            stage.setResizable(false);

            // Configurar las columnas de la tabla

            tableColumnID.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
            tableColumnNombre.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
            tableColumnCorreo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCorreo()));
            tableColumnTelefono.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTelefono()));
            tableColumnEspecialidad.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEspecialidad().toString()));

            initialized = true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error al inicializar la interfaz de usuario {}", e.getMessage());
        }
    }

    /**
     * Limpia la tabla de psicólogos.
     */
    private void limpiarCampos() {
        tableViewPsicologos.getItems().clear();
    }

    private Paciente pacienteActual;

    /**
     * Muestra la ventana con la lista de psicólogos disponibles para un paciente.
     *
     * @param paciente paciente al que se le asignará el psicólogo
     * @param psicologos lista de psicólogos filtrados según la edad del paciente
     */
    public void muestra(Paciente paciente, List<Psicologo> psicologos) {
        if(!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> muestra(paciente, psicologos));
            return;
        }
        this.pacienteActual = paciente;

        initializeUI();
        limpiarCampos();
        stage.show();
        stage.toFront();
        // Cargar los psicólogos en la tabla
        tableViewPsicologos.getItems().setAll(psicologos);
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
     * Acción del botón "Asignar".
     * <p>Valida que se haya seleccionado un psicólogo y delega la asignación al controlador.</p>
     */
    @FXML
    private void handleAsignar() {
        if (tableViewPsicologos.getSelectionModel().isEmpty()) {
            muestraDialogoConMensaje("Por favor, selecciona un psicólogo.");
            return;
        }

        Psicologo psicologoSeleccionado = tableViewPsicologos.getSelectionModel().getSelectedItem();
        if (psicologoSeleccionado == null) {
            muestraDialogoConMensaje("No se ha seleccionado ningún psicólogo.");
            return;
        }

        controlAsignarPsicologo.asignarPsicologo(pacienteActual, psicologoSeleccionado);
        
    }

}
