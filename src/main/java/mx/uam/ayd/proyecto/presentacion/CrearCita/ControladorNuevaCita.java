package mx.uam.ayd.proyecto.presentacion.CrearCita;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.ServicioCita;
import mx.uam.ayd.proyecto.negocio.ServicioCalendario;
import mx.uam.ayd.proyecto.negocio.ServicioPsicologo;
import mx.uam.ayd.proyecto.negocio.ServicioPerfilCitas;
import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
/*
 * Este controlador maneja la lógica para crear una nueva cita
 * entre un paciente y un psicólogo.
 * Permite seleccionar fecha, hora, psicólogo y detalles de la consulta.
 * Valida los datos y utiliza los servicios correspondientes para
 * crear en el repositorio de citas.
 */
@Component
public class ControladorNuevaCita {
    
    @FXML private DatePicker datePickerFecha;
    @FXML private ComboBox<LocalTime> comboHorarios;
    @FXML private ComboBox<Psicologo> comboPsicologos;
    @FXML private TextArea txtMotivoConsulta;
    @FXML private TextArea txtDetallesAdicionales;
    @FXML private Label lblFechaSeleccionada;
    @FXML private Label lblHoraSeleccionada;
    @FXML private Label lblPsicologoSeleccionado;
    
    @Autowired
    private ServicioCita servicioCita;
    
    @Autowired
    private ServicioCalendario servicioCalendario;
    
    @Autowired
    private ServicioPsicologo servicioPsicologo;
    
    @Autowired
    private ServicioPerfilCitas servicioPerfilCitas;
    
    private PerfilCitas perfilActual;
    
    @FXML
    public void initialize() {
        configurarControles();
        cargarPsicologos();
    }
    
