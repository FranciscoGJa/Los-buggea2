package mx.uam.ayd.proyecto.negocio;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional;

import mx.uam.ayd.proyecto.datos.BateriaClinicaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.BateriaClinica;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import mx.uam.ayd.proyecto.datos.PacienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.negocio.modelo.TipoBateria;

/**
 * Servicio encargado de la gestión de baterías clínicas.
 *
 * <p>Incluye la lógica de negocio para registrar nuevas baterías, guardar comentarios
 * y obtener detalles de una batería clínica específica. Se encarga de coordinar
 * la interacción entre los repositorios de {@link BateriaClinica} y {@link Paciente}.</p>
 *
 * <p>Utiliza anotaciones de Spring para ser manejado como un servicio y
 * {@link jakarta.transaction.Transactional} para asegurar la integridad en operaciones críticas.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
@Service
public class ServicioBateriaClinica {
    
    private final BateriaClinicaRepository bateriaClinicaRepository;
    private final PacienteRepository pacienteRepository;

    /**
     * Constructor que inyecta las dependencias requeridas.
     *
     * @param bateriaClinicaRepository repositorio para la entidad {@link BateriaClinica}.
     * @param pacienteRepository repositorio para la entidad {@link Paciente}.
     */
    @Autowired
    public ServicioBateriaClinica(BateriaClinicaRepository bateriaClinicaRepository,
                                    PacienteRepository pacienteRepository) {
                                        this.bateriaClinicaRepository=bateriaClinicaRepository;
                                        this.pacienteRepository=pacienteRepository;
    }

    /**
     * Registra una batería clínica asociada a un paciente, calculando la calificación
     * en base a las respuestas proporcionadas.
     *
     * <p>Si ya existe una batería para el paciente y tipo especificados, se actualiza.
     * De lo contrario, se crea una nueva.</p>
     *
     * @param pacienteID el ID del paciente al que se asociará la batería; no debe ser {@code null}.
     * @param tipo el tipo de batería que se va a registrar (por ejemplo, CEPER, BAI, BDI-II); no debe ser {@code null}.
     * @param respuestas lista con las 5 respuestas dadas por el usuario; no debe ser {@code null} y debe tener tamaño 5.
     * @param comentarios comentarios adicionales realizados por el psicólogo sobre el puntaje obtenido.
     * @return la batería clínica registrada o actualizada.
     * @throws IllegalArgumentException si el paciente no existe o los parámetros son inválidos.
     */
    @Transactional
    public BateriaClinica registrarBateria(Long pacienteID,
                                            TipoBateria tipo,
                                            List<Integer> respuestas,
                                            String comentarios) {
        if(pacienteID==null) throw new IllegalArgumentException("pacienteID obligatorio");
        if(tipo==null) throw new IllegalArgumentException("Tipo es obligatorio");
        if(respuestas==null || respuestas.size() !=5) throw new IllegalArgumentException("Se requieren las 5 respuestas");

        Paciente paciente = pacienteRepository.findById(pacienteID).orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado "+ pacienteID));

        // Revisamos si no hay una bateria existente
        BateriaClinica bateria = bateriaClinicaRepository.findByPacienteAndTipoDeBateria(paciente, tipo).orElseGet(BateriaClinica::new);

        if (bateria.getId() == 0){
            bateria.setPaciente(paciente);
            bateria.setTipoDeBateria(tipo);
        }
        
        bateria.setFechaAplicacion(new Date());
        bateria.setRespuesta1(respuestas.get(0));
        bateria.setRespuesta2(respuestas.get(1));
        bateria.setRespuesta3(respuestas.get(2));
        bateria.setRespuesta4(respuestas.get(3));
        bateria.setRespuesta5(respuestas.get(4));

        //Regla del negocio, la calificacion es la suma
        int calificacion = respuestas.stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        bateria.setCalificacion(calificacion);

        bateria.setComentarios(comentarios != null ? comentarios.trim() : " ");

        return bateriaClinicaRepository.save(bateria);
    }

    /**
     * Guarda o actualiza los comentarios asociados a una batería clínica específica.
     *
     * @param bateria la batería a actualizar; no debe ser {@code null}.
     * @param comentarios los nuevos comentarios.
     * @return la batería clínica actualizada y persistida.
     * @throws IllegalArgumentException si la batería es {@code null}.
     */
    public BateriaClinica guardarComentarios(BateriaClinica bateria, String comentarios) {
        if (bateria == null) {
            throw new IllegalArgumentException("La batería no puede ser nula.");
        }
        bateria.setComentarios(comentarios);
        return bateriaClinicaRepository.save(bateria);
    }
    
    /**
     * Obtiene una descripción en texto con los detalles de una batería clínica.
     *
     * <p>Actualmente, la información incluye el tipo de batería, la fecha de aplicación
     * y el puntaje obtenido. En el futuro, este método podría expandirse para
     * mostrar preguntas y respuestas.</p>
     *
     * @param bateria la batería seleccionada; puede ser {@code null}.
     * @return una cadena con los detalles de la batería o un mensaje si no se ha seleccionado ninguna.
     */
    public String obtenerDetallesBateria(BateriaClinica bateria) {
        if (bateria == null) {
            return "No se ha seleccionado ninguna batería.";
        }
        
        // La lógica de negocio es construir el string de detalles
        StringBuilder detalles = new StringBuilder();
        detalles.append("Detalles para la batería: ").append(bateria.getTipoDeBateria()).append("\n");
        detalles.append("Fecha de Aplicación: ").append(bateria.getFechaAplicacion()).append("\n");
        detalles.append("Puntaje: ").append(bateria.getCalificacion()).append("\n\n");
        detalles.append("(Funcionalidad para ver preguntas y respuestas en desarrollo)");
        
        return detalles.toString();
    }
}