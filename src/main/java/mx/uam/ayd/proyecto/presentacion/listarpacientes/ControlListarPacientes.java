package mx.uam.ayd.proyecto.presentacion.listarpacientes;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
import javafx.scene.Node;
import mx.uam.ayd.proyecto.negocio.ServicioBateriaClinica;
import mx.uam.ayd.proyecto.negocio.ServicioHistorialPdf;
import mx.uam.ayd.proyecto.negocio.ServicioPaciente;
import mx.uam.ayd.proyecto.negocio.modelo.BateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.HistorialClinico;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;

import org.springframework.transaction.annotation.Transactional;
// Importaciones de los controladores de baterías
import mx.uam.ayd.proyecto.presentacion.agregarBAI.ControlAgregarBAI;
import mx.uam.ayd.proyecto.presentacion.agregarBDI.ControlAgregarBDI;
import mx.uam.ayd.proyecto.presentacion.agregarCEPER.ControlAgregarCEPER;

/**
 * Controlador para la funcionalidad de listar pacientes.
 * <p>
 * Esta clase actúa como intermediario entre la vista {@link VentanaListarPacientes}
 * y los servicios de la capa de negocio.
 */
@Component
public class ControlListarPacientes {

    @Autowired
    private VentanaListarPacientes ventana;

    @Autowired
    private ServicioPaciente servicioPaciente;

    @Autowired
    private ServicioBateriaClinica servicioBateriaClinica;

    // Inyecciones de controladores de baterías para abrir detalles
    @Autowired
    private ControlAgregarBAI controlAgregarBAI;
    
    @Autowired
    private ControlAgregarBDI controlAgregarBDI;
    
    @Autowired
    private ControlAgregarCEPER controlAgregarCEPER;

    @Autowired
    private ServicioHistorialPdf servicioHistorialPdf; 


    /**
     * Inicia la vista de listado de pacientes.
     * <p>Obtiene la lista completa de pacientes desde el servicio y
     * la pasa a la ventana para su visualización.</p>
     */
     public void inicia() {
        // Cargar el archivo FXML solo una vez
        ventana.cargarFXML();
        // Obtener los pacientes desde el servicio
        List<Paciente> todosLosPacientes = servicioPaciente.recuperarTodosLosPacientes();
        // Pasar los datos a la vista
        ventana.muestra(this, todosLosPacientes);
    }
    
    public Node getVista() {
        return ventana.getVista();
    }   

    /**
     * Maneja la selección de un paciente en la vista.
     * Actualiza la interfaz limpiando datos anteriores y mostrando
     * las baterías clínicas y el historial del paciente seleccionado.
     *
     * @param paciente Paciente seleccionado en la lista; puede ser {@code null}
     */
    public void seleccionarPaciente(Paciente paciente) {
        // Limpia los paneles de la vista para evitar mostrar datos de selecciones anteriores
        ventana.limpiarDetallesDeBateria();
        ventana.limpiarHistorialEnPestana();

        if (paciente != null) {
            // Método que carga relaciones para evitar carga perezosa
            Paciente pacienteCompleto = servicioPaciente.obtenerPacienteConDetalles(paciente.getId());
            
            if(pacienteCompleto != null) {
                // Muestra las baterías y el historial clínico asociados al paciente
                ventana.mostrarBaterias(pacienteCompleto.getBateriasClinicas());
                
                HistorialClinico historial = pacienteCompleto.getHistorialClinico();
                if (historial != null) {
                    ventana.mostrarHistorialEnPestana(historial);
                }
            }
        }
    }
    
    /**
     * Se invoca cuando se selecciona una batería clínica en la lista de la vista.
     * Ordena a la ventana que muestre los detalles (puntuación y comentarios) de esa batería.
     */
    public void seleccionarBateria(BateriaClinica bateria) {
        if (bateria != null) {
            ventana.mostrarDetallesBateria(bateria);
        }
    }

    /**
     * Abre la ventana específica de la batería seleccionada con los datos cargados para visualización/edición.
     * @param bateria La batería seleccionada.
     */
    public void abrirDetallesBateria(BateriaClinica bateria) {
        if(bateria != null) {
            switch (bateria.getTipoDeBateria()) {
                case CEPER:
                    controlAgregarCEPER.iniciaEditar(bateria);
                    break;
                case BAI:
                    controlAgregarBAI.iniciaEditar(bateria);
                    break;
                case BDI_II:
                    controlAgregarBDI.iniciaEditar(bateria);
                    break;
                default:
                    ventana.muestraDialogoDeInformacion("Tipo de batería no soportado para visualización detallada.");
            }
        } else {
            ventana.muestraDialogoDeError("Debe seleccionar una batería primero.");
        }
    }

    /**
     * Guarda los comentarios que el usuario ha escrito o modificado para una batería clínica.
     * Delega la operación de guardado al servicio de negocio y notifica al usuario del resultado.
     *
     * @param bateria La batería a la que pertenecen los comentarios.
     * @param comentarios El nuevo texto de los comentarios a guardar.
     */
    public void guardarComentarios(BateriaClinica bateria, String comentarios) {
        if (bateria != null) {
            try {
                servicioBateriaClinica.guardarComentarios(bateria, comentarios);
                ventana.muestraDialogoDeInformacion("Comentarios guardados con éxito.");
            } catch (Exception e) {
                ventana.muestraDialogoDeError("Error al guardar: " + e.getMessage());
            }
        } else {
            ventana.muestraDialogoDeError("No hay una batería seleccionada para guardar comentarios.");
        }
    }
    public void generarPdfHistorial(Paciente paciente) {
    if (paciente == null) {
        ventana.muestraDialogoDeError("Por favor, seleccione un paciente primero.");
        return;
    }
    
    // Crear un hilo separado para no bloquear la UI
    new Thread(() -> {
        try {
            System.out.println("[BACKGROUND] Generando PDF para: " + paciente.getNombre());
            byte[] pdfBytes = servicioHistorialPdf.generarPdfHistorial(paciente.getId());
            System.out.println("[BACKGROUND] PDF generado, tamaño: " + pdfBytes.length + " bytes");
            
            // Guardar archivo
            String nombreArchivo = "historial_" + paciente.getNombre().replace(" ", "_") + ".pdf";
            java.io.File archivo = new java.io.File(nombreArchivo);
            
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(archivo)) {
                fos.write(pdfBytes);
            }
            System.out.println("[BACKGROUND] Archivo guardado: " + archivo.getAbsolutePath());
            
            // Volver al hilo de JavaFX
            javafx.application.Platform.runLater(() -> {
                abrirArchivo(archivo);
                ventana.muestraDialogoDeInformacion(
                    "PDF generado exitosamente: " + archivo.getName());
            });
            
        } catch (Exception e) {
            System.err.println("[BACKGROUND] ERROR: " + e.getMessage());
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> {
                ventana.muestraDialogoDeError("Error al generar PDF: " + e.getMessage());
            });
        }
    }).start();
    
    System.out.println("[UI] Hilo de fondo iniciado para generación de PDF");
    ventana.muestraDialogoDeInformacion("Generando PDF en segundo plano...");
}

    private void abrirArchivo(java.io.File archivo) {
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(archivo);
            }
        } catch (Exception e) {
            System.out.println("No se pudo abrir el archivo automáticamente: " + e.getMessage());
        }
    }

    /**
     * Cierra la ventana de listado de pacientes.
     */
    public void cerrar() {
        ventana.cierra();
    }
}