package mx.uam.ayd.proyecto.presentacion.calendarioPsicologo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.TipoConfirmacionCita;

import org.springframework.stereotype.Component;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

@Component
public class VentanaCalendarioPsicologo {

    private Stage stage;
    private ControlCalendarioPsicologo control;

    @FXML private TableView<Cita> tablaAgenda;
    @FXML private TableColumn<Cita, String> colFecha;
    @FXML private TableColumn<Cita, String> colHora;
    @FXML private TableColumn<Cita, String> colPaciente;
    @FXML private TableColumn<Cita, String> colMotivo;
    @FXML private TableColumn<Cita, String> colEstado;

    private final ObservableList<Cita> listaCitas = FXCollections.observableArrayList();

    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");

    @FXML
    public void initialize() {

        // FECHA convertida a String
        colFecha.setCellValueFactory(param -> {
            Date fecha = param.getValue().getFechaCita();
            return new SimpleStringProperty(fecha != null ? formatoFecha.format(fecha) : "—");
        });

        // HORA convertida a String
        colHora.setCellValueFactory(param -> {
            Date hora = param.getValue().getHoraCita();
            return new SimpleStringProperty(hora != null ? formatoHora.format(hora) : "—");
        });

        // PACIENTE convertida a nombre
        colPaciente.setCellValueFactory(param -> {
            if (param.getValue().getPaciente() != null) {
                return new SimpleStringProperty(param.getValue().getPaciente().getNombre());
            }
            return new SimpleStringProperty("Sin asignar");
        });

        // MOTIVO
        colMotivo.setCellValueFactory(param ->
            new SimpleStringProperty(param.getValue().getMotivoCancelacion())
        );

        // ESTADO (Enum)
        colEstado.setCellValueFactory(param ->
            new SimpleStringProperty(param.getValue().getEstadoCita().toString())
        );

        // -------------------------------------------------------------
        //                Citas de ejemplo (FUNCIONAN CON java.util.Date)
        // -------------------------------------------------------------

        Cita c1 = new Cita();
        c1.setFechaCita(new Date());
        c1.setHoraCita(new Date());
        c1.setMotivoCancelacion("Evaluación inicial");
        c1.setEstadoCita(TipoConfirmacionCita.CONFIRMADA);

        Cita c2 = new Cita();
        c2.setFechaCita(new Date());
        c2.setHoraCita(new Date());
        c2.setMotivoCancelacion("Seguimiento");
        c2.setEstadoCita(TipoConfirmacionCita.PENDIENTE);

        listaCitas.addAll(c1, c2);
        tablaAgenda.setItems(listaCitas);
    }

    @FXML
    private void handleNuevaCita() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva Cita");
        dialog.setHeaderText("Agendar nueva cita");
        dialog.setContentText("Ingrese el motivo:");

        dialog.showAndWait().ifPresent(motivo -> {
            Cita nueva = new Cita();
            nueva.setFechaCita(new Date());
            nueva.setHoraCita(new Date());
            nueva.setMotivoCancelacion(motivo);
            nueva.setEstadoCita(TipoConfirmacionCita.PENDIENTE);

            listaCitas.add(nueva);
            tablaAgenda.refresh();

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Cita agregada");
            alerta.setContentText("✅ Nueva cita agregada correctamente.");
            alerta.showAndWait();
        });
    }

    @FXML
    private void handleActualizar() {
        tablaAgenda.refresh();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actualizar");
        alert.setContentText("🔄 Agenda actualizada.");
        alert.showAndWait();
    }

    @FXML
    private void handleCerrar() {
        if (stage != null)
            stage.close();
    }

    public void muestra(ControlCalendarioPsicologo control) {
        this.control = control;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaCalendarioPsicologo.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            Scene scene = new Scene(root, 950, 700);
            stage = new Stage();
            stage.setTitle("Calendario del Psicólogo 🧠");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cerrarVentana() {
        if (stage != null)
            stage.close();
    }

    public void setControl(ControlCalendarioPsicologo control) {
        this.control = control;
    }
}
