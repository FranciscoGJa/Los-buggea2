package mx.uam.ayd.proyecto.presentacion.agregarBDI;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.BateriaClinica;

@Component
public class VentanaAgregarBDI {

    private Stage stage;
    private boolean initialized = false; 
    private ControlAgregarBDI controlAgregarBDI;
    private Long pacienteID;

    public void setControlAgregarBDI(ControlAgregarBDI controlAgregarBDI) {
        this.controlAgregarBDI = controlAgregarBDI;
    }
    
    private void initializeUI() {
        if (initialized) return;
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }
        try {
            stage = new Stage();
            stage.setTitle("Inventario de Depresion de Beck (BDI-II)");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-BDI.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo cargar la ventana: " + e.getMessage()).showAndWait();
        }
    }

    public VentanaAgregarBDI() { }

    public void muestra() {
        if (!initialized) initializeUI();
        limpiarSeleccion();
        stage.show();
    }

    public void muestra(BateriaClinica bateria) {
        if (!initialized) initializeUI();
        cargarRespuestas(bateria);
        stage.show();
    }

    private void cargarRespuestas(BateriaClinica bateria) {
        List<ToggleGroup> grupos = Arrays.asList(q1, q2, q3, q4, q5);
        List<Integer> valores = Arrays.asList(
            bateria.getRespuesta1(), bateria.getRespuesta2(), bateria.getRespuesta3(), 
            bateria.getRespuesta4(), bateria.getRespuesta5()
        );

        for (int i = 0; i < grupos.size(); i++) {
            Integer valor = valores.get(i);
            ToggleGroup grupo = grupos.get(i);
            if (valor != null && grupo != null) {
                for (Toggle t : grupo.getToggles()) {
                    if (t.getUserData() != null && t.getUserData().toString().equals(String.valueOf(valor))) {
                        grupo.selectToggle(t);
                        break;
                    }
                }
            }
        }
    }

    private void limpiarSeleccion() {
        if(q1!=null) q1.selectToggle(null);
        if(q2!=null) q2.selectToggle(null);
        if(q3!=null) q3.selectToggle(null);
        if(q4!=null) q4.selectToggle(null);
        if(q5!=null) q5.selectToggle(null);
    }

    public void setVisible(boolean visible) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.setVisible(visible));
            return;
        }
        if (!initialized) { if (visible) initializeUI(); else return; }
        if (visible) stage.show(); else stage.hide();
    }

    public void muestraDialogoConMensaje(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDialogoConMensaje(mensaje));
            return;
        }
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setPacienteID(Long pacienteID) {
        this.pacienteID=pacienteID;
    }

    @FXML private ToggleGroup q1;
    @FXML private ToggleGroup q2;
    @FXML private ToggleGroup q3;
    @FXML private ToggleGroup q4;
    @FXML private ToggleGroup q5;

    @FXML    
    private void onGuard() {
        try {
            List<Integer> respuestas = Arrays.asList(
                getSelectedValue(q1), getSelectedValue(q2), getSelectedValue(q3), 
                getSelectedValue(q4), getSelectedValue(q5)
            );

            if (respuestas.stream().anyMatch(r -> r == null)) {
                muestraDialogoConMensaje("Responde todas las preguntas antes de guardar.");
                return;
            }

            String comentarios = " ";
            controlAgregarBDI.guardarBDI(pacienteID, respuestas, comentarios);

            muestraDialogoConMensaje("¡Batería BDI guardada/actualizada!");
            stage.close();

        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error al guardar: " + ex.getMessage()).showAndWait();
        }
    }

    private Integer getSelectedValue(ToggleGroup group) {
        if (group != null && group.getSelectedToggle() != null &&
            group.getSelectedToggle().getUserData() != null) {
            return Integer.parseInt(group.getSelectedToggle().getUserData().toString());
        }
        return 0;
    }
}