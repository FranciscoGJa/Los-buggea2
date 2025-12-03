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
    private VentanaPelfil ventanaPelfil; // Para "Consultar Perfiles de Citas"

    @Autowired
    private VentanaAgendaPsicologo ventanaAgendaPsicologo; // Nueva ventana para la agenda

    @Autowired
    private ControlHorario ventanaHorario; // Control para el horario

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

    // --- Funciones permitidas ---

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
        try {
            Parent vista = ventanaPelfil.getVista();
            ventana.actualizaBreadcrumb(List.of("Inicio", "Perfiles", "Consultar"));
            ventana.cargarVista(vista);
        } catch (Exception e) {
            e.printStackTrace();
            ventanaPelfil.muestra(); // fallback a la forma anterior
        }
    }

    /**
     * Abre la ventana del horario del psicólogo.
     */
    public void horario() {
        ventanaHorario.iniciar();
    }

    /**
     * Muestra ejercicios de respiración en una vista incrustada.
     */
    public void ejerciciosRespiracion() {
        try {
            Parent vista = ventanaPDF.getVista();
            ventana.actualizaBreadcrumb(List.of("Inicio", "Ejercicios", "Respiración"));
            ventana.cargarVista(vista);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: mostrar en ventana independiente
            ventanaPDF.muestra();
        }
    }

    /**
     * Muestra la vista de gestión de material didáctico.
     */
    public void mostrarMaterialDidactico() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-GestionRecursos.fxml"));
            loader.setControllerFactory(context::getBean);

            // Carga la UI como un Parent para insertarlo en el contentArea de la misma ventana
            Parent root = loader.load();

            // Si el controlador necesita contexto (ej. id del psicólogo), se lo pasamos
            Object controller = loader.getController();
            if (controller != null && psicologoLogueado != null) {
                try {
                    controller.getClass().getMethod("setIdPsicologo", int.class)
                              .invoke(controller, psicologoLogueado.getId());
                } catch (NoSuchMethodException nsme) {
                    // El controlador no requiere el id, ignorar
                }
            }

            // Actualizar breadcrumb y cargar la vista en el panel central
            ventana.actualizaBreadcrumb(List.of("Inicio", "Material Didáctico", "Gestión"));
            ventana.cargarVista(root);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // Captura reflectiva u otros errores al invocar setIdPsicologo
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
     * @return el psicólogo logueado
     */
    public Psicologo getPsicologo() {
        return psicologoLogueado;
    }

    // --- Salir ---
    public void salir() {
        System.exit(0);
    }
}
