package mx.uam.ayd.proyecto.presentacion.contestarHistorialClinico;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;

import mx.uam.ayd.proyecto.negocio.ServicioHistorialClinico;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.presentacion.asignarPsicologo.ControlAsignarPsicologo;

/**
 * Controlador para la ventana de contestación del historial clínico.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Inicializar la vista {@link VentanaContestarHistorialClinico} y vincularla con este controlador.</li>
 *   <li>Mostrar la ventana para completar el historial clínico de un paciente.</li>
 *   <li>Guardar los datos del historial clínico a través de {@link ServicioHistorialClinico}.</li>
 *   <li>Almacenar el paciente actual y, tras guardar, iniciar el flujo de asignación de psicólogo.</li>
 *   <li>Cerrar la ventana al finalizar.</li>
 * </ul>
 * </p>
 *
 * <p>Flujo típico:
 * <ol>
 *   <li>Llamar a {@link #inicia(Paciente)} para mostrar la ventana de historial clínico.</li>
 *   <li>Completar el formulario y confirmar.</li>
 *   <li>El controlador guarda la información, cierra la ventana e inicia la asignación de psicólogo.</li>
 * </ol>
 * </p>
 *
 * @version 1.0
 */
@Component
public class ControlContestarHistorialClinico {
    
    // Dependencias 
    private final VentanaContestarHistorialClinico ventanaContestarHistorialClinico;
    private final ServicioHistorialClinico servicioHistorialClinico;
    private final ControlAsignarPsicologo controlAsignarPsicologo;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param ventanaContestarHistorialClinico vista para contestar historial clínico
     * @param servicioHistorialClinico servicio de negocio para historial clínico
     * @param controlAsignarPsicologo controlador de asignación de psicólogo
     */
    @Autowired
    public ControlContestarHistorialClinico(
            VentanaContestarHistorialClinico ventanaContestarHistorialClinico,
            ServicioHistorialClinico servicioHistorialClinico, 
            ControlAsignarPsicologo controlAsignarPsicologo) {
        this.ventanaContestarHistorialClinico = ventanaContestarHistorialClinico;
        this.servicioHistorialClinico = servicioHistorialClinico;
        this.controlAsignarPsicologo = controlAsignarPsicologo;
    }

    /**
     * Método que se ejecuta después de la construcción del bean
     * y realiza la conexión bidireccional entre el control y la ventana
     */
    @PostConstruct
    public void inicializa(){
        ventanaContestarHistorialClinico.setControlContestarHistorialClinico(this);
    }

    private Paciente pacienteActual;
    
    /**
     * Inicia el controlador con el paciente actual.
     * 
     * @param paciente El paciente para el cual se contestará el historial clínico
     */
    public void inicia(Paciente paciente) {
        this.pacienteActual = paciente;
        ventanaContestarHistorialClinico.muestra(paciente.getNombre(),paciente.getCorreo());
    }

    /**
     * Guarda el historial clínico del paciente.
     * 
     * @param nombre El nombre del paciente
     * @param correo El correo del paciente
     * @param motivo El motivo de la consulta
     * @param consumoDrogas Información sobre el consumo de drogas
     * @param descripcion Descripción del consumo de drogas
     * @param consentimientoAceptado Indica si se aceptó el consentimiento
     */
    public void guardarHistorialClinico(String nombre, String correo, String motivo, String consumoDrogas, String descripcion, boolean consentimientoAceptado){
        try {
			servicioHistorialClinico.guardarHistorialClinico(nombre, correo, motivo, consumoDrogas, descripcion, consentimientoAceptado);
			ventanaContestarHistorialClinico.muestraDialogoConMensaje("Historial clinico agregado exitosamente");	
            termina(); //Solo termina la ventana si se ha guardado el HC
            this.controlAsignarPsicologo(this.pacienteActual); //Inicia asignar psicologo
        } catch(Exception ex) {
			ventanaContestarHistorialClinico.muestraDialogoConMensaje("Error: \n \n"+ex.getMessage());
		}
    }
    /**
     * Inicia el proceso de asignación de psicólogo para el paciente dado.
     * 
     * @param paciente El paciente al que se le asignará un psicólogo
     */
    public void controlAsignarPsicologo(Paciente paciente) {
        controlAsignarPsicologo.inicia(paciente);
    }
    
    private void termina(){
        ventanaContestarHistorialClinico.setVisible(false);
    }
}
