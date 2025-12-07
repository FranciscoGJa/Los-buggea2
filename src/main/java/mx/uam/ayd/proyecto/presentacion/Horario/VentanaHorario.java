package mx.uam.ayd.proyecto.presentacion.Horario;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.stereotype.Component;
import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.ServicioCita;
import mx.uam.ayd.proyecto.presentacion.Horario.ControlHorario;
import mx.uam.ayd.proyecto.presentacion.menu.ControlMenu;
import mx.uam.ayd.proyecto.presentacion.menuPsicologo.ControlMenuPsicologo;

@Component
public class VentanaHorario {

    private Stage stage;
    private ControlHorario control;
    private boolean initialized = false;
    private List<Cita> citasDelDia;
    private ControlMenuPsicologo controlMenu;
    @FXML private VBox contenedorHoras;

    @FXML
    public void initialize() {
        // Cargar horas de 9 AM a 6 PM
        for (int hora = 9; hora <= 18; hora++) {
            contenedorHoras.getChildren().add(crearFilaHora(hora));
        }
    }

    public void setControlMenu(ControlMenuPsicologo controlMenu) {
        this.controlMenu = controlMenu;
    }


    private HBox crearFilaHora(int h) {
    HBox fila = new HBox();
    fila.setStyle("-fx-border-color: #333; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f8f8f8;");
    
    fila.setPrefHeight(60);      // altura visual correcta
    fila.setMinHeight(60);
    fila.setMaxHeight(60);

    Label lbl = new Label(formatearHora(h));
    lbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    lbl.setMaxHeight(120);

    fila.getChildren().add(lbl);
    HBox.setHgrow(lbl, javafx.scene.layout.Priority.ALWAYS);
    String paciente = buscarNombrePacientePorHora(h);

    if (paciente != null){
        Label lblPAciente = new Label("Paciente: " + paciente);
        lblPAciente.setStyle("-fx-font-size: 17px; -fx-text-fill: #0055aa; -fx-padding: 10;");
        fila.getChildren().add(lblPAciente);
    }

    // Agregar texto especial a las 2 PM (14 hrs)
    if (h == 14) {
        Label comida = new Label(" ← Hora de comida");
        comida.setStyle("-fx-font-size: 16px; -fx-text-fill: green;");
        fila.getChildren().add(comida);
    }

    return fila;
    }

    private String buscarNombrePacientePorHora(int hora){
        if (citasDelDia == null) { return null;}

        for(Cita c : citasDelDia){
            if(c.getHoraCita() != null && c.getHoraCita().getHour() == hora){
                if(c.getPerfilCitas() != null){
                    return c.getPerfilCitas().getNombreCompleto();
                }
            }
        }
        return null;
    }

    private String formatearHora(int h) {
        if (h == 12) return "12:00 PM";
        if (h > 12) return (h - 12) + ":00 PM";
        return h + ":00 AM";
    }

    private void initializeUI() {
        if (initialized) return;

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            //creamos la ventana 
            stage = new Stage();
            //mostramos que la ventana se llama pago en efectivo
            stage.setTitle("Horario");

            //llamamos al fxml donde hicimos el diseño de la ventana de pago en efectivo
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaHorario.fxml"));
            loader.setController(this);
            Parent root = loader.load();

             // Obtener el controlador REAL del FXML
            VentanaHorario controller = loader.getController();

            // Aquí se asigna el controlMenu (tu objeto real)
            controller.setControlMenu(this.controlMenu);  

            //determinamos el tamaño de la ventana 
            Scene scene = new Scene(root, 1040, 800);

            //ponemos el diseño que ya se tiene por definido en el proyecto
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            
            //inicializamos la pestaña para que muestre la ventana del fxml
            initialized = true;
            } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error cargando FXML de ventanaHorario.fxml");
        }
        
    }

    public void setControlHorario(ControlHorario control) {
        this.control = control;
    }


    public void mostrarHorario() {

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::mostrarHorario);
            return;
        }
        initializeUI();
        //carga datos fuera del hilo de la UI
        Task<Void> task = new Task<Void>() {
        @Override
        protected Void call() throws Exception {

            // Actualizar UI
            Platform.runLater(() -> actualizarHorario());

            return null;
            }
        };
    new Thread(task).start();
    stage.show();
        
    }

    private void actualizarHorario() {
        contenedorHoras.getChildren().clear();

        for (int h = 9; h <= 18; h++) {
            contenedorHoras.getChildren().add(crearFilaHora(h));
        }
    }
    public void cargaCitasDelDia(List<Cita> citas){
        this.citasDelDia = citas;
    }

    public void cargarCitasAsync() {
    Task<List<Cita>> task = new Task<List<Cita>>() {
        @Override
        protected List<Cita> call() throws Exception {

            // ID del psicólogo
            int idPsicologo = controlMenu.getPsicologo().getId();
            System.out.println("[AgendaPsicologo] Buscando citas para psicologo " + idPsicologo +
                " en fecha " + LocalDate.now());
            return control.getServicioCita()
                    .obtenerCitasPorPsicologoYFecha(idPsicologo, LocalDate.now());   
        }
    };  

    if (controlMenu == null || controlMenu.getPsicologo() == null) {
        System.err.println("[VentanaHorario] controlMenu NO inicializado o psicólogo nulo.");
        return;
    }

    task.setOnSucceeded(e -> {
        List<Cita> citas = task.getValue();
        cargaCitasDelDia(citas);
        actualizarHorario();   // Refrescar UI
    });

    task.setOnFailed(e -> {
        System.err.println("ERROR al cargar citas: " + task.getException());
    });

    new Thread(task).start(); // ← CORRE EN SEGUNDO PLANO
}

    @FXML
    private void cerrarVentana() {
        if (stage != null) {
            stage.close();
        } else {
        System.err.println("Stage es null, no se puede cerrar la ventana");
        }
    }
}