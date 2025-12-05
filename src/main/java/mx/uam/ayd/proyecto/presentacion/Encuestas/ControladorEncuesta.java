package mx.uam.ayd.proyecto.presentacion.Encuestas;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ControladorEncuesta {

    @FXML private TextField txtPaciente;
    @FXML private TextField txtPsicologo;
    @FXML private TextField txtFecha;
    @FXML private ComboBox<String> cbPuntuacion;
    @FXML private TextArea txtComentarios;

    @FXML private Label lblExito;

    private List<Encuesta> encuestas = new ArrayList<>();

    @FXML
    private void enviarEncuesta() {

        if (!validarCampos()) {
            return;
        }

        Encuesta encuesta = new Encuesta(
                txtPaciente.getText(),
                txtPsicologo.getText(),
                txtFecha.getText(),
                cbPuntuacion.getValue(),
                txtComentarios.getText()
        );

       encuestas.add(encuesta);

        mostrarMensajeYCerrar();
        limpiarCampos();
    }

    private boolean validarCampos() {
        if (txtPaciente.getText().isEmpty() ||
            txtPsicologo.getText().isEmpty() ||
            txtFecha.getText().isEmpty() ||
            cbPuntuacion.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Faltan campos");
            alert.setContentText("Por favor llena todos los campos obligatorios.");
            alert.show();
            return false;
        }
        return true;
    }

    private void limpiarCampos() {
        txtPaciente.clear();
        txtPsicologo.clear();
        txtFecha.clear();
        cbPuntuacion.getSelectionModel().clearSelection();
        txtComentarios.clear();
    }

    private void mostrarMensaje() {
        lblExito.setVisible(true);

        FadeTransition ft = new FadeTransition(Duration.seconds(3), lblExito);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> {
            lblExito.setVisible(false);
            lblExito.setOpacity(1.0);
        });
        ft.play();
    }

    private void mostrarMensajeYCerrar() {
        lblExito.setVisible(true);

        FadeTransition ft = new FadeTransition(Duration.seconds(3), lblExito);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> {
            lblExito.setVisible(false);
            lblExito.setOpacity(1.0);
            // Cerrar ventana después de que el mensaje desaparezca
            Stage stage = (Stage) lblExito.getScene().getWindow();
            stage.close();
        });
        ft.play();
    }

    // Clase interna que simula el objeto "encuesta"
    public static class Encuesta {
        String paciente, psicologo, fecha, puntuacion, comentarios;

        public Encuesta(String paciente, String psicologo, String fecha, String puntuacion, String comentarios) {
            this.paciente = paciente;
            this.psicologo = psicologo;
            this.fecha = fecha;
            this.puntuacion = puntuacion;
            this.comentarios = comentarios;
        }
    }
}