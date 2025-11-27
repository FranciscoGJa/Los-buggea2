package mx.uam.ayd.proyecto.presentacion.listarpacientes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import mx.uam.ayd.proyecto.negocio.modelo.BateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.HistorialClinico;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.presentacion.listarPsicologo.ControlListarPsicologo;

/**
 * Ventana de interfaz gráfica para listar pacientes y gestionar la visualización de sus datos clínicos.
 * * Esta vista permite:
 * <ul>
 * <li>Visualizar una tabla con los pacientes registrados.</li>
 * <li>Consultar y mostrar el historial clínico de un paciente seleccionado.</li>
 * <li>Listar las baterías clínicas asociadas a un paciente y mostrar sus detalles.</li>
 * <li>Guardar comentarios actualizados en una batería clínica.</li>
 * </ul>
 * * @author TechSolutions
 */
@Component
public class VentanaListarPacientes {
   private ControlListarPsicologo controlListarPsicologo; // No borrar, lo utiliza el controlador listar psicologos
   
    // Componentes de la interfaz de usuario inyectados desde el archivo FXML
    @FXML private TableView<Paciente> tablaPacientes;
    @FXML private TableColumn<Paciente, String> columnaNombre;
    @FXML private TableColumn<Paciente, String> columnaCorreo;
    @FXML private TableColumn<Paciente, String> columnaTelefono;
    
    @FXML private ListView<String> listaBaterias;
    @FXML private Label puntajeObtenidoLabel;
    @FXML private TextArea comentariosTextArea;
    @FXML private Button btnAbrirDetalles;
    @FXML private Button btnGuardarComentarios;

    // Paneles para mostrar/ocultar dinámicamente la información del historial clínico
    @FXML private VBox historialPlaceholder; 
    @FXML private GridPane historialDetailsPane; 
    
    @FXML private Label lblHistorialFecha;
    @FXML private Label lblHistorialMotivo;
    @FXML private Label lblHistorialConsumo;
    @FXML private Label lblHistorialDescripcion;
    @FXML private Label lblHistorialConsentimiento;
    
    // Variables de estado y control
    private Parent root; 
    private ControlListarPacientes control; 
    private BateriaClinica bateriaSeleccionada; 
    private List<BateriaClinica> bateriasDelPaciente; 

    // Metodo para cargar el FXML una sola vez
    public void cargarFXML() {
        if (root != null) {
            return; 
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-listar-pacientes.fxml"));
            loader.setController(this);
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            muestraDialogoDeError("Error al cargar la vista de pacientes.");
        }
    }

    public Node getVista() {
        return root;
    }

