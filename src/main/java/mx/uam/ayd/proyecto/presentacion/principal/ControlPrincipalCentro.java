package mx.uam.ayd.proyecto.presentacion.principal;
import mx.uam.ayd.proyecto.datos.PsicologoRepository;
import mx.uam.ayd.proyecto.negocio.ServicioRecordatorios;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;
import mx.uam.ayd.proyecto.presentacion.menu.ControlMenu;
import mx.uam.ayd.proyecto.presentacion.menuPsicologo.ControlMenuPsicologo; 
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controlador principal del flujo de inicio de sesión (login) 
 * del sistema del Centro Psicológico.
 * * @author 
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
    
    /**
     * Constructor con inyección de dependencias.
     *
     * @param ventanaLogin instancia de {@link VentanaPrincipalCentro}
     * @param controlMenu instancia de {@link ControlMenu}
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
     * Inicia el flujo de la ventana de login
     */
    public void inicia() {
        ventanaLogin.muestra();
        probarRecordatorioManual();
    }
    
    /**
     * Autentica las credenciales del usuario
     * * @param usuario nombre de usuario
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
        
        // Autenticación para el centro psicológico
        try{
            Psicologo psicologo = servicioPsicologo.findByUsuario(usuario);
            if(psicologo == null){
                ventanaLogin.mostrarError("Usuario no encontrado");
                return;
            }

            if(psicologo.getContrasena().equals(contrasena)){
                ventanaLogin.cerrarLogin();
                
                // --- INICIO DE MODIFICACIÓN ---
                // Diferenciación de roles
                if (psicologo.getUsuario().equals("Admin")) {
                    // Si es Admin, muestra el menú principal completo
                    controlMenu.inicia();
                } else {
                    // Si es cualquier otro psicólogo, muestra el menú restringido
                    controlMenuPsicologo.inicia(psicologo); 
                }
                // --- FIN DE MODIFICACIÓN (HU-21) ---

            }else{
                ventanaLogin.mostrarError("Contraseña incorrecta");
            }
        }catch(Exception e){
            ventanaLogin.mostrarError("Error al autenticar: "+e.getMessage());
        }
    }
    
    /**
     * Muestra el sistema principal después de un login exitoso.
     * Este método cierra la ventana de login y delega la ejecución 
     * del menú principal al {@link ControlMenu}.
     */
    private void mostrarSistemaPrincipal() {
        ventanaLogin.cerrarLogin();
        controlMenu.inicia();
    }

    private void probarRecordatorioManual() {
        System.out.println(" INICIANDO PRUEBA DE RECORDATORIOS");
    
        // Primero mostrar las citas de hoy (solo lectura)
        System.out.println("\n LISTANDO CITAS DE HOY:");
        servicioRecordatorios.mostrarCitasHoy();
    
        // Luego preguntar si enviar recordatorios
        System.out.println("\n ¿Quieres enviar recordatorios para estas citas?");
        System.out.println("(Esto enviará correos reales)");
    
        // Para prueba automática, puedes comentar/descomentar:
    
        //  DESCOMENTAR PARA ENVIAR CORREOS REALES
        servicioRecordatorios.probarRecordatoriosHoy();
    
        //  O COMENTAR LA LÍNEA ANTERIOR Y USAR ESTA PARA SOLO VER INFO
        //System.out.println("  Envío de correos desactivado temporalmente");
    }
}