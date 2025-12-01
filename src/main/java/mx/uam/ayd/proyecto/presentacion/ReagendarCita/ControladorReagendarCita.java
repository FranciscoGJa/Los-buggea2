package mx.uam.ayd.proyecto.presentacion.ReagendarCita;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.ServicioCita;
import mx.uam.ayd.proyecto.negocio.ServicioCalendario;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador para la funcionalidad de reagendar citas.
 * Permite a los usuarios seleccionar una nueva fecha y hora para una cita existente.
 * Incluye validaciones para asegurar que la nueva cita no sea en el pasado
 * y que no coincida con la cita original.
 */

@Component
public class ControladorReagendarCita {
    
    @FXML private Label lblPaciente;
    @FXML private Label lblPsicologo;
    @FXML private Label lblCitaOriginal;
    @FXML private DatePicker datePickerNuevaFecha;
    @FXML private ComboBox<LocalTime> comboHorarios;
    @FXML private Button btnVerificarDisponibilidad;
    @FXML private Button btnConfirmarReagendo;
    @FXML private Button btnCancelar;
    @FXML private TextArea txtMotivo;
    
    @Autowired
    private ServicioCita servicioCita;
    
    @Autowired
    private ServicioCalendario servicioCalendario;
    
    private Cita citaSeleccionada;
    private LocalDate fechaSeleccionada;
    private LocalTime horarioSeleccionado;
    
    @FXML
    public void initialize() {
        configurarControles();
    }
    
    private void configurarControles() {
        // Configurar DatePicker para no permitir fechas pasadas
        datePickerNuevaFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        
        // Configurar listener para fecha seleccionada
        datePickerNuevaFecha.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                fechaSeleccionada = newDate;
                cargarHorariosDisponibles();
            }
        });
        
        // Configurar listener para horario seleccionado
        comboHorarios.valueProperty().addListener((obs, oldTime, newTime) -> {
            horarioSeleccionado = newTime;
            btnConfirmarReagendo.setDisable(newTime == null);
        });
    }
    
    public void setCitaSeleccionada(Cita cita) {
        this.citaSeleccionada = cita;
        cargarInformacionCita();
    }
    
    private void cargarInformacionCita() {
        if (citaSeleccionada != null) {
            // Manejo seguro de posibles valores nulos
            String nombrePaciente = "No disponible";
            String nombrePsicologo = "No disponible";
            String citaOriginal = "No disponible";
            
            if (citaSeleccionada.getPerfilCitas() != null) {
                nombrePaciente = citaSeleccionada.getPerfilCitas().getNombreCompleto();
            }
            
            if (citaSeleccionada.getPsicologo() != null) {
                nombrePsicologo = citaSeleccionada.getPsicologo().getNombre();
            }
            
            if (citaSeleccionada.getFechaCita() != null && citaSeleccionada.getHoraCita() != null) {
                citaOriginal = citaSeleccionada.getFechaCita() + " " + citaSeleccionada.getHoraCita();
            }
            
            lblPaciente.setText("Paciente: " + nombrePaciente);
            lblPsicologo.setText("Psicólogo: " + nombrePsicologo);
            lblCitaOriginal.setText("Cita Original: " + citaOriginal);
            
            // Establecer fecha mínima como hoy
            datePickerNuevaFecha.setValue(LocalDate.now());
        }
    }
    
    private void cargarHorariosDisponibles() {
        if (fechaSeleccionada != null && citaSeleccionada != null && citaSeleccionada.getPsicologo() != null) {
            try {
                List<LocalTime> horariosDisponibles = servicioCalendario.obtenerHorariosDisponibles(
                    citaSeleccionada.getPsicologo().getId(), fechaSeleccionada);
                
                comboHorarios.getItems().clear();
                comboHorarios.getItems().addAll(horariosDisponibles);
                
                if (horariosDisponibles.isEmpty()) {
                    mostrarAlerta("Sin horarios disponibles", 
                                "No hay horarios disponibles para la fecha seleccionada.");
                }
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudieron cargar los horarios disponibles: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void verificarDisponibilidad() {
        if (fechaSeleccionada == null || horarioSeleccionado == null) {
            mostrarAlerta("Datos incompletos", "Seleccione una fecha y horario para verificar disponibilidad.");
            return;
        }
        
        if (citaSeleccionada == null || citaSeleccionada.getPsicologo() == null) {
            mostrarAlerta("Error", "No hay información del psicólogo disponible.");
            return;
        }
        
        boolean disponible = servicioCalendario.verificarDisponibilidad(
            citaSeleccionada.getPsicologo().getId(), fechaSeleccionada, horarioSeleccionado);
        
        if (disponible) {
            mostrarAlerta("Disponible", "El horario seleccionado está disponible.");
        } else {
            mostrarAlerta("No disponible", "El horario seleccionado no está disponible.");
        }
    }
    
    @FXML
    private void confirmarReagendo() {
        if (validarDatosReagendo()) {
            try {
                // Usar el método de reagendar del servicio
                servicioCita.reagendarCita(
                    citaSeleccionada.getId(), 
                    fechaSeleccionada, 
                    horarioSeleccionado, 
                    txtMotivo.getText()
                );
                
                mostrarAlerta("Éxito", "Cita reagendada exitosamente.");
                cerrarVentana();
                
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo reagendar la cita: " + e.getMessage());
            }
        }
    }
    
    private boolean validarDatosReagendo() {
        if (fechaSeleccionada == null) {
            mostrarAlerta("Validación", "Seleccione una fecha para el reagendo.");
            return false;
        }
        
        if (horarioSeleccionado == null) {
            mostrarAlerta("Validación", "Seleccione un horario para el reagendo.");
            return false;
        }
        
        if (fechaSeleccionada.isBefore(LocalDate.now())) {
            mostrarAlerta("Validación", "No puede seleccionar una fecha pasada.");
            return false;
        }
        
        // Verificar que no sea el mismo horario original
        if (citaSeleccionada.getFechaCita() != null && 
            fechaSeleccionada.equals(citaSeleccionada.getFechaCita()) && 
            horarioSeleccionado.equals(citaSeleccionada.getHoraCita())) {
            mostrarAlerta("Validación", "La nueva cita no puede ser en la misma fecha y hora que la original.");
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void cancelar() {
        cerrarVentana();
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}