    /*Muestra la ventana de listado de pacientes. */
    public void muestra(ControlListarPacientes control, List<Paciente> pacientes) {
        this.control = control;

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> muestra(control, pacientes));
            return;
        }

        // Configura columnas
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        columnaTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        // Carga los pacientes en la tabla
        tablaPacientes.setItems(FXCollections.observableArrayList(pacientes));

        // Listener para selección de batería
        listaBaterias.getSelectionModel().selectedIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            // Usamos el índice para recuperar el objeto BateriaClinica real de la lista bateriasDelPaciente
            if (newIndex.intValue() >= 0 && bateriasDelPaciente != null && newIndex.intValue() < bateriasDelPaciente.size()) {
                BateriaClinica seleccionada = bateriasDelPaciente.get(newIndex.intValue());
                control.seleccionarBateria(seleccionada);
                
                // Habilitar controles
                btnAbrirDetalles.setDisable(false);
                btnGuardarComentarios.setDisable(false);
                comentariosTextArea.setDisable(false);
            } else {
                // Si no hay selección, deshabilitar y limpiar
                control.seleccionarBateria(null); // Esto limpiará mostrarDetallesBateria o limpiar
                btnAbrirDetalles.setDisable(true);
                btnGuardarComentarios.setDisable(true);
                comentariosTextArea.setDisable(true);
                comentariosTextArea.clear();
                puntajeObtenidoLabel.setText("-");
            }
        });

        // Selección de paciente
        tablaPacientes.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldValue, newValue) -> control.seleccionarPaciente(newValue)
        );
        
        // Estado inicial
        limpiarDetallesDeBateria();
    }

    /*  Muestra los detalles de un historial clínico en la sección correspondiente. */
    public void mostrarHistorialEnPestana(HistorialClinico historial) {
        historialPlaceholder.setVisible(false);
        historialPlaceholder.setManaged(false); 
        historialDetailsPane.setVisible(true);
        historialDetailsPane.setManaged(true);
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        lblHistorialFecha.setText(historial.getFechaElaboracion() != null ? formatter.format(historial.getFechaElaboracion()) : "N/A");
        lblHistorialMotivo.setText(historial.getMotivo());
        lblHistorialConsumo.setText(historial.getConsumoDrogas());
        lblHistorialDescripcion.setText(historial.getDescripcionDrogas() != null && !historial.getDescripcionDrogas().isEmpty() ? historial.getDescripcionDrogas() : "N/A");
        lblHistorialConsentimiento.setText(historial.isConsentimientoAceptado() ? "Sí" : "No");
    }

    public void limpiarHistorialEnPestana() {
        historialPlaceholder.setVisible(true);
        historialPlaceholder.setManaged(true);
        historialDetailsPane.setVisible(false);
        historialDetailsPane.setManaged(false);
    }
    
    /**
     * Limpia y resetea todos los campos relacionados con los detalles de las baterías.
     */
    public void limpiarDetallesDeBateria() {
        this.bateriaSeleccionada = null;
        this.bateriasDelPaciente = null;
        listaBaterias.getItems().clear();
        puntajeObtenidoLabel.setText("-");
        comentariosTextArea.clear();
        comentariosTextArea.setDisable(true);
        btnAbrirDetalles.setDisable(true);
        btnGuardarComentarios.setDisable(true);
    }

    /* Muestra la lista de baterías clínicas de un paciente*/
    public void mostrarBaterias(List<BateriaClinica> baterias) {
        this.bateriasDelPaciente = baterias;
        if (baterias != null) {
            // Usa Streams para mapear la lista de objetos Bateria a una lista de sus nombres
            List<String> nombresBaterias = baterias.stream()
                .map(b -> b.getTipoDeBateria().toString() + 
                     (b.getFechaAplicacion() != null ? " (" + new SimpleDateFormat("dd/MM").format(b.getFechaAplicacion()) + ")" : ""))
                .collect(Collectors.toList());
            listaBaterias.setItems(FXCollections.observableArrayList(nombresBaterias));
        } else {
            listaBaterias.getItems().clear();
        }
    }

    /* Muestra los detalles (calificación y comentarios) de una batería específica.*/
    public void mostrarDetallesBateria(BateriaClinica bateria) {
        this.bateriaSeleccionada = bateria;
        if (bateria != null) {
            puntajeObtenidoLabel.setText(String.valueOf(bateria.getCalificacion()));
            comentariosTextArea.setText(bateria.getComentarios());
        }
    }

    public void cierra() { 
       limpiarDetallesDeBateria();
       limpiarHistorialEnPestana();
    }
    
    public void muestraDialogoDeInformacion(String mensaje) { 
        Platform.runLater(() -> new Alert(AlertType.INFORMATION, mensaje).showAndWait());
    }

    public void muestraDialogoDeError(String mensaje) { 
        Platform.runLater(() -> new Alert(AlertType.ERROR, mensaje).showAndWait());
    }

    /* Boton guardar cambios*/
    @FXML 
    private void handleGuardarComentarios() { 
        if (bateriaSeleccionada != null) {
            control.guardarComentarios(bateriaSeleccionada, comentariosTextArea.getText()); 
        }
    }

    /* Boton cerrar*/
    @FXML 
    private void handleCerrar() { 
        control.cerrar(); 
    }

    /* Boton abrir detalles*/
    @FXML 
    private void handleAbrirDetalles() { 
        if (bateriaSeleccionada != null) { 
            control.abrirDetallesBateria(bateriaSeleccionada); 
        } 
    }
}