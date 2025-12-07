package mx.uam.ayd.proyecto.presentacion.agregarCEPER;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import java.util.List;
import mx.uam.ayd.proyecto.negocio.ServicioBateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.BateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.TipoBateria;

@Component
public class ControlAgregarCEPER {

    private final VentanaAgregarCEPER ventanaAgregarCEPER;
    private final ServicioBateriaClinica servicioBateriaClinica;
    private BateriaClinica bateriaEnEdicion;

    @Autowired
    public ControlAgregarCEPER(
        VentanaAgregarCEPER ventanaAgregarCEPER,
        ServicioBateriaClinica servicioBateriaClinica) {
        this.ventanaAgregarCEPER = ventanaAgregarCEPER;
        this.servicioBateriaClinica = servicioBateriaClinica;
    }

    @PostConstruct
    public void inicializa() {
        ventanaAgregarCEPER.setControlAgregarCEPER(this);
    }

    public void inicia(Long pacienteID) {
        this.bateriaEnEdicion = null;
        ventanaAgregarCEPER.setControlAgregarCEPER(this);
        ventanaAgregarCEPER.setPacienteID(pacienteID);
        ventanaAgregarCEPER.muestra();
    }

    public void iniciaEditar(BateriaClinica bateria) {
        this.bateriaEnEdicion = bateria;
        ventanaAgregarCEPER.setControlAgregarCEPER(this);
        ventanaAgregarCEPER.setPacienteID(bateria.getPaciente().getId());
        ventanaAgregarCEPER.muestra(bateria);
    }

    public void guardarCEPER(Long pacienteID, List<Integer> respuestas, String comentarios) {
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
                TipoBateria.CEPER,
                respuestas,
                comentarios
            );
        }
    }
}
