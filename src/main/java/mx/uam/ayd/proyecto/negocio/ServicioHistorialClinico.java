package mx.uam.ayd.proyecto.negocio;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.HistorialClinicoRepository;
import mx.uam.ayd.proyecto.datos.PacienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.HistorialClinico;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;

/**
 * Servicio que gestiona la lógica de negocio relacionada con los historiales clínicos.
 *
 * <p>Proporciona métodos para obtener un historial clínico formateado en texto
 * y para registrar o actualizar el historial de un paciente, asegurando las
 * validaciones necesarias.</p>
 *
 * <p>Utiliza los repositorios {@link HistorialClinicoRepository} y
 * {@link PacienteRepository} para el acceso a datos.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
@Service
public class ServicioHistorialClinico {

    @Autowired
    private HistorialClinicoRepository historialClinicoRepository;
    //Si se van a inyectar por atributo cada dependencia lleva autowired
    @Autowired
    private PacienteRepository pacienteRepository;

    /**
     * Obtiene el historial clínico de un paciente en formato de texto legible.
     *
     * <p>La lógica de formateo reside en el método
     * {@link HistorialClinico#toStringFormateado()} del modelo.</p>
     *
     * @param paciente el paciente cuyo historial se quiere obtener.
     * @return una cadena con el historial formateado, o un mensaje indicando
     *         que no existe historial clínico registrado.
     */
    public String obtenerHistorialFormateado(Paciente paciente) {
        if (paciente.getHistorialClinico() != null) {
            // Llama al método del modelo para generar el texto
            return paciente.getHistorialClinico().toStringFormateado();
        }
        return "No hay un historial clínico registrado para este paciente.";
    }

    /**
     * Crea y guarda un historial clínico para un paciente, validando la información
     * y el consentimiento informado.
     *
     * <p>Las validaciones incluyen:
     * <ul>
     *   <li>Que exista un paciente con el correo proporcionado.</li>
     *   <li>Que el nombre coincida con el registrado para ese correo.</li>
     *   <li>Que el consentimiento informado haya sido aceptado.</li>
     *   <li>Que la fecha del sistema sea válida (no antigua ni futura).</li>
     * </ul>
     * </p>
     *
     * @param nombre el nombre del paciente.
     * @param correo el correo electrónico del paciente.
     * @param motivo el motivo de la consulta.
     * @param consumoDrogas información sobre consumo de drogas.
     * @param descripcion detalles adicionales sobre el consumo de drogas.
     * @param consentimientoAceptado indica si el paciente aceptó el consentimiento informado.
     * @return el historial clínico creado y guardado.
     * @throws IllegalArgumentException si alguna validación falla.
     */
    @Transactional
    public HistorialClinico guardarHistorialClinico(
            String nombre, 
            String correo, 
            String motivo, 
            String consumoDrogas, 
            String descripcion,
            boolean consentimientoAceptado) {

        // 1. VALIDACIONES
            //Buscar paciente dentro de la misma transacción
        Paciente paciente = pacienteRepository.findByCorreo(correo);
        if (paciente == null) {
            throw new IllegalArgumentException("No existe un paciente con el correo: " + correo);
        }
            //Validar que el nombre coincida con el paciente guardado
        if (!paciente.getNombre().equalsIgnoreCase(nombre.trim())) {
            throw new IllegalArgumentException("El nombre no coincide con el registrado para el correo");
        }

            //Validar consentimiento
        if (!consentimientoAceptado) {
            throw new IllegalArgumentException("Acepte el consentimiento informado, por favor.");
        }

            //Validar fecha del sistema (no antigua ni futura)
        Date fechaActual = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaSistemaStr = sdf.format(fechaActual);
        try {
            fechaActual = sdf.parse(fechaSistemaStr); // quitar horas/minutos
        } catch (Exception e) {
            throw new RuntimeException("Error al formatear la fecha del sistema.");
        }
        // Validar que la fecha no sea antigua ni futura
        if (fechaActual.before(new Date(0)) || fechaActual.after(new Date())) {
            throw new IllegalArgumentException("La fecha del sistema no puede ser antigua ni futura.");
        }

        // 2. Crear historial clínico y enlazar ambos lados
        HistorialClinico historialClinico = new HistorialClinico();
        historialClinico.setPaciente(paciente); // @MapsId usará el ID del paciente
        paciente.setHistorialClinico(historialClinico); // Enlace inverso

        // 3. Llenar campos
        historialClinico.setMotivo(motivo);
        historialClinico.setConsumoDrogas(consumoDrogas);
        historialClinico.setDescripcionDrogas(descripcion);
        historialClinico.setFechaElaboracion(new java.util.Date());
        historialClinico.setConsentimientoAceptado(true); // O manejarlo desde parámetro

        // 4. Guardar historial (paciente ya está gestionado en la sesión)
        return historialClinicoRepository.save(historialClinico);
    }
}
