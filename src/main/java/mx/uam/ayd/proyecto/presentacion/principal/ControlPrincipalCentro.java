package mx.uam.ayd.proyecto.presentacion.principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.datos.PsicologoRepository;
import mx.uam.ayd.proyecto.negocio.ServicioRecordatorios;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;
import mx.uam.ayd.proyecto.presentacion.menu.ControlMenu;
import mx.uam.ayd.proyecto.presentacion.menuPsicologo.ControlMenuPsicologo;

/**
 * Controlador principal del flujo de inicio de sesión (login) del sistema del Centro Psicológico.
 */
@Component
public class ControlPrincipalCentro {

    private final VentanaPrincipalCentro ventanaLogin;
    private final ControlMenu controlMenu;

    @Autowired
    private ServicioRecordatorios servicioRecordatorios;

    @Autowired
    private PsicologoRepository servicioPsicologo;

    @Autowired
    private ControlMenuPsicologo controlMenuPsicologo;

    private Psicologo psicologoLogueado;

    /**
     * Constructor con inyección de dependencias.
     * @param ventanaLogin instancia de VentanaPrincipalCentro
     * @param controlMenu instancia de ControlMenu
     */
    @Autowired
    public ControlPrincipalCentro(VentanaPrincipalCentro ventanaLogin, ControlMenu controlMenu) {
        this.ventanaLogin = ventanaLogin;
        this.controlMenu = controlMenu;
    }

    /**
     * Inicializa la conexión entre este controlador y la ventana de login.
     */
    @PostConstruct
    public void init() {
        ventanaLogin.setControlPrincipalCentro(this);
    }

    /**
     * Inicia el flujo de la ventana de login.
     */
    public void inicia() {
        ventanaLogin.muestra();
        // Si quieres probar el envío de recordatorios, descomenta la siguiente línea:
        // probarRecordatorioManual();
    }

    /**
     * Autentica las credenciales del usuario.
     * @param usuario nombre de usuario
     * @param contrasena contraseña
     */
    public void autenticar(String usuario, String contrasena) {
        // Validación de campos vacíos
        if (usuario == null || usuario.trim().isEmpty()) {
            ventanaLogin.mostrarError("Por favor ingrese un usuario");
            return;
        }

        if (contrasena == null || contrasena.trim().isEmpty()) {
            ventanaLogin.mostrarError("Por favor ingrese una contraseña");
            return;
        }

        try {
            Psicologo psicologo = servicioPsicologo.findByUsuario(usuario);
            if (psicologo == null) {
                ventanaLogin.mostrarError("Usuario no encontrado");
                return;
            }

            if (psicologo.getContrasena().equals(contrasena)) {
                psicologoLogueado = psicologo;
                ventanaLogin.cerrarLogin();

                // Diferenciación de roles
                if ("Admin".equals(psicologo.getUsuario())) {
                    // Si es Admin, muestra el menú principal completo
                    mostrarSistemaPrincipal();
                } else {
                    // Si es cualquier otro psicólogo, muestra el menú restringido
                    controlMenuPsicologo.inicia(psicologo);
                }

            } else {
                ventanaLogin.mostrarError("Contraseña incorrecta");
            }
        } catch (Exception e) {
            ventanaLogin.mostrarError("Error al autenticar: " + e.getMessage());
        }
    }

    /**
     * Muestra el sistema principal después de un login exitoso.
     * Delega la ejecución del menú principal al ControlMenu.
     */
    private void mostrarSistemaPrincipal() {
        controlMenu.inicia();
    }

    /**
     * Devuelve el ID del psicólogo actualmente autenticado.
     * @return el ID del psicólogo logueado
     */
    public int obtenerIdPsicologoLogueado() {
        if (psicologoLogueado != null) {
            return psicologoLogueado.getId();
        } else {
            throw new IllegalStateException("No hay psicólogo logueado.");
        }
    }

    /**
     * Método para pruebas manuales de recordatorios.
     * Muestra las citas de hoy y envía recordatorios de manera asíncrona.
     */
    private void probarRecordatorioManual() {
        // Mostrar las citas de hoy (solo lectura)
        servicioRecordatorios.mostrarCitasHoy();
        // Enviar los recordatorios en segundo plano
        servicioRecordatorios.iniciarRecordatoriosAsync();
    }
}
