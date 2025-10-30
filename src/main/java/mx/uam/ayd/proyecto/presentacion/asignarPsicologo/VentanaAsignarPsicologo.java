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

import java.io.IOException; // Importar IOException
import java.util.List;

@Slf4j
@Component
public class VentanaAsignarPsicologo {
    private Stage stage;
    private boolean initialized = false;
    private ControlAsignarPsicologo controlAsignarPsicologo;

    // --- Componentes FXML ---
    @FXML private TableView<Psicologo> tableViewPsicologos;
    @FXML private TableColumn<Psicologo, Integer> tableColumnID;
    @FXML private TableColumn<Psicologo, String> tableColumnNombre;
    @FXML private TableColumn<Psicologo, String> tableColumnCorreo;
    @FXML private TableColumn<Psicologo, String> tableColumnTelefono;
    @FXML private TableColumn<Psicologo, String> tableColumnEspecialidad;

    private Paciente pacienteActual; // Guardamos el paciente

    public void setControlAsignarPsicologo(ControlAsignarPsicologo controlAsignarPsicologo) {
        this.controlAsignarPsicologo = controlAsignarPsicologo;
    }

    /**
     * Muestra la ventana. Inicializa la UI si es necesario.
     */
    public void muestra(Paciente paciente, List<Psicologo> psicologos) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> muestra(paciente, psicologos));
            return;
        }
        this.pacienteActual = paciente;

        // Asegura que la UI esté inicializada ANTES de intentar mostrar
        initializeUI();

        // Verifica que el stage y la tabla existan después de inicializar
        if (stage == null || tableViewPsicologos == null) {
            log.error("El Stage o TableView no se inicializaron correctamente. No se puede mostrar la ventana.");
            muestraDialogoConMensaje("Error crítico: No se pudo preparar la ventana 'Asignar Psicólogo'. Contacte al administrador.");
            return; // No continuar si la inicialización falló
        }

        limpiarCampos(); // Limpia la tabla

        // Carga los psicólogos ANTES de ajustar tamaño y mostrar
        if (psicologos != null) {
            tableViewPsicologos.getItems().setAll(psicologos);
            if (psicologos.isEmpty()) {
                // Opcional: Mostrar mensaje si no hay psicólogos
                // muestraDialogoConMensaje("No se encontraron psicólogos disponibles para este paciente.");
            }
        } else {
            log.warn("La lista de psicólogos es nula.");
            // Opcional: Mostrar mensaje
            // muestraDialogoConMensaje("Error al obtener la lista de psicólogos.");
        }

        stage.sizeToScene(); // Ajusta tamaño al contenido
        stage.show();        // Muestra la ventana
        stage.toFront();     // La trae al frente
    }


    /**
     * Inicializa la interfaz de usuario (Stage, Scene, FXML).
     * Se llama una sola vez.
     */
    private void initializeUI() {
        if (initialized) {
            return; // Ya inicializado
        }

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            // Crea el Stage solo si no existe
            if (stage == null) {
                stage = new Stage();
                stage.setTitle("Asignar Psicólogo");
                // Opcional: stage.initModality(Modality.APPLICATION_MODAL); si quieres que bloquee otras ventanas
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaAsignarPsicologo.fxml"));
            loader.setController(this); // Asegura que esta clase sea el controlador

            // Carga el FXML y crea la Scene
            Scene scene = new Scene(loader.load());

            // *** LA LÍNEA CLAVE QUE FALTABA ***
            stage.setScene(scene); // Establece la escena cargada en el stage

            // Aplica el CSS (Opcional, si lo tienes)
            try {
                 String css = getClass().getResource("/css/style.css").toExternalForm();
                 scene.getStylesheets().add(css);
            } catch (NullPointerException e) {
                 log.warn("No se encontró el archivo CSS global en /css/style.css");
            }


            stage.setResizable(true); // Permitir redimensionar

            // Configura las CellValueFactory DESPUÉS de cargar el FXML (cuando los @FXML ya están inyectados)
            configureTableColumns();

            initialized = true; // Marca como inicializado

        } catch (IOException e) {
            log.error("Error al cargar FXML /fxml/ventanaAsignarPsicologo.fxml: {}", e.getMessage(), e);
            // Mostrar error al usuario
            Platform.runLater(() -> muestraDialogoConMensaje("Error al cargar la ventana de asignación: " + e.getMessage()));
            stage = null; // Anula stage si falla
        } catch (Exception e) { // Captura otros posibles errores
            log.error("Error inesperado al inicializar la UI de Asignar Psicólogo: {}", e.getMessage(), e);
            Platform.runLater(() -> muestraDialogoConMensaje("Error inesperado al preparar la ventana: " + e.getMessage()));
            stage = null; // Anula stage si falla
        }
    }

    /**
     * Configura las CellValueFactory para las columnas de la tabla.
     * Llamar DESPUÉS de que FXMLLoader haya inyectado los @FXML.
     */
    private void configureTableColumns() {
        // Asegúrate de que las columnas no sean null antes de configurarlas
        if (tableColumnID != null) {
            tableColumnID.setCellValueFactory(cellData -> {
                Psicologo p = cellData.getValue();
                return new javafx.beans.property.SimpleIntegerProperty(p != null ? p.getId() : 0).asObject();
            });
        }
        if (tableColumnNombre != null) {
            tableColumnNombre.setCellValueFactory(cellData -> {
                Psicologo p = cellData.getValue();
                return new javafx.beans.property.SimpleStringProperty(p != null ? p.getNombre() : "");
            });
        }
        if (tableColumnCorreo != null) {
            tableColumnCorreo.setCellValueFactory(cellData -> {
                Psicologo p = cellData.getValue();
                return new javafx.beans.property.SimpleStringProperty(p != null ? p.getCorreo() : "");
            });
        }
        if (tableColumnTelefono != null) {
            tableColumnTelefono.setCellValueFactory(cellData -> {
                Psicologo p = cellData.getValue();
                return new javafx.beans.property.SimpleStringProperty(p != null ? p.getTelefono() : "");
            });
        }
        if (tableColumnEspecialidad != null) {
            tableColumnEspecialidad.setCellValueFactory(cellData -> {
                Psicologo p = cellData.getValue();
                // Maneja el caso en que la especialidad pueda ser null
                String especialidadStr = (p != null && p.getEspecialidad() != null) ? p.getEspecialidad().toString() : "N/A";
                return new javafx.beans.property.SimpleStringProperty(especialidadStr);
            });
        }
    }


    /** Limpia la tabla de psicólogos. */
    private void limpiarCampos() {
        if (tableViewPsicologos != null) {
            tableViewPsicologos.getItems().clear();
        }
    }

    /** Cambia la visibilidad de la ventana. */
    public void setVisible(boolean visible) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> setVisible(visible));
            return;
        }

        // Si se pide mostrar y no está inicializada, inicializarla
        if (visible && !initialized) {
            initializeUI();
        }

        // Si la inicialización falló, stage será null
        if (stage != null) {
            if (visible) {
                stage.show();
            } else {
                stage.hide();
            }
        } else if (visible) {
            // Solo muestra error si se intentó mostrar y falló la inicialización
             log.error("Intento de mostrar VentanaAsignarPsicologo pero el stage es nulo (falló la inicialización).");
             muestraDialogoConMensaje("No se puede mostrar la ventana 'Asignar Psicólogo' debido a un error previo.");
        }
    }

    /** Muestra un diálogo informativo. */
    public void muestraDialogoConMensaje(String mensaje) {
        // Asegura que se ejecute en el hilo de JavaFX
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> muestraDialogoConMensaje(mensaje));
            return;
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null); // Sin cabecera
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /** Acción del botón "Asignar". */
    @FXML
    private void handleAsignar() {
         // Verifica que la tabla no sea null antes de acceder a ella
         if (tableViewPsicologos == null) {
              log.error("handleAsignar llamado pero tableViewPsicologos es nulo.");
              muestraDialogoConMensaje("Error interno: La tabla de psicólogos no está disponible.");
              return;
         }

        if (tableViewPsicologos.getSelectionModel().isEmpty()) {
            muestraDialogoConMensaje("Por favor, selecciona un psicólogo de la lista.");
            return;
        }

        Psicologo psicologoSeleccionado = tableViewPsicologos.getSelectionModel().getSelectedItem();
        if (psicologoSeleccionado == null) {
            // Esto no debería pasar si !isEmpty(), pero es una doble verificación
            muestraDialogoConMensaje("No se ha seleccionado ningún psicólogo válido.");
            return;
        }

        // Verifica que pacienteActual no sea null
        if (pacienteActual == null) {
             log.error("handleAsignar llamado pero pacienteActual es nulo.");
             muestraDialogoConMensaje("Error interno: No se ha especificado el paciente.");
             return;
        }

        // Verifica que el control no sea null
        if (controlAsignarPsicologo == null) {
             log.error("handleAsignar llamado pero controlAsignarPsicologo es nulo.");
             muestraDialogoConMensaje("Error interno: El controlador no está disponible.");
             return;
        }

        controlAsignarPsicologo.asignarPsicologo(pacienteActual, psicologoSeleccionado);
    }
}