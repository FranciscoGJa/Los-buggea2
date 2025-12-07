package mx.uam.ayd.proyecto.presentacion.HistorialCitas;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.ServicioHistorialCitas;
import mx.uam.ayd.proyecto.negocio.ServicioCita;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ControladorHistorialCitas {

    // Labels del encabezado
    @FXML
    private Label lblNombrePaciente;

    @FXML
    private Label lblEdadSexo;

    @FXML
    private Label lblContacto;

    // Tabla y columnas
    @FXML
    private TableView<Cita> tablaCitas;

    @FXML
    private TableColumn<Cita, String> colFecha;

    @FXML
    private TableColumn<Cita, String> colHora;

    @FXML
    private TableColumn<Cita, String> colEstado;

    @FXML
    private TableColumn<Cita, String> colMotivo;

    // Botones
    @FXML
    private Button btnNuevaCita;

    @FXML
    private Button btnReagendar;

    @FXML
    private Button btnVerDetalles;

    @FXML
    private Button btnCerrar;

    @Autowired
    private ServicioHistorialCitas servicioHistorial;

    @Autowired
    private ServicioCita servicioCita;

    private PerfilCitas perfilActual;

    @FXML
    public void initialize() {
        System.out.println("Initialize() llamado en ControladorHistorialCitas");
        System.out.println("Servicios inyectados:");
        System.out.println("  - servicioHistorial: " + (servicioHistorial != null));
        System.out.println("  - servicioCita: " + (servicioCita != null));
        System.out.println("Componentes FXML:");
        System.out.println("  - lblNombrePaciente: " + (lblNombrePaciente != null));
        System.out.println("  - tablaCitas: " + (tablaCitas != null));
        System.out.println("  - btnNuevaCita: " + (btnNuevaCita != null));
        System.out.println("  - btnReagendar: " + (btnReagendar != null));
        System.out.println("  - btnVerDetalles: " + (btnVerDetalles != null));
        System.out.println("  - btnCerrar: " + (btnCerrar != null));

        if (tablaCitas != null) {
            tablaCitas.setPlaceholder(
                    new Label("Este paciente aún no tiene citas registradas.")
            );
        }

        // Si quieres, puedes configurar explícitamente las columnas.
        // No es estrictamente necesario porque ya tienes PropertyValueFactory en el FXML,
        // pero NO hace daño tenerlo duplicado.
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
     * Carga la información del perfil y su historial de citas.
     */
    public void cargarPerfil(PerfilCitas perfil) {
        this.perfilActual = perfil;

        System.out.println("Cargando perfil en historial: " + perfil.getNombreCompleto());

        if (lblNombrePaciente != null) {
            lblNombrePaciente.setText(perfil.getNombreCompleto());
        }
        if (lblEdadSexo != null) {
            lblEdadSexo.setText(perfil.getEdad() + " años • " + perfil.getSexo());
        }
        if (lblContacto != null) {
            lblContacto.setText("Tel: " + perfil.getTelefono());
        }

        try {
            // Usamos el ID del perfil para obtener el historial
            List<Cita> citas = servicioHistorial.obtenerHistorialPorPerfil(perfil.getIdPerfil());
            if (tablaCitas != null) {
                tablaCitas.getItems().setAll(citas);
            }
            System.out.println("Citas cargadas: " + citas.size());
        } catch (Exception e) {
            e.printStackTrace();
            if (tablaCitas != null) {
                tablaCitas.getItems().clear();
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudieron cargar las citas");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // ================== MANEJADORES onAction (DEBEN COINCIDIR CON EL FXML) ==================

    /**
     * onAction="#nuevaCita" en el FXML
     */
    @FXML
    private void nuevaCita() {
        // Aquí deberías abrir la ventana para crear una nueva cita
        System.out.println("[HistorialCitas] nuevaCita() llamado");

        // Lógica futura: usar servicioCita / VentanaNuevaCita, etc.
        // Por ahora solo recargamos el historial si ya hay perfil
        if (perfilActual != null) {
            cargarPerfil(perfilActual);
        }
    }

    /**
     * onAction="#reagendarCita" en el FXML
     */
    @FXML
    private void reagendarCita() {
        System.out.println("[HistorialCitas] reagendarCita() llamado");

        if (tablaCitas == null) {
            return;
        }

        Cita seleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione una cita de la tabla para reagendar.");
            alert.showAndWait();
            return;
        }

        // Aquí va la lógica real de reagendar (ventana de reagendar, etc.)

        // Al finalizar reagendar, recargamos:
        if (perfilActual != null) {
            cargarPerfil(perfilActual);
        }
    }

    /**
     * onAction="#verDetallesCita" en el FXML
     */
    @FXML
    private void verDetallesCita() {
        System.out.println("[HistorialCitas] verDetallesCita() llamado");

        if (tablaCitas == null) {
            return;
        }

        Cita seleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione una cita para ver sus detalles.");
            alert.showAndWait();
            return;
        }

        // De momento mostramos los detalles en un Alert simple
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles de la cita");
        alert.setHeaderText("Cita de " + (perfilActual != null ? perfilActual.getNombreCompleto() : ""));
        StringBuilder sb = new StringBuilder();
        sb.append("Fecha: ").append(seleccionada.getFechaCita()).append("\n");
        sb.append("Hora: ").append(seleccionada.getHoraCita()).append("\n");
        sb.append("Estado: ").append(seleccionada.getEstadoCita()).append("\n");
        sb.append("Detalles: ").append(seleccionada.getDetallesAdicionalesPaciente());
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    /**
     * onAction="#cerrar" en el FXML
     */
    @FXML
    private void cerrar() {
        System.out.println("[HistorialCitas] cerrar() llamado");

        if (btnCerrar != null && btnCerrar.getScene() != null) {
            Stage stage = (Stage) btnCerrar.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }
}
