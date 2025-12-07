package mx.uam.ayd.proyecto.presentacion.Horario;

import java.util.List;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioCita;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;
//import mx.uam.ayd.proyecto.presentacion.menu.ControlMenu;
import mx.uam.ayd.proyecto.negocio.ServicioCalendario;
import mx.uam.ayd.proyecto.presentacion.menuPsicologo.ControlMenuPsicologo;

@Component
public class ControlHorario {
    private final VentanaHorario ventanaHorario;
    private ControlMenuPsicologo controlMenu;

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

    public void setControlMenu(ControlMenuPsicologo controlMenu) {
        this.controlMenu = controlMenu;
    }

    public Psicologo getPsicologo() {
        return controlMenu.getPsicologo();
    }
    

    /**
     * Inicia la aparicion de la ventana del horario
     * 
     */
    public void iniciar() {
         if (controlMenu == null) {
        System.err.println("[ControlHorario] ERROR: controlMenu NULO. Debes asignarlo antes de iniciar().");
        return;
    }
        ventanaHorario.setControlMenu(controlMenu);
        ventanaHorario.setControlHorario(this);
        ventanaHorario.mostrarHorario();
        ventanaHorario.cargarCitasAsync();
    }
    public ServicioCita getServicioCita() {
        System.out.println("Psicólogo actual en sesión: " + controlMenu.getPsicologo().getId());
        return servicioCita;
    }
}
