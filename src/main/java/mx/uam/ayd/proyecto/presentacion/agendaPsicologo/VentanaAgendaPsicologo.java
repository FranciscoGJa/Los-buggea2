package mx.uam.ayd.proyecto.presentacion.agendaPsicologo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.ServicioCalendario;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;

@Component
public class VentanaAgendaPsicologo {

    private Stage stage;

    @Autowired
    private ServicioCalendario servicioCalendario;

    @FXML
    private TableView<Cita> tablaAgenda;

    @FXML
    private TableColumn<Cita, String> colFecha;

    @FXML
    private TableColumn<Cita, String> colHora;

    @FXML
    private TableColumn<Cita, String> colPaciente;

    @FXML
    private TableColumn<Cita, String> colEstado;

    public void muestra() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/agendaPsicologo.fxml"));

            loader.setController(this);
            Parent root = loader.load();

            configurarTabla();
            cargarCitasDeHoy();

            stage = new Stage();
            stage.setTitle("Agenda del Psicólogo");
            stage.setScene(new Scene(root, 900, 600));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configurarTabla() {

        colFecha.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getFechaCita().format(DateTimeFormatter.ISO_DATE)));

        colHora.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getHoraCita().toString()));

        colPaciente.setCellValueFactory(param ->
                new SimpleStringProperty(
                        param.getValue().getPaciente() != null
                                ? param.getValue().getPaciente().getNombre()
                                : "Sin asignar"));

        colEstado.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getEstadoCita().toString()));
    }

/**
 * Carga citas reales del psicólogo (modo Sprint)
 * ✅ Solo obtiene citas del día
 */
public void cargarCitasDeHoy() {

    List<Cita> citas = servicioCalendario.obtenerCitasPorPsicologoYFecha(
            1, // ID fijo del psicólogo para la entrega del sprint
            java.time.LocalDate.now()
    );

    tablaAgenda.getItems().setAll(citas);
}

}
