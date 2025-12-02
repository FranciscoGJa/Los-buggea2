package mx.uam.ayd.proyecto.presentacion.Pago;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.presentacion.menu.ControlMenu;

import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class VentanaPagoEfectivo {

    private Stage stage;
    private ControlPagoEfectivo control;
    private boolean initialized = false;
    

    private final double MONTO_A_PAGAR = 1350.0;

    @FXML private Label lblMonto;
    @FXML private TextField txtPago;
    @FXML private Label lblCambio;
    @FXML private Label lblMensaje;
    @FXML private Button btnAbrirCaja;
    @FXML private Button btnCerrarCaja;
    @FXML private Button btnConfirmar;

    private boolean cajaAbierta = false;
    private boolean cajaCerrada = false;
    

    @FXML
    private void initialize() {
        lblMonto.setText(String.format("$%.2f", MONTO_A_PAGAR));
        lblCambio.setText("");
        lblMensaje.setText("");
        btnCerrarCaja.setDisable(true);
        btnConfirmar.setDisable(true);
        txtEfectivo();
    }
    //Creamos el método para calcular el cambio
    @FXML
    private void calcularCambio() {
        lblMensaje.setText("");
        lblCambio.setText("");

        try {
            //Intentamos convertir el texto ingresado a un número
            double pago = Double.parseDouble(txtPago.getText());
            //Validamos si el pago es suficiente
            if (pago < MONTO_A_PAGAR) {
                //lblMensaje.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); 
                lblMensaje.setText(" El monto es insuficiente. Falta " + String.format("$%.2f", MONTO_A_PAGAR - pago));
                btnAbrirCaja.setDisable(true);
                lblCambio.setText("");
                //si el pago es suficiente
            } else {
                double cambio = pago - MONTO_A_PAGAR;
                lblCambio.setText(String.format("Cambio: $%.2f", cambio));
                lblMensaje.setText(" Pago válido. Puede abrir la caja.");
                btnAbrirCaja.setDisable(false);
            }
            //Manejamos la excepción en caso de que el texto no sea un número válido
            //Este deja de ser funcional debido a que el TextField ya valida solo números
        } catch (NumberFormatException e) {
            lblMensaje.setText(" Ingresa un número válido.");
            lblCambio.setText("");
            btnAbrirCaja.setDisable(true);
        }
    }
    //Método para validar el TextField solo aceopte números y máximo 16 caracteres
    private void txtEfectivo() {
        // --- Validar que solo acepte números y máximo 16 caracteres ---
        txtPago.textProperty().addListener((observable, oldValue, newValue) -> {
        // Eliminar todo lo que no sean dígitos
        if (!newValue.matches("\\d*")) {
            txtPago.setText(newValue.replaceAll("[^\\d]", ""));
        }

        // Limitar a 16 dígitos
        if (txtPago.getText().length() > 16) {
            txtPago.setText(txtPago.getText().substring(0, 16));
        }
    });
    }

    //Métodos para manejar la apertura y cierre de la caja, así como la confirmación del pago
    @FXML
    private void abrirCaja() {
        if (!cajaAbierta) {
            cajaAbierta = true;
            lblMensaje.setText(" Caja abierta. Coloca el dinero y ciérrala para continuar.");
            btnAbrirCaja.setDisable(true);
            btnCerrarCaja.setDisable(false);
        }
    }

    //cierre de caja
    @FXML
    private void cerrarCaja() {
        if (cajaAbierta && !cajaCerrada) {
            cajaCerrada = true;
            lblMensaje.setText(" Caja cerrada. Puedes confirmar el pago.");
            btnCerrarCaja.setDisable(true);
            btnConfirmar.setDisable(false);
        }
    }

    //  confirmación de pago
    @FXML
    private void confirmarPago() {
        lblMensaje.setText(" Pago completado con éxito.");
        btnConfirmar.setDisable(true);
    }

    
    //Método para inicializar la interfaz gráfica de usuario de java con el FXML
    private void initializeUI() {
        if (initialized) return;

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Pago en efectivo");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaPagoEfectivo.fxml"));
            loader.setController(this); 
            Parent root = loader.load();

            Scene scene = new Scene(root, 640, 400);

            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            
            stage.setScene(scene);
            
            initialized = true;
            } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error cargando FXML de ventanaPagoEfectivo.fxml");
        }
        
    }

    
    /**
     * Establece la referencia al controlador de esta ventana.
     * * @param control instancia de {@link ControlPagoEfectivo}
     */
    public void setControlPagoEfectivo(ControlPagoEfectivo control) {
        this.control = control;
    }

    //  Método para mostrar la ventana
    public void mostrar() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::mostrar);
            return;
        }
        initializeUI();
        if (stage != null) {
            stage.show();
        } else {
            System.err.println("No se pudo mostrar la ventana: stage es null.");
        }
    }

    /**
     * Devuelve la vista (`Parent`) para insertar en el `contentArea` de la ventana principal.
     * No crea un Stage.
     */
    public Parent getVista() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaPagoEfectivo.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        return root;
    }
}