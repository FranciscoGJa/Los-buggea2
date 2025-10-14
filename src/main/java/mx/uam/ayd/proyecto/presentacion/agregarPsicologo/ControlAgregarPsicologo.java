package mx.uam.ayd.proyecto.presentacion.agregarPsicologo;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioPsicologo;
import mx.uam.ayd.proyecto.negocio.modelo.TipoEspecialidad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controlador para la ventana de registro de psicólogos.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Inicializar la vista {@link VentanaAgregarPsicologo} y asociarla con este controlador.</li>
 *   <li>Mostrar la ventana para agregar psicólogos.</li>
 *   <li>Delegar al servicio {@link ServicioPsicologo} la lógica de alta.</li>
 *   <li>Mostrar mensajes de éxito o error en la vista.</li>
 *   <li>Cerrar la ventana al finalizar la operación.</li>
 * </ul>
 * </p>
 *
 * <p>Flujo típico:
 * <ol>
 *   <li>Invocar {@link #inicia()} para abrir la ventana.</li>
 *   <li>Llamar a {@link #agregarPsicologo(String, String, String, TipoEspecialidad)} desde la vista.</li>
 *   <li>El controlador valida la operación con el servicio y muestra el resultado.</li>
 *   <li>Se cierra la ventana con {@link #termina()}.</li>
 * </ol>
 * </p>
 *
 * @author 
 * @version 1.0
 */
@Component
public class ControlAgregarPsicologo {

    private final VentanaAgregarPsicologo ventanaAgregarPsicologo;
    private final ServicioPsicologo servicioPsicologo;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param ventanaAgregarPsicologo vista de registro de psicólogos
     * @param servicioPsicologo servicio de negocio para psicólogos
     */
    @Autowired
    public ControlAgregarPsicologo(VentanaAgregarPsicologo ventanaAgregarPsicologo,
                                    ServicioPsicologo servicioPsicologo){
        this.ventanaAgregarPsicologo = ventanaAgregarPsicologo;
        this.servicioPsicologo = servicioPsicologo;
    }

    /**
     * Inicializa la vista asociando este controlador.
     * <p>Se ejecuta automáticamente después de la construcción del bean.</p>
     */
    @PostConstruct
    public void init(){
        ventanaAgregarPsicologo.setControlAgregarPsicologo(this);
    }

    /**
     * Inicia la historia de usuario mostrando la ventana.
     */
    public void inicia(){
        ventanaAgregarPsicologo.muestra();
    }

    /**
     * Agrega un nuevo psicólogo utilizando el servicio de negocio.
     *
     * @param nombre nombre del psicólogo
     * @param correo correo electrónico
     * @param telefono número de teléfono
     * @param especialidad especialidad del psicólogo
     */
    public void agregarPsicologo(String nombre, String correo, String telefono, TipoEspecialidad especialidad) {
        try {
            servicioPsicologo.agregarPsicologo(nombre, correo, telefono, especialidad);
            ventanaAgregarPsicologo.mostrarExito("Psicologo agregado exitosamente");
        } catch(Exception ex) {
            ventanaAgregarPsicologo.mostrarError("Error al agregar Psicologo: "+ex.getMessage());
        }

        termina();
    }

    /**
     * Finaliza la operación ocultando la ventana.
     */
    public void termina() {
        ventanaAgregarPsicologo.setVisible(false);
    }
}