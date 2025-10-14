package mx.uam.ayd.proyecto.presentacion.asignarPsicologo;

import mx.uam.ayd.proyecto.negocio.ServicioPaciente;
import mx.uam.ayd.proyecto.negocio.ServicioPsicologo;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;

import org.springframework.stereotype.Component;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;

/**
 * Controlador para la ventana de asignación de psicólogo a un paciente.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Inicializar la vista {@link VentanaAsignarPsicologo} y asociarla a este controlador.</li>
 *   <li>Obtener la lista de psicólogos disponibles según la edad del paciente.</li>
 *   <li>Mostrar la ventana con el paciente y la lista de psicólogos.</li>
 *   <li>Asignar un psicólogo al paciente y notificar el resultado.</li>
 * </ul>
 * </p>
 *
 * <p>Flujo típico:
 * <ol>
 *   <li>Llamar a {@link #inicia(Paciente)} para abrir la ventana con las opciones de psicólogo.</li>
 *   <li>Seleccionar un psicólogo y llamar a {@link #asignarPsicologo(Paciente, Psicologo)}.</li>
 *   <li>El controlador delega en {@link ServicioPaciente} y muestra el resultado en la vista.</li>
 *   <li>Si es exitoso, cierra la ventana.</li>
 * </ol>
 * </p>
 *
 * @version 1.0
 */
@Component
public class ControlAsignarPsicologo {
    private final VentanaAsignarPsicologo ventanaAsignarPsicologo;
    private final ServicioPsicologo servicioPsicologo;
    private final ServicioPaciente servicioPaciente;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param ventanaAsignarPsicologo vista de asignación
     * @param servicioPaciente servicio para operaciones con pacientes
     * @param servicioPsicologo servicio para operaciones con psicólogos
     */
    @Autowired
    public ControlAsignarPsicologo(VentanaAsignarPsicologo ventanaAsignarPsicologo,
                                    ServicioPaciente servicioPaciente,
                                    ServicioPsicologo servicioPsicologo){
                                        this.ventanaAsignarPsicologo = ventanaAsignarPsicologo;
                                        this.servicioPaciente = servicioPaciente;
                                        this.servicioPsicologo = servicioPsicologo;
    }
    /**
     * Método que se ejecuta después de la construcción del bean
     * y realiza la conexión bidireccional entre el control y la ventana
     */
    @PostConstruct
    public void init() {
        ventanaAsignarPsicologo.setControlAsignarPsicologo(this);
    }

    /**
     * Inicia el proceso de asignación de psicólogo para el paciente dado.
     * 
     * @param paciente El paciente al que se le asignará un psicólogo
     */
    public void inicia(Paciente paciente) {
        
        List<Psicologo> psicologos = servicioPsicologo.obtenerPsicologosPorEdadPaciente(paciente);
        ventanaAsignarPsicologo.muestra(paciente, psicologos);
    }

    /**
     * Asigna un psicólogo al paciente actual.
     * 
     * @param paciente El paciente al que se le asignará un psicólogo
     * @param psicologo El psicólogo que se asignará al paciente
     */
    public void asignarPsicologo(Paciente paciente, Psicologo psicologo) {

        try {
			servicioPaciente.asignarPsicologo(paciente, psicologo);
			ventanaAsignarPsicologo.muestraDialogoConMensaje("Psicólogo asignado exitosamente");	
            termina(); //Solo termina la ventana si se ha guardado el HC
        } catch(Exception ex) {
			ventanaAsignarPsicologo.muestraDialogoConMensaje("Error al asignar el psicólogo: "+ex.getMessage());
		}   
    }

    /**
     * Cierra la ventana de asignación.
     */
    private void termina(){
        ventanaAsignarPsicologo.setVisible(false);
    }
}