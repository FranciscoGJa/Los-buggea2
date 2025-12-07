package mx.uam.ayd.proyecto.presentacion.menuPsicologo;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;
import mx.uam.ayd.proyecto.presentacion.listarpacientes.ControlListarPacientes;
import mx.uam.ayd.proyecto.presentacion.agregarPaciente.ControlAgregarPaciente;
import mx.uam.ayd.proyecto.presentacion.PerfilCitas.VentanaPelfil;
import mx.uam.ayd.proyecto.presentacion.agendaPsicologo.VentanaAgendaPsicologo;
import mx.uam.ayd.proyecto.presentacion.Horario.ControlHorario;
import mx.uam.ayd.proyecto.presentacion.VentanaPDF;

import java.io.IOException;
import java.util.List;

/**
 * Controlador para el menú específico del Psicólogo.
 * Muestra una vista restringida sin opciones de administrador.
 */
@Component
public class ControlMenuPsicologo {

    @Autowired
    private VentanaMenuPsicologo ventana;

    @Autowired
    private VentanaPDF ventanaPDF; // Ventana para ejercicios de respiración

    @Autowired
    private ControlListarPacientes controlListarPacientes;

    @Autowired
    private ControlAgregarPaciente controlAgregarPaciente;

    @Autowired
    private VentanaPelfil ventanaPelfil; // Ventana de "Consultar Perfiles de Citas"

    @Autowired
    private VentanaAgendaPsicologo ventanaAgendaPsicologo; // Ventana para la agenda

    @Autowired
    private ControlHorario controlHorario; // Control para el horario

    private Psicologo psicologoLogueado;

    @Autowired
    private ApplicationContext context; // Contexto de Spring

    @PostConstruct
    public void init() {
        ventana.setControl(this);
    }

    /**
     * Inicia el menú del psicólogo.
     * @param psicologo El psicólogo que ha iniciado sesión
     */
    public void inicia(Psicologo psicologo) {
        this.psicologoLogueado = psicologo;
        System.out.println("Iniciando menú para el psicólogo: " + psicologo.getNombre());
        ventana.muestra();
    }

    // -------------------------------------------------------------------------
    // FUNCIONES DISPONIBLES EN EL MENÚ DEL PSICÓLOGO
    // -------------------------------------------------------------------------

    /**
     * Abre la vista para agregar un nuevo paciente.
     */
    public void agregarPaciente() {
        ventana.actualizaBreadcrumb(List.of("Inicio", "Pacientes", "Agregar Paciente"));
        controlAgregarPaciente.inicia();
        ventana.cargarVista(controlAgregarPaciente.getVista());
    }

    /**
     * Muestra la lista de pacientes del psicólogo.
     */
    public void listarPacientes() {
        ventana.actualizaBreadcrumb(List.of("Inicio", "Pacientes", "Listar Pacientes"));
        controlListarPacientes.inicia();
        ventana.cargarVista(controlListarPacientes.getVista());
    }

    /**
     * Abre la ventana de consulta de perfiles de citas.
     * Ahora usa la misma lógica que el menú de administrador (ventana independiente).
     */
    public void consultarPerfilCitas() {
        try {
            // Abrimos la misma ventana que el administrador
            // para evitar errores en la carga de la tabla o estilos.
            ventanaPelfil.muestra();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la ventana del horario del psicólogo.
     */
    public void horario() {
        controlHorario.setControlMenu(this); // aquí SI es ControlMenuPsicologo
        controlHorario.iniciar();
    }

    /**
     * Muestra ejercicios de respiración (PDF) dentro del panel central o en ventana aparte.
     */
    public void ejerciciosRespiracion() {
        try {
            Parent vista = ventanaPDF.getVista();
            ventana.actualizaBreadcrumb(List.of("Inicio", "Ejercicios", "Respiración"));
            ventana.cargarVista(vista);
        } catch (Exception e) {
            e.printStackTrace();
            ventanaPDF.muestra(); // Fallback: abrir en nueva ventana
        }
    }

    /**
     * Muestra la vista de gestión de material didáctico.
     */
    public void mostrarMaterialDidactico() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-GestionRecursos.fxml"));
            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            // Pasar el ID del psicólogo si el controlador lo requiere
            Object controller = loader.getController();
            if (controller != null && psicologoLogueado != null) {
                try {
                    controller.getClass()
                            .getMethod("setIdPsicologo", int.class)
                            .invoke(controller, psicologoLogueado.getId());
                } catch (NoSuchMethodException nsme) {
                    // El controlador no requiere el ID, se ignora
                }
            }

            ventana.actualizaBreadcrumb(List.of("Inicio", "Material Didáctico", "Gestión"));
            ventana.cargarVista(root);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la agenda del psicólogo.
     */
    public void abrirAgendaPsicologo() {
        ventanaAgendaPsicologo.setControlMenuPsicologo(this);
        ventanaAgendaPsicologo.muestra();
    }

    /**
     * Devuelve el psicólogo actualmente autenticado.
     */
    public Psicologo getPsicologo() {
        return psicologoLogueado;
    }

    /**
     * Cierra la aplicación (salida completa).
     */
    public void salir() {
        System.exit(0);
    }
}