    private void configurarControles() {
        // Configurar date picker para no permitir fechas pasadas
        datePickerFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isBefore(LocalDate.now()));
            }
        });
        
        // Configurar ComboBox de psicólogos para mostrar el nombre
        comboPsicologos.setCellFactory(param -> new ListCell<Psicologo>() {
            @Override
            protected void updateItem(Psicologo psicologo, boolean empty) {
                super.updateItem(psicologo, empty);
                if (empty || psicologo == null) {
                    setText(null);
                } else {
                    setText(psicologo.getNombre() + " - " + psicologo.getEspecialidad());
                }
            }
        });
        
        comboPsicologos.setButtonCell(new ListCell<Psicologo>() {
            @Override
            protected void updateItem(Psicologo psicologo, boolean empty) {
                super.updateItem(psicologo, empty);
                if (empty || psicologo == null) {
                    setText("Seleccione un psicólogo");
                } else {
                    setText(psicologo.getNombre() + " - " + psicologo.getEspecialidad());
                }
            }
        });
        
        // Listener para cuando se selecciona un psicólogo
        comboPsicologos.valueProperty().addListener((obs, oldPsicologo, newPsicologo) -> {
            if (newPsicologo != null) {
                lblPsicologoSeleccionado.setText("Psicólogo: " + newPsicologo.getNombre());
                // Si ya hay fecha seleccionada, cargar horarios
                if (datePickerFecha.getValue() != null) {
                    cargarHorariosDisponibles(datePickerFecha.getValue(), newPsicologo.getId());
                }
            } else {
                lblPsicologoSeleccionado.setText("Psicólogo: No seleccionado");
                comboHorarios.getItems().clear();
                comboHorarios.setPromptText("Seleccione psicólogo primero");
            }
        });
        
        // Listener para cuando se selecciona una fecha
        datePickerFecha.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null && comboPsicologos.getValue() != null) {
                cargarHorariosDisponibles(newDate, comboPsicologos.getValue().getId());
            } else if (newDate != null) {
                comboHorarios.getItems().clear();
                comboHorarios.setPromptText("Seleccione psicólogo primero");
            }
        });
        
        // Listener para cuando se selecciona un horario
        comboHorarios.valueProperty().addListener((obs, oldTime, newTime) -> {
            if (newTime != null && datePickerFecha.getValue() != null) {
                lblFechaSeleccionada.setText("Fecha: " + datePickerFecha.getValue());
                lblHoraSeleccionada.setText("Hora: " + newTime);
            }
        });
        
        // Limitar caracteres en text areas
        txtMotivoConsulta.setTextFormatter(new TextFormatter<String>(change -> 
            change.getControlNewText().length() <= 500 ? change : null));
        txtDetallesAdicionales.setTextFormatter(new TextFormatter<String>(change -> 
            change.getControlNewText().length() <= 500 ? change : null));
    }
    
    private void cargarPsicologos() {
        try {
            List<Psicologo> psicologos = servicioPsicologo.listarPsicologos();
            comboPsicologos.getItems().clear();
            comboPsicologos.getItems().addAll(psicologos);
            
            if (psicologos.isEmpty()) {
                comboPsicologos.setPromptText("No hay psicólogos registrados");
                mostrarAlerta("Información", "No hay psicólogos registrados en el sistema. Por favor, registre al menos un psicólogo antes de agendar citas.");
            } else {
                comboPsicologos.setPromptText("Seleccione un psicólogo");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los psicólogos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga los datos del perfil para agendar cita
     */
    public void cargarPerfil(PerfilCitas perfil) {
        this.perfilActual = perfil;
        
        // Si el perfil ya tiene un psicólogo asignado, seleccionarlo automáticamente
        if (perfilActual.getPsicologo() != null) {
            for (Psicologo psicologo : comboPsicologos.getItems()) {
                if (psicologo.getId() == perfilActual.getPsicologo().getId()) {
                    comboPsicologos.setValue(psicologo);
                    break;
                }
            }
        }
        
        limpiarFormulario();
    }
    
    private void cargarHorariosDisponibles(LocalDate fecha, Integer psicologoId) {
        comboHorarios.getItems().clear();
        
        try {
            List<LocalTime> horariosDisponibles = servicioCalendario.obtenerHorariosDisponibles(psicologoId, fecha);
            comboHorarios.getItems().addAll(horariosDisponibles);
            
            if (horariosDisponibles.isEmpty()) {
                comboHorarios.setPromptText("No hay horarios disponibles");
            } else {
                comboHorarios.setPromptText("Seleccione horario");
            }
        } catch (Exception e) {
            comboHorarios.setPromptText("Error al cargar horarios");
            // No mostrar alerta para no molestar al usuario, solo log
            System.err.println("Error al cargar horarios: " + e.getMessage());
        }
    }
    
    /**
     * Crea la nueva cita
     */
    @FXML
    public void crearCita() {
        try {
            // Validaciones
            if (!validarFormulario()) {
                return;
            }
            
            LocalDate fecha = datePickerFecha.getValue();
            LocalTime hora = comboHorarios.getValue();
            String motivo = txtMotivoConsulta.getText().trim();
            String detalles = txtDetallesAdicionales.getText().trim();
            Psicologo psicologoSeleccionado = comboPsicologos.getValue();
            
            // Si el perfil no tiene psicólogo asignado, asignarlo
            if (perfilActual.getPsicologo() == null || 
                perfilActual.getPsicologo().getId() != psicologoSeleccionado.getId()) {
                servicioPerfilCitas.asignarPsicologo(perfilActual.getIdPerfil(), psicologoSeleccionado);
            }
            
                // Crear la cita
                servicioCita.crearCita(
                perfilActual.getIdPerfil(),
                psicologoSeleccionado.getId(),
                fecha,
                hora,
                motivo + (detalles.isEmpty() ? "" : "\n\nDetalles adicionales:\n" + detalles)
            );
            
            mostrarAlerta("Cita creada", 
                "Cita agendada exitosamente:\n" +
                "Fecha: " + fecha + "\n" +
                "Hora: " + hora + "\n" +
                "Psicólogo: " + psicologoSeleccionado.getNombre() + "\n" +
                "Paciente: " + perfilActual.getNombreCompleto());
            
            cerrarVentana();
            
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo crear la cita: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    public void cancelar() {
        cerrarVentana();
    }
    
    private boolean validarFormulario() {
        if (comboPsicologos.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione un psicólogo");
            comboPsicologos.requestFocus();
            return false;
        }
        
        if (datePickerFecha.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione una fecha");
            datePickerFecha.requestFocus();
            return false;
        }
        
        if (comboHorarios.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione un horario disponible");
            comboHorarios.requestFocus();
            return false;
        }
        
        if (txtMotivoConsulta.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El motivo de la consulta es obligatorio");
            txtMotivoConsulta.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void limpiarFormulario() {
        datePickerFecha.setValue(null);
        comboHorarios.getItems().clear();
        comboHorarios.setValue(null);
        txtMotivoConsulta.clear();
        txtDetallesAdicionales.clear();
        lblFechaSeleccionada.setText("Fecha: No seleccionada");
        lblHoraSeleccionada.setText("Hora: No seleccionada");
        lblPsicologoSeleccionado.setText("Psicólogo: No seleccionado");
    }
    
    private void cerrarVentana() {
        if (datePickerFecha != null) {
            javafx.stage.Stage stage = (javafx.stage.Stage) datePickerFecha.getScene().getWindow();
            stage.close();
        }
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}