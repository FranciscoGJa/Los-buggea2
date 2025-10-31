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
 * 
 * Esta vista permite:
 * <ul>
 *   <li>Visualizar una tabla con los pacientes registrados.</li>
 *   <li>Consultar y mostrar el historial clínico de un paciente seleccionado.</li>
 *   <li>Listar las baterías clínicas asociadas a un paciente y mostrar sus detalles (puntaje y comentarios).</li>
 *   <li>Guardar comentarios actualizados en una batería clínica.</li>
 *   <li>Abrir una vista con información detallada de una batería.</li>
 * </ul>
 *
 * La clase actúa como controlador de la vista JavaFX definida en el archivo FXML correspondiente,
 * y delega la lógica de negocio a {@link ControlListarPacientes}.
 * 
 * @author TechSolutions
 */
@Component
public class VentanaListarPacientes {
   private ControlListarPsicologo controlListarPsicologo;//No borrar lo utiliza el controlador listar psicologos
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
    @FXML private VBox historialPlaceholder; // Se muestra cuando no hay historial
    @FXML private GridPane historialDetailsPane; // Se muestra con los detalles del historial
    
    // Etiquetas para los detalles del historial clínico
    @FXML private Label lblHistorialFecha;
    @FXML private Label lblHistorialMotivo;
    @FXML private Label lblHistorialConsumo;
    @FXML private Label lblHistorialDescripcion;
    @FXML private Label lblHistorialConsentimiento;
    
    // Variables de estado y control
    private Parent root; // La ventana principal (escenario) de JavaFX
    private ControlListarPacientes control; // Referencia al controlador para delegar la lógica de negocio
    private BateriaClinica bateriaSeleccionada; // Almacena la batería seleccionada actualmente en la lista
    private List<BateriaClinica> bateriasDelPaciente; // Lista de todas las baterías del paciente seleccionado

    /**
     * Muestra la ventana de listado de pacientes.
     * Carga la vista FXML, configura los componentes y establece los listeners de eventos.
     *
     * @param control El controlador que gestionará la lógica de esta vista.
     * @param pacientes La lista inicial de pacientes a mostrar en la tabla.
     */
    //Metodo para cargar el FXML una sola vez
    public void cargarFXML() {
    if (root != null) {
        return; // Ya cargado, no recargar
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
//Metodo para obtener la vista cargada 
public Node getVista() {
    return root;
}

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

    // Listeners
    listaBaterias.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
        boolean isSelected = newValue != null;
        btnAbrirDetalles.setDisable(!isSelected);
        btnGuardarComentarios.setDisable(!isSelected);

        if (isSelected && bateriasDelPaciente != null) {
            bateriasDelPaciente.stream()
                .filter(b -> b.getTipoDeBateria().toString().equals(newValue))
                .findFirst()
                .ifPresent(b -> control.seleccionarBateria(b));
        }
    });

    tablaPacientes.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldValue, newValue) -> control.seleccionarPaciente(newValue)
    );
}


    /**
     * Muestra los detalles de un historial clínico en la sección correspondiente de la UI.
     * Oculta el placeholder y hace visible el panel de detalles.
     */
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

    /**
     * Limpia la sección del historial clínico, mostrando el placeholder inicial.
     * Se usa cuando se deselecciona un paciente o se selecciona uno sin historial.
     */
    public void limpiarHistorialEnPestana() {
        historialPlaceholder.setVisible(true);
        historialPlaceholder.setManaged(true);
        historialDetailsPane.setVisible(false);
        historialDetailsPane.setManaged(false);
    }
    
    /**
     * Limpia y resetea todos los campos relacionados con los detalles de las baterías.
     * Se llama cuando cambia el paciente seleccionado.
     */
    public void limpiarDetallesDeBateria() {
        this.bateriaSeleccionada = null;
        this.bateriasDelPaciente = null;
        listaBaterias.getItems().clear();
        puntajeObtenidoLabel.setText("-");
        comentariosTextArea.clear();
        btnAbrirDetalles.setDisable(true);
        btnGuardarComentarios.setDisable(true);
    }

    /**
     * Muestra la lista de baterías clínicas de un paciente en el ListView.
     *
     * @param baterias La lista de baterías a mostrar. Si es nula o vacía, la lista se limpia.
     */
    public void mostrarBaterias(List<BateriaClinica> baterias) {
        this.bateriasDelPaciente = baterias;
        if (baterias != null) {
            // Usa Streams para mapear la lista de objetos Bateria a una lista de sus nombres (String)
            List<String> nombresBaterias = baterias.stream()
                .map(bateria -> bateria.getTipoDeBateria().toString())
                .collect(Collectors.toList());
            listaBaterias.setItems(FXCollections.observableArrayList(nombresBaterias));
        } else {
            listaBaterias.getItems().clear();
        }
    }

    /**
     * Muestra los detalles (calificación y comentarios) de una batería específica.
     *
     * @param bateria La batería seleccionada cuyos detalles se van a mostrar.
     */
    public void mostrarDetallesBateria(BateriaClinica bateria) {
        this.bateriaSeleccionada = bateria;
        puntajeObtenidoLabel.setText(String.valueOf(bateria.getCalificacion()));
        comentariosTextArea.setText(bateria.getComentarios());
    }

    /**
     * Cierra la ventana actual de listado de pacientes.
     */
    public void cierra() { 
       limpiarDetallesDeBateria();
       limpiarHistorialEnPestana();
    }
    
    /**
     * Muestra un cuadro de diálogo informativo con el mensaje proporcionado.
     *
     * @param mensaje Texto que se mostrará en el cuadro de diálogo.
     */
    public void muestraDialogoDeInformacion(String mensaje) { 
        new Alert(AlertType.INFORMATION, mensaje).showAndWait(); 
    }

    /**
     * Muestra un cuadro de diálogo de error con el mensaje proporcionado.
     *
     * @param mensaje Texto que se mostrará en el cuadro de diálogo.
     */
    public void muestraDialogoDeError(String mensaje) { 
        new Alert(AlertType.ERROR, mensaje).showAndWait(); 
    }

    /**
     * Evento manejador para el botón de guardar comentarios.
     * Llama al controlador para persistir los comentarios de la batería seleccionada.
     */
    @FXML 
    private void handleGuardarComentarios() { 
        control.guardarComentarios(bateriaSeleccionada, comentariosTextArea.getText()); 
    }

    /**
     * Evento manejador para el botón de cerrar.
     * Delegar la acción de cierre al controlador.
     */
    @FXML 
    private void handleCerrar() { 
        control.cerrar(); 
    }

    /**
     * Evento manejador para el botón de abrir detalles.
     * Solicita al controlador que muestre los detalles de la batería seleccionada.
     */
    @FXML 
    private void handleAbrirDetalles() { 
        if (bateriaSeleccionada != null) { 
            control.abrirDetallesBateria(bateriaSeleccionada); 
        } 
    }
}