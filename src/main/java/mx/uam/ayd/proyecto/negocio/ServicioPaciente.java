package mx.uam.ayd.proyecto.negocio;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import mx.uam.ayd.proyecto.datos.PacienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.negocio.modelo.Psicologo;

/**
 * Servicio que gestiona la lógica de negocio relacionada con los pacientes.
 *
 * <p>Permite registrar nuevos pacientes, recuperar la lista de pacientes registrados
 * y asignar psicólogos a pacientes existentes.</p>
 *
 * <p>Utiliza {@link PacienteRepository} para acceder y persistir la información
 * en la base de datos.</p>
 *
 * @author Tech Solutions
 * @version 1.0
 */
@Service
public class ServicioPaciente {

    private static final Logger log = LoggerFactory.getLogger(ServicioPaciente.class);

    @Autowired
    private PacienteRepository pacienteRepository;
    
    /**
     * Registra un nuevo paciente en el sistema, validando que los datos sean correctos
     * y que el correo electrónico no esté previamente registrado.
     *
     * @param nombre el nombre del paciente; no debe ser nulo ni vacío.
     * @param correo el correo electrónico del paciente; no debe ser nulo ni vacío.
     * @param telefono el número de teléfono del paciente; no debe ser nulo ni vacío.
     * @param edad la edad del paciente.
     * @return el paciente registrado.
     * @throws IllegalArgumentException si alguno de los parámetros es inválido o si el correo ya está registrado.
     */
    public Paciente agregarPaciente(String nombre, String correo, String telefono, int edad) {
        if(nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacio");
        }
        if(correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo no puede ser nulo o vacio");
        }
        if(telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El telefono no puede ser nulo o vacio");
        }

        Paciente paciente = pacienteRepository.findByCorreo(correo);
        if (paciente != null){
            throw new IllegalArgumentException("Este correo ya ha sido registrado en el centro");
        }

        log.info("Agregando paciente nombre: "+nombre+" correo: "+correo);

        paciente = new Paciente();
        paciente.setNombre(nombre);
        paciente.setCorreo(correo);
        paciente.setTelefono(telefono);
        paciente.setEdad(edad);

        pacienteRepository.save(paciente);

        return paciente;
    }

    /**
     * Recupera todos los pacientes registrados en el sistema.
     *
     * @return una lista con todos los pacientes; si no hay pacientes registrados, la lista estará vacía.
     */
    public List<Paciente> recuperarTodosLosPacientes() {
        List<Paciente> pacientes = new ArrayList<>();
        pacienteRepository.findAll().forEach(pacientes::add);
        return pacientes;
    }

    /**
     * Asigna un psicólogo a un paciente existente.
     *
     * @param paciente el paciente al que se le asignará el psicólogo.
     * @param psicologo el psicólogo a asignar.
     */
    @Transactional
    public void asignarPsicologo(Paciente paciente, Psicologo psicologo) {
        paciente.setPsicologo(psicologo);
        pacienteRepository.save(paciente);
    }

}