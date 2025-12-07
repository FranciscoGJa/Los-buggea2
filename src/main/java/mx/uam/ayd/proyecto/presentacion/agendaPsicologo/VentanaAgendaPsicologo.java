package mx.uam.ayd.proyecto.presentacion.agendaPsicologo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.ServicioCalendario;
import mx.uam.ayd.proyecto.presentacion.menuPsicologo.ControlMenuPsicologo;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;

@Component
public class VentanaAgendaPsicologo {

    @Autowired
    private ServicioCalendario servicioCalendario;

    @FXML
    private TableView<Cita> tablaAgenda;

    @FXML
    private TableColumn<Cita, String> colHora;

    @FXML
    private TableColumn<Cita, String> colPaciente;

    @FXML
    private TableColumn<Cita, String> colEstado;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label lblInfo;

    private Stage stage;

    private ControlMenuPsicologo controlMenuPsicologo;

    public void setControlMenuPsicologo(ControlMenuPsicologo control) {
        this.controlMenuPsicologo = control;
    }

    public void muestra() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/agendaPsicologo.fxml"));
            loader.setController(this);

            Parent root = loader.load();

            configurarTabla();

            // fecha por defecto: hoy
            if (datePicker != null) {
                datePicker.setValue(LocalDate.now());
            }

            Scene scene = new Scene(root);

            // ====== ESTILO GLOBAL PERO SIN ROMPER SI NO EXISTE ======
            try {
                URL css = getClass().getResource("/fxml/css/style.css");
                if (css == null) {
                    css = getClass().getResource("/css/style.css");
                }
                if (css != null) {
                    scene.getStylesheets().add(css.toExternalForm());
                } else {
                    System.out.println("[AgendaPsicologo] CSS global no encontrado, se continúa sin estilos extra.");
                }
            } catch (Exception e) {
                System.out.println("[AgendaPsicologo] Error al cargar CSS (ignorado): " + e);
            }
            // ========================================================

            stage = new Stage();
            stage.setTitle("Agenda del Psicólogo");
            stage.setScene(scene);
            stage.show();

            // Opcional: buscar automáticamente las citas del día
            if (datePicker != null) {
                buscarCitas(datePicker.getValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configurarTabla() {

        // Columna HORA
        colHora.setCellValueFactory(param ->
                new SimpleStringProperty(
                        param.getValue().getHoraCita() != null
                                ? param.getValue().getHoraCita().toString()
                                : ""
                ));

        // Columna PACIENTE: primero PerfilCitas, luego Paciente
        colPaciente.setCellValueFactory(param -> {
            Cita cita = param.getValue();
            String nombre = "Sin asignar";

            if (cita != null) {
                if (cita.getPerfilCitas() != null &&
                        cita.getPerfilCitas().getNombreCompleto() != null &&
                        !cita.getPerfilCitas().getNombreCompleto().isBlank()) {

                    nombre = cita.getPerfilCitas().getNombreCompleto();

                } else if (cita.getPaciente() != null &&
                        cita.getPaciente().getNombre() != null &&
                        !cita.getPaciente().getNombre().isBlank()) {

                    nombre = cita.getPaciente().getNombre();
                }
            }

            return new SimpleStringProperty(nombre);
        });

        // Columna ESTADO
        colEstado.setCellValueFactory(param ->
                new SimpleStringProperty(
                        param.getValue().getEstadoCita() != null
                                ? param.getValue().getEstadoCita().toString()
                                : ""
                ));
    }

    private void buscarCitas(LocalDate fecha) {

        if (fecha == null) {
            lblInfo.setText("Seleccione una fecha.");
            tablaAgenda.getItems().clear();
            return;
        }

        if (controlMenuPsicologo == null || controlMenuPsicologo.getPsicologo() == null) {
            lblInfo.setText("No se encontró el psicólogo actual.");
            tablaAgenda.getItems().clear();
            return;
        }

        int id = controlMenuPsicologo.getPsicologo().getId();
        System.out.println("[AgendaPsicologo] Buscando citas para psicologo " + id +
                " en fecha " + fecha);

        List<Cita> citas = servicioCalendario
                .obtenerCitasPorPsicologoYFecha(id, fecha);

        System.out.println("[AgendaPsicologo] Citas obtenidas: " + citas.size());

        tablaAgenda.getItems().setAll(citas);

        if (citas.isEmpty()) {
            lblInfo.setText("No hay citas para esta fecha");
        } else {
            lblInfo.setText("Citas encontradas: " + citas.size());
        }
    }

    @FXML
    private void buscarPorFecha() {
        buscarCitas(datePicker.getValue());
    }
}
