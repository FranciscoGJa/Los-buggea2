package mx.uam.ayd.proyecto.presentacion.CrearPerfilCitas;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.ServicioPerfilCitas;
import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;

/*
 * Controlador de VentanaCrearPerfilCitas 
 * Permite crear un nuevo perfil de citas para un paciente
 * sin asignar un psicólogo inicialmente.
 */

@Component
public class ControladorCrearPerfilCitas {
    
    @FXML
    private TextField txtNombreCompleto;
    
    @FXML
    private TextField txtEdad;
    
    @FXML
    private TextField txtOtroSexo;
    
    @FXML
    private TextField txtDireccion;
    
    @FXML
    private TextField txtOcupacion;
    
    @FXML
    private TextField txtTelefono;
    
    @FXML
    private TextField txtEmail;
    
  
    private ToggleGroup grupoSexo;
    
    @FXML
    private RadioButton radioHombre;
    
    @FXML
    private RadioButton radioMujer;
    
    @FXML
    private RadioButton radioOtro;

    @Autowired
    private ServicioPerfilCitas servicioPerfilCitas;

    public ControladorCrearPerfilCitas() {
        System.out.println("ControladorCrearPerfilCitas instanciado");
    }

    @FXML
    public void initialize() {
        System.out.println("Initialize() llamado en ControladorCrearPerfilCitas");
        System.out.println("Servicio inyectado: " + (servicioPerfilCitas != null));
        
        // CREAR EL TOGGLEGROUP PROGRAMÁTICAMENTE
        grupoSexo = new ToggleGroup();
        radioHombre.setToggleGroup(grupoSexo);
        radioMujer.setToggleGroup(grupoSexo);
        radioOtro.setToggleGroup(grupoSexo);
        
        // Configurar el comportamiento del radio button "Otro"
        radioOtro.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (txtOtroSexo != null) {
                txtOtroSexo.setDisable(!newValue);
                if (newValue) {
                    txtOtroSexo.requestFocus();
                } else {
                    txtOtroSexo.clear();
                }
            }
        });
        
        // Inicialmente deshabilitar el campo "Otro"
        if (txtOtroSexo != null) {
            txtOtroSexo.setDisable(true);
        }
    }

    /**
     * Método para guardar el nuevo perfil - PSICÓLOGO OPCIONAL
     */
    @FXML
    public void guardarPerfil() {
        try {
            System.out.println("Intentando guardar perfil...");
        
            // Validar campos obligatorios
            if (!validarCampos()) {
                return;
            }
        
            // Obtener los datos del formulario
            String nombreCompleto = txtNombreCompleto.getText().trim();
            String edadTexto = txtEdad.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String ocupacion = txtOcupacion.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();
        
            // Validar y convertir edad a número
            int edad;
            try {
                edad = Integer.parseInt(edadTexto);
                if (edad < 1 || edad > 120) {
                    mostrarAlerta("Error de validación", "La edad debe estar entre 1 y 120 años");
                    txtEdad.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Error de validación", "La edad debe ser un número válido");
                txtEdad.requestFocus();
                return;
            }
        
            // Obtener el sexo seleccionado
            String sexo = obtenerSexoSeleccionado();
        
            System.out.println("Datos a guardar - Nombre: " + nombreCompleto + ", Edad: " + edad + ", Sexo: " + sexo);
            System.out.println("Creando perfil SIN psicólogo...");
        
            // USAR EL NUEVO MÉTODO SIN PSICÓLOGO
            PerfilCitas perfilGuardado = servicioPerfilCitas.crearPerfilCitasSinPsicologo(
                nombreCompleto, edad, sexo, direccion, ocupacion, 
                telefono.isEmpty() ? null : telefono, 
                email.isEmpty() ? null : email
            );
        
            // Mostrar mensaje de éxito
            mostrarAlerta("Historial creado", 
                "Perfil de citas creado correctamente\n" +
                "Nombre: " + perfilGuardado.getNombreCompleto() + "\n" +
                "ID: " + perfilGuardado.getIdPerfil() + "\n\n" +
                "Nota: El psicólogo puede ser asignado posteriormente.");
        
            // CERRAR LA VENTANA AUTOMÁTICAMENTE después de guardar
            cancelar();
        
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error al guardar el perfil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método para cancelar y cerrar la ventana
     */
    @FXML
    public void cancelar() {
        try {
            // Cerrar la ventana actual
            javafx.stage.Stage stage = (javafx.stage.Stage) txtNombreCompleto.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para limpiar el formulario
     */
    @FXML
    public void limpiarFormulario() {
        limpiarFormularioCompleto();
    }

    /**
     * Valida que todos los campos obligatorios estén llenos
     */
    private boolean validarCampos() {
        if (txtNombreCompleto.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El nombre completo es obligatorio");
            txtNombreCompleto.requestFocus();
            return false;
        }
        
        if (txtEdad.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "La edad es obligatoria");
            txtEdad.requestFocus();
            return false;
        }
        
        if (grupoSexo.getSelectedToggle() == null) {
            mostrarAlerta("Validación", "Debe seleccionar un sexo");
            return false;
        }
        
        if (radioOtro.isSelected() && txtOtroSexo.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "Debe especificar el sexo en 'Otro'");
            txtOtroSexo.requestFocus();
            return false;
        }
        
        if (txtDireccion.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "La dirección es obligatoria");
            txtDireccion.requestFocus();
            return false;
        }
        
        if (txtOcupacion.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "La ocupación es obligatoria");
            txtOcupacion.requestFocus();
            return false;
        }
        
        return true;
    }

    /**
     * Obtiene el sexo seleccionado
     */
    private String obtenerSexoSeleccionado() {
        if (radioHombre.isSelected()) {
            return "Hombre";
        } else if (radioMujer.isSelected()) {
            return "Mujer";
        } else if (radioOtro.isSelected()) {
            return txtOtroSexo.getText().trim();
        }
        return "No especificado";
    }

    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarFormularioCompleto() {
        txtNombreCompleto.clear();
        txtEdad.clear();
        txtDireccion.clear();
        txtOcupacion.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtOtroSexo.clear();
        grupoSexo.selectToggle(null);
        txtOtroSexo.setDisable(true);
        txtNombreCompleto.requestFocus();
    }

    /**
     * Método para mostrar alertas al usuario
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}