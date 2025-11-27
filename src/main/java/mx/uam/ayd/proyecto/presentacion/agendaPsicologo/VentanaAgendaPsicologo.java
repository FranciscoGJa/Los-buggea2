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
import mx.uam.ayd.proyecto.presentacion.principal.ControlPrincipalCentro;


import java.io.IOException;
import java.time.LocalDate;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;

@Component
public class VentanaAgendaPsicologo {

    @Autowired
    private ServicioCalendario servicioCalendario;

    @FXML
    private TableView<Cita> tablaAgenda;

    @FXML
    private TableColumn<Cita,String> colHora;

    @FXML
    private TableColumn<Cita,String> colPaciente;

    @FXML
    private TableColumn<Cita,String> colEstado;

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

        Scene scene = new Scene(root);

        //estilo
        scene.getStylesheets().add(
            getClass().getResource("/css/style.css").toExternalForm()
        );

        stage = new Stage();
        stage.setTitle("Agenda del Psicólogo");
        stage.setScene(scene);
        stage.show();

        } catch (IOException e) {
        e.printStackTrace();
        }
   }




    private void configurarTabla() {

        colHora.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getHoraCita().toString()));

        colPaciente.setCellValueFactory(param ->
                new SimpleStringProperty(
                        param.getValue().getPaciente() == null ?
                                "Sin asignar" :
                                param.getValue().getPaciente().getNombre()
                ));

        colEstado.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getEstadoCita().toString()));
    }

    private void buscarCitas(LocalDate fecha) {

        int id = controlMenuPsicologo.getPsicologo().getId();


        List<Cita> citas = servicioCalendario
                .obtenerCitasPorPsicologoYFecha(id, fecha);

        tablaAgenda.getItems().setAll(citas);

        if(citas.isEmpty()){
            lblInfo.setText("No hay citas para esta fecha");
        } else {
            lblInfo.setText("Citas encontradas: " + citas.size());
        }
    }

    @FXML
    private void buscarPorFecha(){
        buscarCitas(datePicker.getValue());
    }
}
