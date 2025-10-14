package mx.uam.ayd.proyecto.presentacion.agregarCEPER;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;

import java.util.List;

import mx.uam.ayd.proyecto.negocio.ServicioBateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.TipoBateria;

/**
 * Controlador para la ventana de registro de la batería CEPER
 * (Cuestionario de Evaluación Psicológica Específica para Rehabilitación).
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Inicializar la vista {@link VentanaAgregarCEPER} asociando este controlador.</li>
 *   <li>Configurar la vista con el ID del paciente y mostrarla.</li>
 *   <li>Delegar el guardado de respuestas al servicio de negocio 
 *       {@link ServicioBateriaClinica}.</li>
 * </ul>
 * </p>
 *
 * <p>Flujo típico:
 * <ol>
 *   <li>Invocar {@link #inicia(Long)} para abrir la ventana de captura de la batería.</li>
 *   <li>La vista llama a {@link #guardarCEPER(Long, List, String)} para registrar resultados.</li>
 * </ol>
 * </p>
 *
 * @see TipoBateria#CEPER
 * @see ServicioBateriaClinica
 * @see VentanaAgregarCEPER
 *
 * @version 1.0
 */
@Component
public class ControlAgregarCEPER {
    
    private final VentanaAgregarCEPER ventanaAgregarCEPER;
    private final ServicioBateriaClinica servicioBateriaClinica;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param ventanaAgregarCEPER vista de la batería CEPER
     * @param servicioBateriaClinica servicio que registra los resultados
     */
    @Autowired
    public ControlAgregarCEPER(
        VentanaAgregarCEPER ventanaAgregarCEPER,
        ServicioBateriaClinica servicioBateriaClinica) {
            this.ventanaAgregarCEPER=ventanaAgregarCEPER;
            this.servicioBateriaClinica=servicioBateriaClinica;
        }
    
    /**
     * Asocia este controlador con la vista tras la construcción del bean.
     */
    @PostConstruct
    public void inicializa(){
        ventanaAgregarCEPER.setControlAgregarCEPER(this);
    }

    /**
     * Inicia la ventana de captura de CEPER para un paciente.
     *
     * @param pacienteID identificador único del paciente
     */
    public void inicia(Long pacienteID){
        ventanaAgregarCEPER.setControlAgregarCEPER(this);
        ventanaAgregarCEPER.setPacienteID(pacienteID);
        ventanaAgregarCEPER.muestra();
    }

    /**
     * Guarda los resultados de la batería CEPER.
     *
     * @param pacienteID identificador del paciente
     * @param respuestas lista de respuestas
     * @param comentarios observaciones adicionales
     */
    public void guardarCEPER(Long pacienteID, List<Integer> respuestas, String comentarios) {
        servicioBateriaClinica.registrarBateria(
            pacienteID,
            TipoBateria.CEPER,
            respuestas,
            comentarios
        );
    }    
}
