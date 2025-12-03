package mx.uam.ayd.proyecto.presentacion.agregarCEPER;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;

import java.util.List;

import mx.uam.ayd.proyecto.negocio.ServicioBateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.BateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.TipoBateria;

/**
 * Controlador para la ventana de registro de la batería CEPER.
 */
@Component
public class ControlAgregarCEPER {
    
    private final VentanaAgregarCEPER ventanaAgregarCEPER;
    private final ServicioBateriaClinica servicioBateriaClinica;
    
    private BateriaClinica bateriaEnEdicion; // Variable para controlar si estamos editando

    @Autowired
    public ControlAgregarCEPER(
        VentanaAgregarCEPER ventanaAgregarCEPER,
        ServicioBateriaClinica servicioBateriaClinica) {
            this.ventanaAgregarCEPER=ventanaAgregarCEPER;
            this.servicioBateriaClinica=servicioBateriaClinica;
        }
    
    @PostConstruct
    public void inicializa(){
        ventanaAgregarCEPER.setControlAgregarCEPER(this);
    }

    /**
     * Inicia la ventana para NUEVO registro.
     */
    public void inicia(Long pacienteID){
        this.bateriaEnEdicion = null; // Modo creación
        ventanaAgregarCEPER.setControlAgregarCEPER(this);
        ventanaAgregarCEPER.setPacienteID(pacienteID);
        ventanaAgregarCEPER.muestra();
    }

    /**
     * Inicia la ventana para EDITAR una batería existente.
     */
    public void iniciaEditar(BateriaClinica bateria) {
        this.bateriaEnEdicion = bateria; // Modo edición
        ventanaAgregarCEPER.setControlAgregarCEPER(this);
        ventanaAgregarCEPER.setPacienteID(bateria.getPaciente().getId());
        ventanaAgregarCEPER.muestra(bateria); // Sobrecarga que recibe datos
    }

    /**
     * Guarda los resultados (crea nuevo o actualiza existente).
     */
    public void guardarCEPER(Long pacienteID, List<Integer> respuestas, String comentarios) {
        if (bateriaEnEdicion != null) {
            // Actualizar datos de la batería existente
            bateriaEnEdicion.setRespuesta1(respuestas.get(0));
            bateriaEnEdicion.setRespuesta2(respuestas.get(1));
            bateriaEnEdicion.setRespuesta3(respuestas.get(2));
            bateriaEnEdicion.setRespuesta4(respuestas.get(3));
            bateriaEnEdicion.setRespuesta5(respuestas.get(4));
            
            // Recalcular calificación
            int total = respuestas.stream().mapToInt(Integer::intValue).sum();
            bateriaEnEdicion.setCalificacion(total);
            
            servicioBateriaClinica.actualizarBateria(bateriaEnEdicion);
        } else {
            // Crear nueva
            servicioBateriaClinica.registrarBateria(
                pacienteID,
                TipoBateria.CEPER,
                respuestas,
                comentarios
            );
        }
    }    
}