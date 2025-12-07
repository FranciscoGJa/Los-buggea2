package mx.uam.ayd.proyecto.presentacion.Horario;

import java.util.List;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioCita;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;

@Component
public class ControlHorario {
    private final VentanaHorario ventanaHorario;

    @Autowired
    private ServicioCita servicioCita;
    /**
     * Constructor con inyección de dependencias.
     *
     * @param ventanaHorario vista encargada de mostrar la ventana de pago
     */

    @Autowired
    public ControlHorario(VentanaHorario ventanaHorario) {
        this.ventanaHorario = ventanaHorario;
    }

    /*
     *Iniciamos la visualizacion del horario
     *Ahora incluyendo la carga de citas del dia actual
     */
    

    /**
     * Inicia la aparicion de la ventana del horario
     * 
     */
    public void iniciar() {
        ventanaHorario.setControlHorario(this);
        ventanaHorario.mostrarHorario();
        ventanaHorario.cargarCitasAsync();
    }
    public ServicioCita getServicioCita() {
        return servicioCita;
    }
}
