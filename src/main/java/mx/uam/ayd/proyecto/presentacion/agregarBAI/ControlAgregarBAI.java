package mx.uam.ayd.proyecto.presentacion.agregarBAI;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import java.util.List;
import mx.uam.ayd.proyecto.negocio.ServicioBateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.BateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.TipoBateria;

/**
 * Controlador encargado de gestionar el flujo de la batería clínica BAI 
 * (Inventario de Ansiedad de Beck).
 * * <p>Esta clase actúa como intermediario entre la vista {@link VentanaAgregarBAI} 
 * y la lógica de negocio {@link ServicioBateriaClinica}. Es responsable de iniciar
 * la captura de una nueva prueba o la edición de una existente, así como de 
 * coordinar el guardado de los resultados.</p>
 * * @author Tech Solutions
 * @version 1.0
 */
@Component
public class ControlAgregarBAI {
    
    /** Vista asociada a este controlador para la captura de datos del BAI */
    private final VentanaAgregarBAI ventanaAgregarBAI;
    
    /** Servicio de negocio para persistir y actualizar las baterías clínicas */
    private final ServicioBateriaClinica servicioBateriaClinica;
    
    /** * Referencia a la batería que se está editando actualmente.
     * Si es {@code null}, indica que se está en modo de creación de una nueva batería.
     */
    private BateriaClinica bateriaEnEdicion;

    /**
<<<<<<< HEAD
     * Constructor que inyecta las dependencias necesarias.
     * * @param ventanaAgregarBAI La vista gráfica para el BAI.
     * @param servicioBateriaClinica El servicio para operaciones con la base de datos.
     */
=======
     * Constructor que inyecta la vista y el servicio de negocio.
     *
     * @param ventanaAgregarBAI la ventana encargada de la interfaz de usuario para agregar la batería BAI.
     * @param servicioBateriaClinica el servicio que gestiona la lógica de negocio de baterías clínicas.
     */ 
>>>>>>> HU-19-EjerciciosDeRespiracion
    @Autowired
    public ControlAgregarBAI(
            VentanaAgregarBAI ventanaAgregarBAI,
            ServicioBateriaClinica servicioBateriaClinica) {
        this.ventanaAgregarBAI = ventanaAgregarBAI;
        this.servicioBateriaClinica = servicioBateriaClinica;
    }

    /**
     * Método de inicialización ejecutado tras la inyección de dependencias.
     * Establece la referencia de este controlador en la vista para permitir la comunicación bidireccional.
     */
    @PostConstruct
    public void inicializa() {
        ventanaAgregarBAI.setControlAgregarBAI(this);
    }

    /**
     * Inicia el flujo para registrar una <b>nueva</b> batería BAI.
     * Prepara la vista en estado limpio y establece el ID del paciente asociado.
     * * @param pacienteID El identificador del paciente al que se le aplicará la prueba.
     */
    public void inicia(Long pacienteID) {
        this.bateriaEnEdicion = null; // Reinicia el estado para asegurar que es una creación
        ventanaAgregarBAI.setControlAgregarBAI(this);
        ventanaAgregarBAI.setPacienteID(pacienteID);
        ventanaAgregarBAI.muestra();
    }

    /**
     * Inicia el flujo para <b>editar</b> o visualizar una batería BAI existente.
     * Carga los datos de la batería proporcionada en la vista.
     * * @param bateria La entidad {@link BateriaClinica} con los datos previos a cargar.
     */
    public void iniciaEditar(BateriaClinica bateria) {
        this.bateriaEnEdicion = bateria; // Establece el objeto que se va a modificar
        ventanaAgregarBAI.setControlAgregarBAI(this);
        // Se obtiene el ID del paciente desde la relación en la batería
        ventanaAgregarBAI.setPacienteID(bateria.getPaciente().getId());
        ventanaAgregarBAI.muestra(bateria);
    }

    /**
     * Guarda los resultados de la prueba BAI en el sistema.
     * * <p>Dependiendo del estado interno ({@code bateriaEnEdicion}), este método decide si:
     * <ul>
     * <li><b>Actualiza:</b> Si se está editando, modifica las respuestas y recalcula el puntaje total de la batería existente.</li>
     * <li><b>Registra:</b> Si es nueva, crea una nueva entrada en la base de datos asociada al paciente.</li>
     * </ul>
     * </p>
     * * @param pacienteID El identificador del paciente.
     * @param respuestas Lista de enteros con las respuestas a las preguntas 0-4.
     * @param comentarios Comentarios u observaciones adicionales ingresados.
     */
    public void guardarBAI(Long pacienteID, List<Integer> respuestas, String comentarios) {
        if (bateriaEnEdicion != null) {
            // --- Lógica de Edición ---
            bateriaEnEdicion.setRespuesta1(respuestas.get(0));
            bateriaEnEdicion.setRespuesta2(respuestas.get(1));
            bateriaEnEdicion.setRespuesta3(respuestas.get(2));
            bateriaEnEdicion.setRespuesta4(respuestas.get(3));
            bateriaEnEdicion.setRespuesta5(respuestas.get(4));
            
            // Recalcular la calificación sumando las nuevas respuestas
            int total = respuestas.stream().mapToInt(Integer::intValue).sum();
            bateriaEnEdicion.setCalificacion(total);
            
            // Persistir los cambios
            servicioBateriaClinica.actualizarBateria(bateriaEnEdicion);
        } else {
            // --- Lógica de Creación ---
            servicioBateriaClinica.registrarBateria(pacienteID, TipoBateria.BAI, respuestas, comentarios);
        }
    }
}