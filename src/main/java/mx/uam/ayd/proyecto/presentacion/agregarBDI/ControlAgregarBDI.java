package mx.uam.ayd.proyecto.presentacion.agregarBDI;

import org.springframework.stereotype.Component;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioBateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.BateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.TipoBateria;

/**
 * Controlador encargado de la lógica para la gestión de la batería clínica BDI-II
 * (Inventario de Depresión de Beck).
 */
@Component
public class ControlAgregarBDI {

    private final VentanaAgregarBDI ventanaAgregarBDI;
    private final ServicioBateriaClinica servicioBateriaClinica;
    private BateriaClinica bateriaEnEdicion;

    @Autowired
    public ControlAgregarBDI(
            VentanaAgregarBDI ventanaAgregarBDI,
            ServicioBateriaClinica servicioBateriaClinica) {
        this.ventanaAgregarBDI = ventanaAgregarBDI;
        this.servicioBateriaClinica = servicioBateriaClinica;
    }

    @PostConstruct
    public void inicializa() {
        ventanaAgregarBDI.setControlAgregarBDI(this);
    }

    public void inicia(Long pacienteID) {
        this.bateriaEnEdicion = null;
        ventanaAgregarBDI.setControlAgregarBDI(this);
        ventanaAgregarBDI.setPacienteID(pacienteID);
        ventanaAgregarBDI.muestra();
    }

    public void iniciaEditar(BateriaClinica bateria) {
        this.bateriaEnEdicion = bateria;
        ventanaAgregarBDI.setControlAgregarBDI(this);
        ventanaAgregarBDI.setPacienteID(bateria.getPaciente().getId());
        ventanaAgregarBDI.muestra(bateria);
    }

    public void guardarBDI(Long pacienteID, List<Integer> respuestas, String comentarios) {
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
                    TipoBateria.BDI_II,
                    respuestas,
                    comentarios
            );
        }
    }
}
