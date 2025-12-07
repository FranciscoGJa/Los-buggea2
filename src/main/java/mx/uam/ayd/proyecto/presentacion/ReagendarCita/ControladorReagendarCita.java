package mx.uam.ayd.proyecto.presentacion.ReagendarCita;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.ServicioCalendario;
import mx.uam.ayd.proyecto.negocio.ServicioCita;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class ControladorReagendarCita {

    // ====== Labels (parte superior) ======
    @FXML
    private Label lblPaciente;

    @FXML
    private Label lblPsicologo;

    @FXML
    private Label lblCitaOriginal;

    // ====== Nueva fecha y hora ======
    @FXML
    private DatePicker datePickerNuevaFecha;

    @FXML
    private ComboBox<LocalTime> comboHorarios;

    @FXML
    private Button btnVerificarDisponibilidad;

    // ====== Motivo ======
    @FXML
    private TextArea txtMotivo;

    // ====== Botones inferiores ======
    @FXML
    private Button btnConfirmarReagendo;

    @FXML
    private Button btnCancelar;

    // ====== Servicios ======
    @Autowired
    private ServicioCalendario servicioCalendario;

    @Autowired
    private ServicioCita servicioCita;

    // ====== Datos de la cita ======
    private PerfilCitas perfil;
    private Cita citaOriginal;

    @FXML
    private void initialize() {
        System.out.println("[ControladorReagendarCita] initialize()");

        if (comboHorarios != null) {
            comboHorarios.setDisable(true);
        }
        if (btnConfirmarReagendo != null) {
            btnConfirmarReagendo.setDisable(true);
        }
    }

    /**
     * Método llamado desde VentanaReagendarCita.mostrar(...)
     */
    public void inicializarDatos(PerfilCitas perfil, Cita cita) {
        this.perfil = perfil;
        this.citaOriginal = cita;

        try {
            cargarInformacionCita();
        } catch (Exception e) {
            System.out.println("[ControladorReagendarCita] Error al cargar información de la cita:");
            e.printStackTrace();
            mostrarError("No se pudo cargar la información de la cita.\n" + e.getMessage());
        }
    }

    private void cargarInformacionCita() {

        if (citaOriginal == null) {
            mostrarError("No se recibió la cita a reagendar.");
            return;
        }

        // ====== Paciente ======
        String nombrePaciente = "Sin asignar";
        try {
            if (perfil != null && perfil.getNombreCompleto() != null) {
                nombrePaciente = perfil.getNombreCompleto();
            }
        } catch (Exception e) {
            System.out.println("[ControladorReagendarCita] Error obteniendo nombre del paciente: " + e);
        }
        lblPaciente.setText(nombrePaciente);

        // ====== Psicólogo ======
        String nombrePsico = "Desconocido";
        try {
            if (citaOriginal.getPsicologo() != null &&
                    citaOriginal.getPsicologo().getNombre() != null) {
                nombrePsico = citaOriginal.getPsicologo().getNombre();
            }
        } catch (Exception ex) {
            System.out.println("[ControladorReagendarCita] Error obteniendo nombre del psicólogo: " + ex);
        }
        lblPsicologo.setText(nombrePsico);

        // ====== Cita original (fecha + hora) ======
        String citaOrig = "";
        if (citaOriginal.getFechaCita() != null) {
            citaOrig += citaOriginal.getFechaCita();
        }
        if (citaOriginal.getHoraCita() != null) {
            citaOrig += " " + citaOriginal.getHoraCita();
        }
        lblCitaOriginal.setText(citaOrig);

        // ====== Fecha y motivo por defecto ======
        if (datePickerNuevaFecha != null) {
            datePickerNuevaFecha.setValue(citaOriginal.getFechaCita());
        }
        if (txtMotivo != null && citaOriginal.getDetallesAdicionalesPaciente() != null) {
            txtMotivo.setText(citaOriginal.getDetallesAdicionalesPaciente());
        }
    }

    // ================= BOTÓN "Verificar Disponibilidad" =================
    @FXML
    private void verificarDisponibilidad() {

        if (citaOriginal == null) {
            mostrarError("No se ha cargado ninguna cita.");
            return;
        }

        LocalDate nuevaFecha = datePickerNuevaFecha.getValue();
        if (nuevaFecha == null) {
            mostrarInfo("Seleccione una nueva fecha para reagendar.");
            return;
        }

        Integer idPsicologo = null;
        try {
            if (citaOriginal.getPsicologo() != null) {
                idPsicologo = citaOriginal.getPsicologo().getId();
            }
        } catch (Exception ex) {
            System.out.println("[ControladorReagendarCita] Error obteniendo ID del psicólogo: " + ex);
        }

        if (idPsicologo == null) {
            mostrarError("No se pudo determinar el psicólogo de la cita.");
            return;
        }

        System.out.println("[ControladorReagendarCita] Verificando horarios para psicologo "
                + idPsicologo + " en fecha " + nuevaFecha);

        List<LocalTime> disponibles =
                servicioCalendario.obtenerHorariosDisponibles(idPsicologo, nuevaFecha);

        comboHorarios.getItems().setAll(disponibles);
        comboHorarios.setDisable(disponibles.isEmpty());

        if (disponibles.isEmpty()) {
            mostrarInfo("No hay horarios disponibles para esa fecha.");
            btnConfirmarReagendo.setDisable(true);
        } else {
            btnConfirmarReagendo.setDisable(false);
        }
    }

    // ================= BOTÓN "Confirmar Reagendo" =================
    @FXML
    private void confirmarReagendo() {

        if (citaOriginal == null) {
            mostrarError("No se ha cargado ninguna cita.");
            return;
        }

        LocalDate nuevaFecha = datePickerNuevaFecha.getValue();
        LocalTime nuevoHorario = comboHorarios.getValue();

        if (nuevaFecha == null || nuevoHorario == null) {
            mostrarInfo("Seleccione fecha y hora para el reagendo.");
            return;
        }

        String nuevoMotivo = txtMotivo.getText();

        try {
            // 👉 Método que agregaremos en ServicioCita
            servicioCita.reagendarCita(
                    citaOriginal.getIdCita(),
                    nuevaFecha,
                    nuevoHorario,
                    nuevoMotivo
            );

            mostrarInfo("La cita se reagendó correctamente.");
            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo reagendar la cita:\n" + e.getMessage());
        }
    }

    // ================= BOTÓN "Cancelar" =================
    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        if (btnCancelar != null && btnCancelar.getScene() != null) {
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }

    // ================= Helpers de alertas =================
    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error inesperado");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
