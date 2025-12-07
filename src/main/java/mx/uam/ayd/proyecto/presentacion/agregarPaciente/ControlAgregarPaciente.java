package mx.uam.ayd.proyecto.presentacion.agregarPaciente;

// Notaciones
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import javafx.scene.Node;

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
 * Flujo:
 *  - El menú llama a ControlAgregarPaciente.inicia()
 *  - Se muestra la pantalla de alta con campos vacíos y baterías deshabilitadas
 *  - Al presionar "Agregar paciente" se crea el paciente, se abre historial clínico
 *    y se habilitan las baterías BAI / BDI / CEPER para ese paciente.
 */
@Component
public class ControlAgregarPaciente {

    /** ID del paciente actualmente en edición / para llenar baterías */
    private Long pacienteID;

    // Dependencias inyectadas
    private final VentanaAgregarPaciente ventanaAgregarPaciente;
    private final ServicioPaciente servicioPaciente;
    private final VentanaAgregarBAI ventanaAgregarBAI;
    private final VentanaAgregarBDI ventanaAgregarBDI;
    private final VentanaAgregarCEPER ventanaAgregarCEPER;
    private final ControlContestarHistorialClinico controlContestarHistorialClinico;

    @Autowired
    public ControlAgregarPaciente(
            VentanaAgregarPaciente ventanaAgregarPaciente,
            ServicioPaciente servicioPaciente,
            VentanaAgregarBAI ventanaAgregarBAI,
            VentanaAgregarBDI ventanaAgregarBDI,
            VentanaAgregarCEPER ventanaAgregarCEPER,
            ControlContestarHistorialClinico controlContestarHistorialClinico
    ) {
        this.ventanaAgregarPaciente = ventanaAgregarPaciente;
        this.servicioPaciente = servicioPaciente;
        this.ventanaAgregarBAI = ventanaAgregarBAI;
        this.ventanaAgregarBDI = ventanaAgregarBDI;
        this.ventanaAgregarCEPER = ventanaAgregarCEPER;
        this.controlContestarHistorialClinico = controlContestarHistorialClinico;
    }

    /**
     * Conecta control ↔ ventana después de que Spring crea los beans.
     */
    @PostConstruct
    public void inicializa() {
        ventanaAgregarPaciente.setControlAgregarPaciente(this);
    }

    /**
     * Inicia la historia de usuario.
     * Se llama desde el menú.
     *
     * - Limpia campos
     * - Deshabilita baterías
     * - Resetea pacienteID
     */
    public void inicia() {
        pacienteID = null;
        ventanaAgregarPaciente.inicia();               // limpia campos
        ventanaAgregarPaciente.deshabilitarBaterias(); // deshabilita BAI/BDI/CEPER
    }

    /**
     * Devuelve el nodo raíz para insertarlo en el StackPane del menú.
     */
    public Node getVista() {
        return ventanaAgregarPaciente.getVista();
    }

    /**
     * Agrega un paciente utilizando el servicio de pacientes.
     */
    public void agregarPaciente(String nombre, String correo, String telefono, int edad) {
        try {
            Paciente paciente = servicioPaciente.agregarPaciente(nombre, correo, telefono, edad);
            pacienteID = paciente.getId();

            ventanaAgregarPaciente.muestraDialogoConMensaje("Paciente agregado exitosamente");

            // Habilitamos baterías para este paciente
            ventanaAgregarPaciente.habilitarBaterias();

            // Lanzamos el flujo de historial clínico
            contestarHistorialClinico(paciente);

        } catch (Exception ex) {
            ventanaAgregarPaciente.muestraDialogoConMensaje(
                    "Error al agregar usuario:\n\n" + ex.getMessage()
            );
        }
    }

    /**
     * Abre la ventana para capturar la batería BAI del paciente actual.
     */
    public void agregarBAI() {
        if (pacienteID == null) {
            ventanaAgregarPaciente.muestraDialogoConMensaje(
                    "Primero registra al paciente antes de llenar la BAI."
            );
            return;
        }
        ventanaAgregarBAI.setPacienteID(pacienteID);
        ventanaAgregarBAI.muestra();
    }

    /**
     * Abre la ventana para capturar la batería BDI-II del paciente actual.
     */
    public void agregarBDI() {
        if (pacienteID == null) {
            ventanaAgregarPaciente.muestraDialogoConMensaje(
                    "Primero registra al paciente antes de llenar la BDI-II."
            );
            return;
        }
        ventanaAgregarBDI.setPacienteID(pacienteID);
        ventanaAgregarBDI.muestra();
    }

    /**
     * Abre la ventana para capturar la batería CEPER del paciente actual.
     */
    public void agregarCEPER() {
        if (pacienteID == null) {
            ventanaAgregarPaciente.muestraDialogoConMensaje(
                    "Primero registra al paciente antes de llenar la CEPER."
            );
            return;
        }
        ventanaAgregarCEPER.setPacienteID(pacienteID);
        ventanaAgregarCEPER.muestra();
    }

    /**
     * Inicia el flujo para contestar el historial clínico del paciente.
     */
    public void contestarHistorialClinico(Paciente paciente) {
        controlContestarHistorialClinico.inicia(paciente);
    }
}
