package mx.uam.ayd.proyecto.presentacion.menu;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.presentacion.listarpacientes.ControlListarPacientes;
import mx.uam.ayd.proyecto.presentacion.agregarPsicologo.ControlAgregarPsicologo;
import mx.uam.ayd.proyecto.presentacion.listarPsicologo.ControlListarPsicologo;
import mx.uam.ayd.proyecto.presentacion.agregarPaciente.ControlAgregarPaciente;
import mx.uam.ayd.proyecto.presentacion.Pago.ControlPagar;
import mx.uam.ayd.proyecto.presentacion.PerfilCitas.VentanaPelfil;

/**
 * Controlador principal del menú de la aplicación.
 */
@Component
public class ControlMenu {

    private final VentanaMenu ventana;
    
    private final ControlListarPacientes controlListarPacientes;
    private final ControlAgregarPaciente controlAgregarPaciente;
    private final ControlAgregarPsicologo controlAgregarPsicologo;
    private final ControlListarPsicologo controlListarPsicologo;
    private final ControlPagar controlPagar;
    private final VentanaPelfil ventanaPelfil;
    
    /**
     * Constructor que inyecta todas las dependencias necesarias
     * @param controlPagar controlador para la funcionalidad de pago de servicios
     */
    @Autowired
    public ControlMenu(
            VentanaMenu ventana,
            ControlListarPacientes controlListarPacientes,
            ControlAgregarPsicologo controlAgregarPsicologo,
            ControlListarPsicologo controlListarPsicologo,
            ControlAgregarPaciente controlAgregarPaciente,
            VentanaPelfil ventanaPelfil,
            ControlPagar controlPagar
            ) 
            {
        this.ventana = ventana;
        this.controlListarPacientes = controlListarPacientes;
        this.controlAgregarPsicologo = controlAgregarPsicologo;
        this.controlListarPsicologo = controlListarPsicologo;
        this.controlAgregarPaciente = controlAgregarPaciente;
        this.controlPagar = controlPagar;
        this.ventanaPelfil = ventanaPelfil;
    }
    
    /**
     * Inicializa la conexión entre este controlador y la ventana de menú.
     */
    @PostConstruct
    public void init() {
        ventana.setControlMenu(this);
    }
    
    /**
     * Inicia la visualización del menú principal.
     */
    public void inicia() {
        ventana.muestra();
    }
    
    /**
     * Abre el flujo para agregar un nuevo paciente.
     */
    public void agregarPaciente() {
        controlAgregarPaciente.inicia();
    }
    
    /**
     * Abre la ventana para listar todos los pacientes registrados.
     */
    public void listarPacientes() {
        controlListarPacientes.inicia();
    }
    /**
     * Abre el flujo para el pago de servicios.
     */
    public void pagoServicio() {
        // Aquí se podría agregar la lógica para iniciar el proceso de pago
        controlPagar.inicia();
    }

    /**
     * Abre el flujo para agregar un nuevo psicólogo.
     */
    public void agregarPsicologo() {
        controlAgregarPsicologo.inicia();
    }
    
    /**
     * Abre la ventana para listar todos los psicólogos registrados.
     */
    public void listarPsicologo() {
        controlListarPsicologo.inicia();
    }

    /**
     * Abre la ventana para consultar perfiles de citas.
     */
    public void consultarPerfilCitas() {
        ventanaPelfil.muestra();
    }
    
    /**
     * Finaliza la ejecución de la aplicación.
     */
    public void salir() {
        System.exit(0);
    }
}