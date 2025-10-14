package mx.uam.ayd.proyecto.presentacion.agregarPaciente;

//Notaciones
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

import mx.uam.ayd.proyecto.presentacion.agregarBAI.VentanaAgregarBAI;
import mx.uam.ayd.proyecto.presentacion.agregarBDI.VentanaAgregarBDI;
import mx.uam.ayd.proyecto.presentacion.agregarCEPER.VentanaAgregarCEPER;
import mx.uam.ayd.proyecto.negocio.ServicioPaciente;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.presentacion.contestarHistorialClinico.ControlContestarHistorialClinico;

/**
 * Controlador para agregar pacientes.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Inicializar la vista {@link VentanaAgregarPaciente} y mostrarla.</li>
 *   <li>Registrar un nuevo paciente mediante {@link ServicioPaciente}.</li>
 *   <li>Abrir las ventanas de captura de baterías (BAI, BDI, CEPER).</li>
 *   <li>Desencadenar el flujo para contestar el historial clínico.</li>
 * </ul>
 * </p>
 *
 * <p>Flujo típico:
 * <ol>
 *   <li>Llamar a {@link #inicia()} para mostrar la ventana de alta.</li>
 *   <li>Usar {@link #agregarPaciente(String, String, String, int)} para registrar.</li>
 *   <li>Opcionalmente abrir BAI/BDI/CEPER con los métodos correspondientes.</li>
 *   <li>Continuar a historial clínico con {@link #contestarHistorialClinico(Paciente)}.</li>
 * </ol>
 * </p>
 *
 * @author TechSolutions
 * @version 1.0
 */
@Component
public class ControlAgregarPaciente {
    private Long pacienteID;

    //Dependencias inyectadas
    private final VentanaAgregarPaciente ventanaAgregarPaciente;
    private final ServicioPaciente servicioPaciente;
    private final VentanaAgregarBAI ventanaAgregarBAI;
    private final VentanaAgregarBDI ventanaAgregarBDI;
    private final VentanaAgregarCEPER ventanaAgregarCEPER;
    private final ControlContestarHistorialClinico controlContestarHistorialClinico;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param ventanaAgregarPaciente vista para alta de pacientes
     * @param servicioPaciente servicio de negocio para pacientes
     * @param ventanaAgregarBAI ventana de captura BAI
     * @param ventanaAgregarBDI ventana de captura BDI-II
     * @param ventanaAgregarCEPER ventana de captura CEPER
     * @param controlContestarHistorialClinico flujo de historial clínico
     */
    @Autowired
    public ControlAgregarPaciente(
    VentanaAgregarPaciente ventanaAgregarPaciente, 
    ServicioPaciente servicioPaciente, 
    VentanaAgregarBAI ventanaAgregarBAI,
    VentanaAgregarBDI ventanaAgregarBDI,
    VentanaAgregarCEPER ventanaAgregarCEPER,
    ControlContestarHistorialClinico controlContestarHistorialClinico) {
        this.ventanaAgregarPaciente = ventanaAgregarPaciente;
        this.servicioPaciente = servicioPaciente;
        this.ventanaAgregarBAI = ventanaAgregarBAI;
        this.ventanaAgregarBDI = ventanaAgregarBDI;
        this.ventanaAgregarCEPER=ventanaAgregarCEPER;
        this.controlContestarHistorialClinico = controlContestarHistorialClinico;
    }

    /**
     * Método que se ejecuta después de la construcción del bean
     * y realiza la conexión bidireccional entre el control y la ventana
     */
    @PostConstruct
    public void inicializa() {
        ventanaAgregarPaciente.setControlAgregarPaciente(this);
    }

    /**
     * Inicia la historia de usuario
     * 
     */
    public void inicia() {
        ventanaAgregarPaciente.muestra();
    }
    
    /**
     * Agrega un paciente utilizando el servicio de pacientes.
     * 
     * @param nombre Nombre del paciente
     * @param correo Correo del paciente
     * @param telefono Teléfono del paciente
     * @param edad Edad del paciente
     */
    public void agregarPaciente(String nombre, String correo, String telefono, int edad) {
        try {
			Paciente paciente = servicioPaciente.agregarPaciente(nombre, correo, telefono, edad);
            pacienteID = paciente.getId();

			ventanaAgregarPaciente.muestraDialogoConMensaje("Paciente agregado exitosamente");
            this.contestarHistorialClinico(paciente);
		} catch(Exception ex) {
			ventanaAgregarPaciente.muestraDialogoConMensaje("Error al agregar usuario:\n \n"+ex.getMessage());
		}
    }

    /**
     * Abre la ventana para capturar la batería BAI del paciente actual.
     */
    public void agregarBAI() {
        ventanaAgregarBAI.setPacienteID(pacienteID);
        ventanaAgregarBAI.muestra();
    }

    /**
     * Abre la ventana para capturar la batería BDI-II del paciente actual.
     */
    public void agregarBDI() {
        ventanaAgregarBDI.setPacienteID(pacienteID);
        ventanaAgregarBDI.muestra();
    }
    
    /**
     * Abre la ventana para capturar la batería CEPER del paciente actual.
     */
    public void agregarCEPER() {
        ventanaAgregarCEPER.setPacienteID(pacienteID);
        ventanaAgregarCEPER.muestra();
    }

    /**
     * Inicia el flujo para contestar el historial clínico del paciente.
     * @param paciente instancia del paciente recién agregado
     */
    public void contestarHistorialClinico(Paciente paciente) {
        controlContestarHistorialClinico.inicia(paciente);
    }
}