package mx.uam.ayd.proyecto.presentacion.HistorialCitas;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.ServicioHistorialCitas;
import mx.uam.ayd.proyecto.negocio.ServicioCita;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;
import mx.uam.ayd.proyecto.presentacion.CrearCita.VentanaNuevaCita;
import java.util.List;

/*
 * Controlador de la ventana de historial de citas
 * Muestra el historial de citas de un perfil seleccionado
 * Permite ver detalles de citas y crear nuevas citas
 * Carga los datos del perfil y su historial de citas
 * Se inyectan los servicios necesarios para obtener los datos
 * Se manejan eventos de botones para nuevas citas y ver detalles
 * Se cierra la ventana al finalizar
 * Utiliza JavaFX para la interfaz gráfica
 * Se anota como componente de Spring para inyección de dependencias
 * Se registran mensajes de depuración en consola
 */
@Component
public class ControladorHistorialCitas {
    
    @FXML private Label lblNombrePaciente;
    @FXML private Label lblEdadSexo;
    @FXML private Label lblContacto;
    @FXML private TableView<Cita> tablaCitas;
    @FXML private TableColumn<Cita, String> colFecha;
    @FXML private TableColumn<Cita, String> colHora;
    @FXML private TableColumn<Cita, String> colEstado;
    @FXML private TableColumn<Cita, String> colMotivo;
    @FXML private Button btnNuevaCita;
    @FXML private Button btnVerDetalles;
    @FXML private Button btnCerrar;
    
    @Autowired
    private ServicioHistorialCitas servicioHistorial;
    
    @Autowired
    private ServicioCita servicioCita;
    
    @Autowired
    private VentanaNuevaCita ventanaNuevaCita;
    
    private PerfilCitas perfilActual;
    
    public ControladorHistorialCitas() {
        System.out.println("ControladorHistorialCitas instanciado por Spring");
    }
    
    @FXML
    public void initialize() {
        System.out.println("Initialize() llamado en ControladorHistorialCitas");
        System.out.println("Servicios inyectados:");
        System.out.println("  - servicioHistorial: " + (servicioHistorial != null));
        System.out.println("  - servicioCita: " + (servicioCita != null));
        System.out.println("  - ventanaNuevaCita: " + (ventanaNuevaCita != null));
        
        configurarTabla();
        
        // Verificar que los componentes FXML se inyectaron
        System.out.println("Componentes FXML:");
        System.out.println("  - lblNombrePaciente: " + (lblNombrePaciente != null));
        System.out.println("  - tablaCitas: " + (tablaCitas != null));
        System.out.println("  - btnNuevaCita: " + (btnNuevaCita != null));
    }
    
    private void configurarTabla() {
        if (colFecha != null) {
            colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaCita"));
        }
        if (colHora != null) {
            colHora.setCellValueFactory(new PropertyValueFactory<>("horaCita"));
        }
        if (colEstado != null) {
            colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoCita"));
        }
        if (colMotivo != null) {
            colMotivo.setCellValueFactory(new PropertyValueFactory<>("detallesAdicionalesPaciente"));
        }
    }
    
    /**
     * Carga los datos del perfil y su historial de citas
     */
    public void cargarPerfil(PerfilCitas perfil) {
        System.out.println("Cargando perfil en historial: " + perfil.getNombreCompleto());
        this.perfilActual = perfil;
        actualizarInformacionPerfil();
        cargarHistorialCitas();
    }
    
    private void actualizarInformacionPerfil() {
        if (perfilActual != null && lblNombrePaciente != null) {
            lblNombrePaciente.setText(perfilActual.getNombreCompleto());
            lblEdadSexo.setText(perfilActual.getEdad() + " años • " + perfilActual.getSexo());
            
            String contacto = "";
            if (perfilActual.getTelefono() != null && !perfilActual.getTelefono().isEmpty()) {
                contacto += "Tel: " + perfilActual.getTelefono();
            }
            if (perfilActual.getEmail() != null && !perfilActual.getEmail().isEmpty()) {
                if (!contacto.isEmpty()) contacto += " • ";
                contacto += "Email: " + perfilActual.getEmail();
            }
            if (contacto.isEmpty()) {
                contacto = "Sin información de contacto";
            }
            lblContacto.setText(contacto);
            
            System.out.println("Información del perfil actualizada");
        }
    }
    
    private void cargarHistorialCitas() {
        if (perfilActual != null && tablaCitas != null) {
            try {
                List<Cita> citas = servicioHistorial.obtenerHistorialPorPerfil(perfilActual.getIdPerfil());
                tablaCitas.getItems().setAll(citas);
                System.out.println("Citas cargadas: " + citas.size());
            } catch (Exception e) {
                System.err.println("Error al cargar citas: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Abre la ventana para crear nueva cita
     */
    @FXML
    public void nuevaCita() {
        System.out.println("Botón nueva cita presionado");
        if (perfilActual != null) {
            try {
                ventanaNuevaCita.mostrar(perfilActual);
                // Recargar historial después de crear cita
                cargarHistorialCitas();
            } catch (Exception e) {
                System.err.println("Error al abrir nueva cita: " + e.getMessage());
                mostrarAlerta("Error", "No se pudo abrir la ventana de nueva cita: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Error", "No hay perfil seleccionado");
        }
    }
    
    /**
     * Ver detalles de cita seleccionada
     */
    @FXML
    public void verDetallesCita() {
        System.out.println("Botón ver detalles presionado");
        if (tablaCitas != null) {
            Cita citaSeleccionada = tablaCitas.getSelectionModel().getSelectedItem();
            if (citaSeleccionada != null) {
                StringBuilder detalles = new StringBuilder();
                detalles.append("Fecha: ").append(citaSeleccionada.getFechaCita()).append("\n");
                detalles.append("Hora: ").append(citaSeleccionada.getHoraCita()).append("\n");
                detalles.append("Estado: ").append(citaSeleccionada.getEstadoCita()).append("\n");
                detalles.append("Detalles: ").append(citaSeleccionada.getDetallesAdicionalesPaciente()).append("\n");
                
                if (citaSeleccionada.getNotaPostSesion() != null) {
                    detalles.append("Notas post-sesión: ").append(citaSeleccionada.getNotaPostSesion());
                }
                
                mostrarAlerta("Detalles de Cita", detalles.toString());
            } else {
                mostrarAlerta("Información", "Seleccione una cita para ver detalles");
            }
        }
    }
    
    @FXML
    public void cerrar() {
        System.out.println("Cerrando ventana de historial");
        // Cerrar ventana actual
        if (lblNombrePaciente != null) {
            javafx.stage.Stage stage = (javafx.stage.Stage) lblNombrePaciente.getScene().getWindow();
            stage.close();
        }
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}