package mx.uam.ayd.proyecto.presentacion.CrearPDFHistorial;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;

@Slf4j
@Component
public class ControladorPacientes {

    @FXML
    private TableView<Paciente> tablaPacientes;
    
    @FXML
    private TableColumn<Paciente, String> columnaNombre;
    
    @FXML
    private TableColumn<Paciente, String> columnaTelefono;
    
    @Autowired
    private RestTemplate restTemplate;

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        
        // Cargar datos de pacientes...
    }

    /**
     * Método para generar PDF del historial del paciente seleccionado
     */
    @FXML
    public void generarPdfHistorial() {
        Paciente pacienteSeleccionado = tablaPacientes.getSelectionModel().getSelectedItem();
        
        if (pacienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", 
                "Por favor, seleccione un paciente de la tabla.");
            return;
        }
        
        try {
            // Mostrar diálogo de progreso
            mostrarAlerta(Alert.AlertType.INFORMATION, "Generando PDF", 
                "Generando historial clínico para " + pacienteSeleccionado.getNombre() + "...");
            
            // Llamar al endpoint REST
            String url = "http://localhost:8080/api/historial/paciente/" + 
                        pacienteSeleccionado.getId() + "/pdf";
            
            ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Guardar archivo localmente
                String nombreArchivo = "historial_" + pacienteSeleccionado.getNombre().replace(" ", "_") + ".pdf";
                File archivo = new File(nombreArchivo);
                
                try (FileOutputStream fos = new FileOutputStream(archivo)) {
                    fos.write(response.getBody());
                }
                
                // Abrir el PDF
                abrirArchivo(archivo);
                
                mostrarAlerta(Alert.AlertType.INFORMATION, "PDF Generado", 
                    "El historial clínico se ha generado exitosamente: " + archivo.getName());
                    
            } else {
                throw new Exception("Error en la respuesta del servidor");
            }
            
        } catch (Exception e) {
            log.error("Error al generar PDF: {}", e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error", 
                "No se pudo generar el PDF: " + e.getMessage());
        }
    }

    private void abrirArchivo(File archivo) {
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(archivo);
            }
        } catch (Exception e) {
            log.warn("No se pudo abrir el archivo automáticamente: {}", e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}