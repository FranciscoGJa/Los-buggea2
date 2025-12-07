package mx.uam.ayd.proyecto.presentacion.agregarPaciente;

import java.io.IOException;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Ventana para registrar nuevos pacientes.
 *
 * Se usa como "contenido" dentro del menú (no crea Stage propio).
 * NO debe haber fx:controller en el archivo FXML.
 */
@Component
public class VentanaAgregarPaciente {

    private Parent root;
    private boolean initialized = false;

    // Referencia al controlador de la HU
    private ControlAgregarPaciente controlAgregarPaciente;

    // ====== CAMPOS FXML ======
    @FXML
    private TextField textFieldNombre;

    @FXML
    private TextField textFieldTelefono;

    @FXML
    private TextField textFieldCorreo;

    @FXML
    private TextField textFieldEdad;

    @FXML
    private Button btnBAI;

    @FXML
    private Button btnBDI;

    @FXML
    private Button btnCEPER;

    // =========================================================
    //                    CONSTRUCTOR
    // =========================================================

    public VentanaAgregarPaciente() {
        // Spring inyecta dependencias después
    }

    /**
     * Lo llama Spring desde ControlAgregarPaciente para enlazar ambos.
     */
    public void setControlAgregarPaciente(ControlAgregarPaciente controlAgregarPaciente) {
        this.controlAgregarPaciente = controlAgregarPaciente;
    }

    /**
     * Carga el FXML una sola vez.
     * NO debe haber fx:controller en el archivo FXML.
     */
    public void cargarFXML() {
        if (initialized) {
            return;
        }

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/fxml/ventanaAgregarPaciente.fxml"));

            // Esta clase es el controller del FXML
            loader.setController(this);
            root = loader.load();
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
            muestraDialogoConMensaje("Error al cargar ventanaAgregarPaciente.fxml:\n\n" + e.getMessage());
        }
    }

    /**
     * Devuelve el nodo raíz para insertarlo en el StackPane del menú.
     */
    public Node getVista() {
        if (!initialized) {
            cargarFXML();
        }
        return root;
    }

    /**
     * Limpia todos los campos.
     */
    private void limpiarCampos() {
        if (textFieldNombre != null) textFieldNombre.clear();
        if (textFieldTelefono != null) textFieldTelefono.clear();
        if (textFieldCorreo != null) textFieldCorreo.clear();
        if (textFieldEdad != null) textFieldEdad.clear();
    }

    /**
     * Deshabilita los botones de baterías.
     */
    public void deshabilitarBaterias() {
        if (btnBAI != null) btnBAI.setDisable(true);
        if (btnBDI != null) btnBDI.setDisable(true);
        if (btnCEPER != null) btnCEPER.setDisable(true);
    }

    /**
     * Habilita los botones de baterías.
     */
    public void habilitarBaterias() {
        if (btnBAI != null) btnBAI.setDisable(false);
        if (btnBDI != null) btnBDI.setDisable(false);
        if (btnCEPER != null) btnCEPER.setDisable(false);
    }

    /**
     * Se llama desde ControlAgregarPaciente.inicia().
     * Solo prepara la vista y limpia los campos.
     */
    public void inicia() {
        if (!initialized) {
            cargarFXML();
        }
        limpiarCampos();
        deshabilitarBaterias(); // al iniciar nuevo paciente, baterías apagadas
    }

    /**
     * Muestra un diálogo informativo.
     */
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

    // =========================================================
    //              MANEJADORES DE BOTONES
    // =========================================================

    /**
     * Botón "Agregar paciente".
     */
    @FXML
    private void handleAgregarPaciente() {
        String nombre = textFieldNombre.getText().trim();
        String telefono = textFieldTelefono.getText().trim();
        String correo = textFieldCorreo.getText().trim();
        String edadStr = textFieldEdad.getText().trim();

        String regexCorreo = "^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$";
        String regexTelefono = "^(\\+\\d{1,3}\\s?)?\\d{7,15}$";

        StringBuilder errores = new StringBuilder();

        // Campos vacíos
        if (nombre.isEmpty()) errores.append("- El nombre no debe estar vacío.\n");
        if (telefono.isEmpty()) errores.append("- El teléfono no debe estar vacío.\n");
        if (correo.isEmpty()) errores.append("- El correo no debe estar vacío.\n");
        if (edadStr.isEmpty()) errores.append("- La edad no debe estar vacía.\n");

        // Formato correo
        if (!correo.isEmpty() && !correo.matches(regexCorreo)) {
            errores.append("- El correo no tiene un formato válido.\n");
        }

        // Formato teléfono
        if (!telefono.isEmpty() && !telefono.matches(regexTelefono)) {
            errores.append("- El número de teléfono no es válido.\n");
        }

        // Edad
        if (!edadStr.isEmpty()) {
            try {
                int edad = Integer.parseInt(edadStr);
                if (edad <= 5) {
                    errores.append("- La edad debe ser mayor a 5 años.\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- La edad debe ser un número válido.\n");
            }
        }

        if (errores.length() > 0) {
            muestraDialogoConMensaje(
                    "Por favor corrige los siguientes errores:\n\n" + errores.toString()
            );
            return;
        }

        int edad = Integer.parseInt(edadStr);

        if (controlAgregarPaciente != null) {
            controlAgregarPaciente.agregarPaciente(nombre, correo, telefono, edad);
        } else {
            System.out.println("[VentanaAgregarPaciente] controlAgregarPaciente es null");
        }
    }

    @FXML
    private void handleCeper() {
        if (controlAgregarPaciente != null) {
            controlAgregarPaciente.agregarCEPER();
        } else {
            muestraDialogoConMensaje("No hay paciente seleccionado para CEPER.");
        }
    }

    @FXML
    private void handleBAI() {
        if (controlAgregarPaciente != null) {
            controlAgregarPaciente.agregarBAI();
        } else {
            muestraDialogoConMensaje("No hay paciente seleccionado para BAI.");
        }
    }

    @FXML
    private void handleBDI() {
        if (controlAgregarPaciente != null) {
            controlAgregarPaciente.agregarBDI();
        } else {
            muestraDialogoConMensaje("No hay paciente seleccionado para BDI-II.");
        }
    }
}
