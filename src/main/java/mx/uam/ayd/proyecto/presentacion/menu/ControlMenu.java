package mx.uam.ayd.proyecto.presentacion.menu;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.presentacion.listarpacientes.ControlListarPacientes;
import mx.uam.ayd.proyecto.presentacion.agregarPsicologo.ControlAgregarPsicologo;
import mx.uam.ayd.proyecto.presentacion.listarPsicologo.ControlListarPsicologo;
import mx.uam.ayd.proyecto.presentacion.agregarPaciente.ControlAgregarPaciente;
import mx.uam.ayd.proyecto.presentacion.Pago.ControlPagar;

/**
 * Controlador principal del menú de la aplicación.
 * 
 * Esta clase actúa como punto central de navegación entre las distintas funcionalidades
 * del sistema. Recibe las acciones del usuario desde {@link VentanaMenu} y redirige el flujo
 * a los controladores correspondientes.
 * 
 * Sus responsabilidades incluyen:
 * <ul>
 *   <li>Inicializar la conexión con la vista del menú.</li>
 *   <li>Delegar la apertura de cada módulo según la acción seleccionada.</li>
 *   <li>Permitir la salida de la aplicación.</li>
 * </ul>
 * 
 * Es un bean administrado por Spring y se instancia una sola vez durante el ciclo de vida
 * de la aplicación.
 * 
 * @author 
 */
@Component
public class ControlMenu {

    private final VentanaMenu ventana;
    
    private final ControlListarPacientes controlListarPacientes;
    private final ControlAgregarPaciente controlAgregarPaciente;
    private final ControlAgregarPsicologo controlAgregarPsicologo;
    private final ControlListarPsicologo controlListarPsicologo;
    private final ControlPagar controlPagar;
    
    /**
     * Constructor que inyecta todas las dependencias necesarias para gestionar las opciones del menú.
     * 
     * @param ventana vista principal del menú
     * @param controlListarPacientes controlador para la funcionalidad de listar pacientes
     * @param controlAgregarPsicologo controlador para la funcionalidad de agregar psicólogos
     * @param controlListarPsicologo controlador para la funcionalidad de listar psicólogos
     * @param controlAgregarPaciente controlador para la funcionalidad de agregar pacientes
     * @param controlPagar controlador para la funcionalidad de pago de servicios
     */
    @Autowired
    public ControlMenu(
            VentanaMenu ventana,
            ControlListarPacientes controlListarPacientes,
            ControlAgregarPsicologo controlAgregarPsicologo,
            ControlListarPsicologo controlListarPsicologo,
            ControlAgregarPaciente controlAgregarPaciente,
            ControlPagar controlPagar
            ) 
            {
        this.ventana = ventana;
        this.controlListarPacientes = controlListarPacientes;
        this.controlAgregarPsicologo = controlAgregarPsicologo;
        this.controlListarPsicologo = controlListarPsicologo;
        this.controlAgregarPaciente = controlAgregarPaciente;
        this.controlPagar = controlPagar;
    }
    
    /**
     * Inicializa la conexión entre este controlador y la ventana de menú.
     * Se ejecuta automáticamente tras la construcción del bean por Spring.
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
     * Finaliza la ejecución de la aplicación.
     */
    public void salir() {
        System.exit(0);
    }
}