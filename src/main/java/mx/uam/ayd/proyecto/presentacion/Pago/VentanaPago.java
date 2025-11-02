package mx.uam.ayd.proyecto.presentacion.Pago;

import org.springframework.stereotype.Component;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.function.UnaryOperator;

import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import java.util.function.UnaryOperator;
import javafx.scene.control.TextFormatter.Change;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.Year;
import java.util.stream.IntStream;


@Component
public class VentanaPago {

    private Stage stage;
    private ControlPagar control;
    private boolean initialized = false;
    

    @FXML private TextField txtCardNumber;
    @FXML private TextField txtCardHolder;// Nombre del titular
    @FXML private TextField txtExpiryDate;
    @FXML private PasswordField txtCVV;
    @FXML private TextField CVVvisible;
    @FXML private Button btnToggleCVV;
    @FXML private TextField txtMonto;
    @FXML private ChoiceBox<String> cbMetodo;
    @FXML private Button btnPagar;
    @FXML private Label lblMensaje;
    private boolean mostrando = false;
    @FXML private Label lblInfo;
    @FXML private ComboBox<String> cbMes;
    @FXML private ComboBox<Integer> cbAno;

    @FXML
    private void onPago() {
        lblMensaje.setText("Simulación de pago completada.");
    }

    private final int ANIOS_ADELANTE = 10;

