package mx.uam.ayd.proyecto.presentacion.menuPsicologo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;
import mx.uam.ayd.proyecto.presentacion.listarpacientes.ControlListarPacientes;
import mx.uam.ayd.proyecto.presentacion.agregarPaciente.ControlAgregarPaciente;
import mx.uam.ayd.proyecto.presentacion.PerfilCitas.VentanaPelfil;
import mx.uam.ayd.proyecto.presentacion.agendaPsicologo.VentanaAgendaPsicologo;

import java.util.List;

@Component
public class ControlMenuPsicologo {

    @Autowired
    private VentanaMenuPsicologo ventana;

    @Autowired
    private ControlListarPacientes controlListarPacientes;

    @Autowired
    private ControlAgregarPaciente controlAgregarPaciente;

    @Autowired
    private VentanaPelfil ventanaPelfil;

    @Autowired
    private VentanaAgendaPsicologo ventanaAgendaPsicologo;

    private Psicologo psicologoLogueado;

    @PostConstruct
    public void init() {
        ventana.setControl(this);
    }

    public void inicia(Psicologo psicologo) {
        this.psicologoLogueado = psicologo;
        System.out.println("Iniciando menú para el psicólogo: " + psicologo.getNombre());
        ventana.muestra();
    }

    public void agregarPaciente() {
        ventana.actualizaBreadcrumb(List.of("Inicio", "Pacientes", "Agregar Paciente"));
        controlAgregarPaciente.inicia();
        ventana.cargarVista(controlAgregarPaciente.getVista());
    }

    public void listarPacientes() {
        ventana.actualizaBreadcrumb(List.of("Inicio", "Pacientes", "Listar Pacientes"));
        controlListarPacientes.inicia();
        ventana.cargarVista(controlListarPacientes.getVista());
    }

    public void consultarPerfilCitas() {
        ventanaPelfil.muestra();
    }

    public void abrirAgendaPsicologo() {
        ventanaAgendaPsicologo.setControlMenuPsicologo(this);
        ventanaAgendaPsicologo.muestra();
    }

    public Psicologo getPsicologo() {
    return psicologoLogueado;
    }


    public void salir() {
        System.exit(0);
    }
}
