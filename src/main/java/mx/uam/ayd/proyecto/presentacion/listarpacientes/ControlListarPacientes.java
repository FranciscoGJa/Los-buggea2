package mx.uam.ayd.proyecto.presentacion.listarpacientes;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.ServicioBateriaClinica;
import mx.uam.ayd.proyecto.negocio.ServicioPaciente;
import mx.uam.ayd.proyecto.negocio.modelo.BateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.HistorialClinico;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;

/**
 * Controlador para la funcionalidad de listar pacientes.
 * <p>
 * Esta clase actúa como intermediario entre la vista {@link VentanaListarPacientes}
 * y los servicios de la capa de negocio. Se encarga de la lógica de la aplicación
 * para esta característica: obtener los datos, responder a las interacciones del usuario
 * y actualizar la vista según sea necesario.
 */
@Component
public class ControlListarPacientes {

    @Autowired
    private VentanaListarPacientes ventana;

    @Autowired
    private ServicioPaciente servicioPaciente;

    @Autowired
    private ServicioBateriaClinica servicioBateriaClinica;

    /**
     * Inicia la vista de listado de pacientes.
     * <p>Obtiene la lista completa de pacientes desde el servicio y
     * la pasa a la ventana para su visualización.</p>
     */
    public void inicia() {
        List<Paciente> todosLosPacientes = servicioPaciente.recuperarTodosLosPacientes();
        ventana.muestra(this, todosLosPacientes);
    }

    /**
     * Maneja la selección de un paciente en la vista.
     * <p>Actualiza la interfaz limpiando datos anteriores y mostrando
     * las baterías clínicas y el historial del paciente seleccionado.</p>
     *
     * @param paciente Paciente seleccionado en la lista; puede ser {@code null}
     */
    public void seleccionarPaciente(Paciente paciente) {
        // Limpia los paneles de la vista para evitar mostrar datos de selecciones anteriores
        ventana.limpiarDetallesDeBateria();
        ventana.limpiarHistorialEnPestana();

        if (paciente != null) {
            // Muestra las baterías y el historial clínico asociados al paciente
            ventana.mostrarBaterias(paciente.getBateriasClinicas());
            
            HistorialClinico historial = paciente.getHistorialClinico();
            if (historial != null) {
                ventana.mostrarHistorialEnPestana(historial);
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
     * Obtiene una cadena formateada con los detalles de una batería y la muestra en un diálogo de información.
     * @param bateria La batería de la cual se quieren ver los detalles completos.
     */
    public void abrirDetallesBateria(BateriaClinica bateria) {
        String detalles = servicioBateriaClinica.obtenerDetallesBateria(bateria);
        ventana.muestraDialogoDeInformacion(detalles);
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
    
    /**
     * Cierra la ventana de listado de pacientes.
     */
    public void cerrar() {
        ventana.cierra();
    }
}