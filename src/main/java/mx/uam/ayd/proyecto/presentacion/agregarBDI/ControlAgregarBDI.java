package mx.uam.ayd.proyecto.presentacion.agregarBDI;

import org.springframework.stereotype.Component;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioBateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.BateriaClinica;
import mx.uam.ayd.proyecto.negocio.modelo.TipoBateria;

/**
 * Controlador encargado de la lógica para la gestión de la batería clínica BDI-II
 * (Inventario de Depresión de Beck).
 * * <p>Esta clase coordina la interacción entre la vista {@link VentanaAgregarBDI} y 
 * la capa de negocio {@link ServicioBateriaClinica}. Permite tanto registrar nuevos
 * resultados como editar baterías ya existentes.</p>
 * * @author Tech Solutions
 * @version 1.0
 */
@Component
public class ControlAgregarBDI {
    
    /** Referencia a la ventana de captura de la prueba BDI-II */
    private final VentanaAgregarBDI ventanaAgregarBDI;
    
    /** Servicio de negocio para persistir los resultados clínicos */
    private final ServicioBateriaClinica servicioBateriaClinica;
    
    /** * Almacena la referencia a la batería que se está editando actualmente.
     * Si es null, indica que se está creando una nueva batería.
     */
    private BateriaClinica bateriaEnEdicion;

    /**
     * Constructor para la inyección de dependencias.
     * * @param ventanaAgregarBDI La vista asociada a este controlador.
     * @param servicioBateriaClinica El servicio para operaciones de negocio sobre baterías.
     */
    @Autowired
    public ControlAgregarBDI(
            VentanaAgregarBDI ventanaAgregarBDI,
            ServicioBateriaClinica servicioBateriaClinica) {
        this.ventanaAgregarBDI = ventanaAgregarBDI;
        this.servicioBateriaClinica = servicioBateriaClinica;
    }

    /**
     * Método ejecutado tras la construcción del bean.
     * Establece la comunicación bidireccional asignando este controlador a la vista.
     */
    @PostConstruct
    public void inicializa() {
        ventanaAgregarBDI.setControlAgregarBDI(this);
    }

    /**
     * Inicia el flujo para registrar una <b>nueva</b> batería BDI-II.
     * Prepara la ventana en modo de creación (limpia).
     * * @param pacienteID El identificador del paciente al que se le aplicará la prueba.
     */
    public void inicia(Long pacienteID) {
        this.bateriaEnEdicion = null; // Asegura que no estamos en modo edición
        ventanaAgregarBDI.setControlAgregarBDI(this);
        ventanaAgregarBDI.setPacienteID(pacienteID);
        ventanaAgregarBDI.muestra();
    }

    /**
     * Inicia el flujo para <b>editar</b> o visualizar una batería BDI-II existente.
     * Carga los datos previos en la ventana.
     * * @param bateria La entidad {@link BateriaClinica} con los datos existentes a cargar.
     */
    public void iniciaEditar(BateriaClinica bateria) {
        this.bateriaEnEdicion = bateria; // Establece el contexto de edición
        ventanaAgregarBDI.setControlAgregarBDI(this);
        // Se recupera el ID del paciente desde la batería existente
        ventanaAgregarBDI.setPacienteID(bateria.getPaciente().getId());
        ventanaAgregarBDI.muestra(bateria);
    }

    /**
     * Guarda los resultados de la prueba en la base de datos.
     * * <p>Este método distingue entre dos casos de uso:
     * <ul>
     * <li><b>Edición:</b> Si existe una {@code bateriaEnEdicion}, actualiza sus respuestas,
     * recalcula el puntaje total y persiste los cambios.</li>
     * <li><b>Creación:</b> Si no, registra una nueva batería asociada al paciente y al tipo {@link TipoBateria#BDI_II}.</li>
     * </ul>
     * </p>
     * * @param pacienteID El identificador del paciente.
     * @param respuestas Lista de enteros con las respuestas seleccionadas por el usuario (índices 0-4).
     * @param comentarios Comentarios u observaciones adicionales sobre la prueba.
     */
    public void guardarBDI(Long pacienteID, List<Integer> respuestas, String comentarios) {
        if (bateriaEnEdicion != null) {
            // Lógica de actualización de batería existente
            bateriaEnEdicion.setRespuesta1(respuestas.get(0));
            bateriaEnEdicion.setRespuesta2(respuestas.get(1));
            bateriaEnEdicion.setRespuesta3(respuestas.get(2));
            bateriaEnEdicion.setRespuesta4(respuestas.get(3));
            bateriaEnEdicion.setRespuesta5(respuestas.get(4));
            
            // Recálculo de la calificación total
            int total = respuestas.stream().mapToInt(Integer::intValue).sum();
            bateriaEnEdicion.setCalificacion(total);
            
            servicioBateriaClinica.actualizarBateria(bateriaEnEdicion);
        } else {
            // Lógica de registro de nueva batería
            servicioBateriaClinica.registrarBateria(pacienteID, TipoBateria.BDI_II, respuestas, comentarios);
        }
    } 
}