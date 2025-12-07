package mx.uam.ayd.proyecto.presentacion.agregarBAI;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import java.util.List;

import mx.uam.ayd.proyecto.negocio.ServicioBateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.BateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.TipoBateria;

/**
 * Controlador encargado de manejar la lógica del Inventario de Ansiedad de Beck (BAI).
 * Permite registrar o editar baterías clínicas asociadas a un paciente.
 */
@Component
public class ControlAgregarBAI {

    private final VentanaAgregarBAI ventanaAgregarBAI;
    private final ServicioBateriaClinica servicioBateriaClinica;
    private BateriaClinica bateriaEnEdicion;

    @Autowired
    public ControlAgregarBAI(VentanaAgregarBAI ventanaAgregarBAI, ServicioBateriaClinica servicioBateriaClinica) {
        this.ventanaAgregarBAI = ventanaAgregarBAI;
        this.servicioBateriaClinica = servicioBateriaClinica;
    }

    @PostConstruct
    public void inicializa() {
        ventanaAgregarBAI.setControlAgregarBAI(this);
    }

    /**
     * Muestra la ventana para capturar una nueva batería BAI.
     */
    public void inicia(Long pacienteID) {
        this.bateriaEnEdicion = null;
        ventanaAgregarBAI.setControlAgregarBAI(this);
        ventanaAgregarBAI.setPacienteID(pacienteID);
        ventanaAgregarBAI.muestra();
    }

    /**
     * Muestra la ventana con una batería existente para edición.
     */
    public void iniciaEditar(BateriaClinica bateria) {
        this.bateriaEnEdicion = bateria;
        ventanaAgregarBAI.setControlAgregarBAI(this);
        ventanaAgregarBAI.setPacienteID(bateria.getPaciente().getId());
        ventanaAgregarBAI.muestra(bateria);
    }

    /**
     * Guarda o actualiza la batería BAI.
     */
    public void guardarBAI(Long pacienteID, List<Integer> respuestas, String comentarios) {
        if (bateriaEnEdicion != null) {
            bateriaEnEdicion.setRespuesta1(respuestas.get(0));
            bateriaEnEdicion.setRespuesta2(respuestas.get(1));
            bateriaEnEdicion.setRespuesta3(respuestas.get(2));
            bateriaEnEdicion.setRespuesta4(respuestas.get(3));
            bateriaEnEdicion.setRespuesta5(respuestas.get(4));

            int total = respuestas.stream().mapToInt(Integer::intValue).sum();
            bateriaEnEdicion.setCalificacion(total);

            servicioBateriaClinica.actualizarBateria(bateriaEnEdicion);
        } else {
            servicioBateriaClinica.registrarBateria(
                pacienteID,
                TipoBateria.BAI,
                respuestas,
                comentarios
            );
        }
    }
}
