package mx.uam.ayd.proyecto.presentacion.agregarBAI;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;

import java.util.List;

import mx.uam.ayd.proyecto.negocio.ServicioBateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.TipoBateria;

/**
 * Controlador para la ventana de registro de la batería BAI (Inventario de Ansiedad de Beck).
 *
 * <p>Gestiona la interacción entre la vista {@link VentanaAgregarBAI} y el servicio
 * de negocio {@link ServicioBateriaClinica} para registrar los resultados
 * de la batería BAI asociados a un paciente.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
@Component
public class ControlAgregarBAI {

    private final VentanaAgregarBAI ventanaAgregarBAI;
    private final ServicioBateriaClinica servicioBateriaClinica;

    /**
     * Constructor que inyecta la vista y el servicio de negocio.
     *
     * @param ventanaAgregarBAI la ventana encargada de la interfaz de usuario para agregar la batería BAI.
     * @param servicioBateriaClinica el servicio que gestiona la lógica de negocio de baterías clínicas.
     */ 
    @Autowired
    public ControlAgregarBAI(
            VentanaAgregarBAI ventanaAgregarBAI,
            ServicioBateriaClinica servicioBateriaClinica) {
        this.ventanaAgregarBAI = ventanaAgregarBAI;
        this.servicioBateriaClinica = servicioBateriaClinica;
    }

    /**
     * Asocia este controlador con la vista tras la construcción del bean.
     * <p>Se ejecuta automáticamente después de la inyección de dependencias.</p>
     */
    @PostConstruct
    public void inicializa() {
        ventanaAgregarBAI.setControlAgregarBAI(this);
    }

    /**
     * Inicia la ventana de registro BAI para un paciente específico.
     *
     * @param pacienteID identificador único del paciente.
     */
    public void inicia(Long pacienteID) {
        ventanaAgregarBAI.setControlAgregarBAI(this);
        ventanaAgregarBAI.setPacienteID(pacienteID);
        ventanaAgregarBAI.muestra();
    }

    /**
     * Guarda los resultados de la batería BAI en el sistema.
     *
     * @param pacienteID identificador del paciente.
     * @param respuestas lista de respuestas seleccionadas.
     * @param comentarios observaciones adicionales sobre la batería.
     */
    public void guardarBAI(Long pacienteID, List<Integer> respuestas, String comentarios) {
       servicioBateriaClinica.registrarBateria(
        pacienteID,
        TipoBateria.BAI,
        respuestas,
        comentarios
        );
    }
}
