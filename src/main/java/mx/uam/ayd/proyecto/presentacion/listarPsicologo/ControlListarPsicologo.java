package mx.uam.ayd.proyecto.presentacion.listarPsicologo;

import jakarta.annotation.PostConstruct;
import javafx.scene.Node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import mx.uam.ayd.proyecto.negocio.ServicioPsicologo;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;

/**
 * Controlador para la funcionalidad de listar psicólogos.
 * 
 * Esta clase actúa como intermediario entre la vista {@link VentanaListarPsicologo} 
 * y el servicio de negocio {@link ServicioPsicologo}.
 * Sus responsabilidades principales incluyen:
 * <ul>
 *   <li>Inicializar la conexión con la ventana correspondiente.</li>
 *   <li>Solicitar al servicio de negocio la lista de psicólogos registrados.</li>
 *   <li>Enviar los datos obtenidos a la vista para su visualización.</li>
 *   <li>Cerrar la ventana cuando sea requerido.</li>
 * </ul>
 * 
 * El método {@link #init()} se ejecuta automáticamente tras la construcción del bean
 * para establecer la referencia de este controlador en la vista.
 * 
 * @author TechSolutions
 */
@Component
public class ControlListarPsicologo {
    private ControlListarPsicologo controlListarPsicologo;

    private final VentanaListarPsicologo ventanaListarPsicologo;

    @Autowired
    private ServicioPsicologo servicioPsicologo;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param ventanaListarPsicologo vista encargada de mostrar el listado de psicólogos
     */
    @Autowired
    public ControlListarPsicologo(VentanaListarPsicologo ventanaListarPsicologo){
        this.ventanaListarPsicologo = ventanaListarPsicologo;
    }

    /**
     * Inicializa la conexión entre esta clase de control y la ventana asociada.
     * Este método se ejecuta automáticamente después de la construcción del bean.
     */
    @PostConstruct
    public void init(){
        ventanaListarPsicologo.setControlListarPsicologo(this);
    }

    /**
     * Inicia el flujo de listado de psicólogos.
     * <p>Recupera la lista de psicólogos desde el servicio de negocio y
     * la envía a la vista para su visualización.</p>
     */
    public void inicia(){
        List<Psicologo> psicologos = servicioPsicologo.listarPsicologos();
        ventanaListarPsicologo.muestra(psicologos);
    }
    public Node getVista() {
    return ventanaListarPsicologo.getVista();
}

    /**
     * Finaliza el flujo actual ocultando la ventana de listado de psicólogos.
     */
    public void termina() {
        ventanaListarPsicologo.setVisible(false);
    }
}
