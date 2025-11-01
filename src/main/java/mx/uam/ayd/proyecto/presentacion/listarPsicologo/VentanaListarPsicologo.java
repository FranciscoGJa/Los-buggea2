package mx.uam.ayd.proyecto.presentacion.listarPsicologo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;
import java.util.List;

/**
 * Ventana para listar los psicólogos registrados en el sistema.
 * 
 * Esta clase representa la capa de presentación (vista) que se encarga de:
 * <ul>
 *   <li>Mostrar la tabla con los datos de todos los psicólogos.</li>
 *   <li>Configurar las columnas de la tabla para mostrar las propiedades del modelo {@link Psicologo}.</li>
 *   <li>Permitir la interacción con el usuario para cerrar o regresar desde esta vista.</li>
 * </ul>
 * 
 * Es controlada por {@link ControlListarPsicologo}, quien se encarga de suministrar
 * la lista de datos y gestionar las acciones de negocio.
 * 
 * El archivo FXML asociado es <code>/fxml/ventanaListarPsicologos.fxml</code>.
 * 
 * @author TechSolutions
 */
@Component
public class VentanaListarPsicologo {

    private ControlListarPsicologo controlListarPsicologo;
    private Stage stage;

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
    @FXML
    private Parent root; 

    /**
     * Establece la referencia al controlador de la vista.
     * 
     * @param controlListarPsicologo instancia de {@link ControlListarPsicologo} que gestiona la lógica de negocio
     */
    public void setControlListarPsicologo(ControlListarPsicologo controlListarPsicologo) {
        this.controlListarPsicologo = controlListarPsicologo;
    }

    /**
     * Muestra la ventana de listado de psicólogos.
     * <p>
     * Si la ventana no ha sido creada aún, carga el archivo FXML, configura la escena y crea el escenario.
     * Luego, carga la lista de psicólogos proporcionada en la tabla.
     * </p>
     *
     * @param psicologos lista de psicólogos a mostrar en la tabla
     */
     public void cargarFXML() {
    if (root != null) return; // ya cargado
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaListarPsicologos.fxml"));
        loader.setController(this);
        root = loader.load();
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Error al cargar la vista de listar psicólogos: " + e.getMessage());
    }

    public void muestra(List<Psicologo> psicologos) {
    cargarFXML(); // asegura que root está cargado

    ObservableList<Psicologo> datos = FXCollections.observableArrayList(psicologos);
    tableViewPsicologos.setItems(datos);
}
public Node getVista() {
    return root;
}




    /**
     * Inicializa las columnas de la tabla para vincularlas a las propiedades del modelo {@link Psicologo}.
     * Este método es invocado automáticamente por JavaFX tras cargar el archivo FXML.
     */
    @FXML
    public void initialize() {
        tableColumnID.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tableColumnNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        tableColumnCorreo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCorreo()));
        tableColumnTelefono.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefono()));
        tableColumnEspecialidad.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEspecialidad().toString()));
    }

    /**
     * Cambia la visibilidad de la ventana.
     *
     * @param visible {@code true} para mostrar la ventana, {@code false} para ocultarla
     */
    public void setVisible(boolean visible) {

    }

    /**
     * Maneja el evento de regresar a la pantalla anterior.
     * <p>
     * Llama al método {@link ControlListarPsicologo#termina()} para cerrar la ventana y terminar el flujo actual.
     * </p>
     */
    public void handleRegresar() {
    }
}
