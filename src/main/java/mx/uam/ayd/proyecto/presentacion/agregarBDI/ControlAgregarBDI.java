package mx.uam.ayd.proyecto.presentacion.agregarBDI;

import org.springframework.stereotype.Component;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioBateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.TipoBateria;

/**
 * Controlador para la ventana de registro de la batería BDI-II 
 * (Inventario de Depresión de Beck, segunda edición).
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Inicializar la vista {@link VentanaAgregarBDI} asociando este controlador.</li>
 *   <li>Configurar la vista con el ID del paciente y mostrarla.</li>
 *   <li>Delegar el guardado de las respuestas al servicio de negocio 
 *       {@link ServicioBateriaClinica}.</li>
 * </ul>
 * </p>
 *
 * <p>Flujo típico:
 * <ol>
 *   <li>Llamar a {@link #inicia(Long)} para abrir la ventana de captura del BDI-II.</li>
 *   <li>La vista invoca {@link #guardarBDI(Long, List, String)} para registrar resultados.</li>
 * </ol>
 * </p>
 *
 * @see TipoBateria#BDI_II
 * @see ServicioBateriaClinica
 * @see VentanaAgregarBDI
 * 
 * @version 1.0
 */
@Component
public class ControlAgregarBDI {
    
    // Dependencias inyectadas
    private final VentanaAgregarBDI ventanaAgregarBDI;
    private final ServicioBateriaClinica servicioBateriaClinica;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param ventanaAgregarBDI vista que muestra la interfaz del BDI-II
     * @param servicioBateriaClinica servicio que guarda las respuestas en el sistema
     */
    @Autowired
    public ControlAgregarBDI(
            VentanaAgregarBDI ventanaAgregarBDI,
            ServicioBateriaClinica servicioBateriaClinica) {
        this.ventanaAgregarBDI = ventanaAgregarBDI;
        this.servicioBateriaClinica = servicioBateriaClinica;
    }

    /**
     * Asocia este controlador con la vista después de la construcción del bean.
     */
    @PostConstruct
    public void inicializa() {
        ventanaAgregarBDI.setControlAgregarBDI(this);
    }

    /**
     * Inicia la ventana de registro del BDI-II para un paciente específico.
     *
     * @param pacienteID identificador único del paciente
     */
    public void inicia(Long pacienteID) {
        ventanaAgregarBDI.setControlAgregarBDI(this);
        ventanaAgregarBDI.setPacienteID(pacienteID);
        ventanaAgregarBDI.muestra();
    }

    /**
     * Guarda los resultados del BDI-II en el sistema.
     *
     * @param pacienteID identificador del paciente
     * @param respuestas lista de respuestas seleccionadas
     * @param comentarios observaciones adicionales
     */
    public void guardarBDI(Long pacienteID, List<Integer> respuestas, String comentarios) {
        servicioBateriaClinica.registrarBateria(
            pacienteID,
            TipoBateria.BDI_II,
            respuestas,
            comentarios
        );
    } 

}