    @FXML
    private void initialize() {
        // --- Configurar ComboBox de fecha ---
        configurarComboBoxesFecha();
        // --- Configurar TextField de número de tarjeta ---
        txtCardNumber();
        // --- Configurar TextField de CVV ---
        limitarCvv();
        limitarCvvVisible();
        // --- Configurar TextField de nombre del titular ---
        limitarNombreTitular();
        // Sincroniza ambos campos para que siempre tengan el mismo texto
        //configuramos la visibilidad del cvv
        if (CVVvisible != null && txtCVV != null) {
            CVVvisible.textProperty().bindBidirectional(txtCVV.textProperty());
        }
        System.out.println("VentanaPago.initialize() llamado (el controlador está en la instancia de Spring).");

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newtext = change.getControlNewText();
            if (newtext.matches("\\d{0,16}")) {
                return change;
            }else{
                lblInfo.setText("Solo se permiten números y un máximo de 16 dígitos.");
                return null;//rechaza el cambio
            }
        };
    }
    private void configurarComboBoxesFecha() {
         if (cbMes == null || cbAno == null) {
            System.err.println("⚠️ cbMes o cbAno no están conectados al FXML.");
            return;
        }
        // Configurar ComboBox de Mes
        cbMes.getItems().clear();
        for (int i = 1; i <= 12; i++) {
            String mm = String.format("%02d", i);
            cbMes.getItems().add(mm);
        }

        // Llenar años (año actual .. actual + N)
        int currentYear = Year.now().getValue();
        cbAno.getItems().clear();
        IntStream.rangeClosed(currentYear, currentYear + ANIOS_ADELANTE).forEach(cbAno.getItems()::add);

        // Seleccionar por defecto el mes y año actuales (o siguiente mes si ya pasó)
        LocalDate today = LocalDate.now();
        cbMes.getSelectionModel().select(String.format("%02d", today.getMonthValue()));
        cbAno.getSelectionModel().select(Integer.valueOf(today.getYear()));
    }

    /** Valida que la fecha seleccionada no sea anterior al mes actual */
    @FXML
    private void onValidar() {
        if (!haySeleccion()) {
            lblMensaje.setText("Selecciona mes y año.");
            return;
        }

        int mes = Integer.parseInt(cbMes.getSelectionModel().getSelectedItem());
        int anio = cbAno.getSelectionModel().getSelectedItem();

        // construimos la fecha (día 1 del mes)
        LocalDate expiry = LocalDate.of(anio, mes, 1);
        LocalDate firstOfThisMonth = LocalDate.now().withDayOfMonth(1);

        if (expiry.isBefore(firstOfThisMonth)) {
            lblMensaje.setText("La tarjeta está vencida. Elige una fecha igual o posterior al mes actual.");
        } else {
            lblMensaje.setText("Fecha válida: " + String.format("%02d/%04d", mes, anio));
        }
    }

    /** Devuelve la fecha en formato MM/YY y la muestra en lblMensaje */
    @FXML
    private void onObtener() {
        if (!haySeleccion()) {
            lblMensaje.setText("Selecciona mes y año antes de obtener la fecha.");
            return;
        }
        String mm = cbMes.getSelectionModel().getSelectedItem();
        int anio = cbAno.getSelectionModel().getSelectedItem();
        String yy = String.format("%02d", anio % 100); // dos dígitos del año

        String mm_yy = mm + "/" + yy;
        lblMensaje.setText("Expiración (MM/YY): " + mm_yy);
    }

    private boolean haySeleccion() {
        return cbMes.getSelectionModel().getSelectedItem() != null &&
               cbAno.getSelectionModel().getSelectedItem() != null;
    }

    /** Método de ayuda: obtener la cadena MM/YY sin mostrarla */
    public String obtenerMMYY() {
        if (!haySeleccion()) return null;
        String mm = cbMes.getSelectionModel().getSelectedItem();
        int anio = cbAno.getSelectionModel().getSelectedItem();
        return mm + "/" + String.format("%02d", anio % 100);
    }
    
    //hacemos visible o invisible el cvv
    @FXML
    private void toggleCVVVisibility() {
        mostrando = !mostrando;
            CVVvisible.setVisible(mostrando);
            CVVvisible.setManaged(mostrando);
            txtCVV.setVisible(!mostrando);
            txtCVV.setManaged(!mostrando);
        if(btnToggleCVV != null) {
            // No puede procesar emoji por eso usamos prefijo
            btnToggleCVV.setText(mostrando ? "🙈" : "👁");
        }
        // 👇 Forzamos un relayout y refresco visual para mantener selección de los ComboBox
        if (cbMes != null) {
            cbMes.requestLayout();
            cbMes.setValue(cbMes.getValue()); // reestablece selección visible
        }
        if (cbAno != null) {
            cbAno.requestLayout();
            cbAno.setValue(cbAno.getValue()); // idem año
        }   
    }
    //limitar a 16 digitos el numero de tarjeta
    private void txtCardNumber() {
        // --- Validar que solo acepte números y máximo 16 caracteres ---
        txtCardNumber.textProperty().addListener((observable, oldValue, newValue) -> {
        // Eliminar todo lo que no sean dígitos
        if (!newValue.matches("\\d*")) {
            txtCardNumber.setText(newValue.replaceAll("[^\\d]", ""));
        }

        // Limitar a 16 dígitos
        if (txtCardNumber.getText().length() > 16) {
            txtCardNumber.setText(txtCardNumber.getText().substring(0, 16));
        }
    });
    }


    //limitar a 3 digitos el cvv cuando el cvv no es visible
    private void limitarCvv() {
        // Limitar a 3 dígitos
        txtCVV.textProperty().addListener((observable, oldValue, newValue) -> {
        // Eliminar todo lo que no sean dígitos
        if (!newValue.matches("\\d*")) {
            txtCVV.setText(newValue.replaceAll("[^\\d]", ""));
        }

        // Limitar a 3 dígitos
        if (txtCVV.getText().length() > 3) {
            txtCVV.setText(txtCVV.getText().substring(0, 3));
        }
    });
    }

    //limitamos el nombre del titular a 30 caracteres y que solo acepte letras y espacios
    private void limitarNombreTitular() {
        txtCardHolder.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*")) {
            txtCardHolder.setText(newValue.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑ ]", ""));
            }

        // Opcional: limitar longitud (por ejemplo, 30 caracteres)
            if (txtCardHolder.getText().length() > 30) {
                txtCardHolder.setText(txtCardHolder.getText().substring(0, 30));
            }
        });
    }

    //limitar a 3 digitos el cvv cuando el cvv es visible
    private void limitarCvvVisible() {
        // Limitar a 3 dígitos
        CVVvisible.textProperty().addListener((observable, oldValue, newValue) -> {
        // Eliminar todo lo que no sean dígitos
        if (!newValue.matches("\\d*")) {
            CVVvisible.setText(newValue.replaceAll("[^\\d]", ""));
        }

        // Limitar a 3 dígitos
        if (CVVvisible.getText().length() > 3) {
            CVVvisible.setText(CVVvisible.getText().substring(0, 3));
        }
    });
    }

    private void initializeUI() {
        if (initialized) return;

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Pago de Servicios");

           FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaPago.fxml"));
        loader.setController(this); 

        Parent root = loader.load();

        // Forzar configuración/refresh de ComboBoxes en el siguiente tick de FX
        Platform.runLater(() -> {
    try {
        System.out.println("Post-load: forzando configurarComboBoxesFecha()");
        configurarComboBoxesFecha();
        // debug: imprimir tamaños
        System.out.println("cbMes items: " + (cbMes != null ? cbMes.getItems().size() : "NULL"));
        System.out.println("cbAno items: " + (cbAno != null ? cbAno.getItems().size() : "NULL"));
    } catch (Exception ex) {
        ex.printStackTrace();
    }
});

        Scene scene = new Scene(root, 640, 400);
        stage.setScene(scene);

        initialized = true;
    }catch (IOException e) {
        e.printStackTrace();
    }
}

    @FXML
    private void handlePagado(){
        if (control != null) {
            //control.servicioPagado();
        }
    }

    public void setControlPago(ControlPagar control) {
        this.control = control;
    }

    public void mostrar() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::mostrar);
            return;
        }
        initializeUI();
        stage.show();
    }
}