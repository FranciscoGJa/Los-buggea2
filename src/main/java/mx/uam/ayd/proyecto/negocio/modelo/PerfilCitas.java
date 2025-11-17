package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * Modelo que representa el perfil de citas de un paciente.
 * Contiene información personal y una lista de citas asociadas.
 * Puede estar vinculado a un paciente registrado o ser un perfil independiente.
 * También puede tener un psicólogo asignado, pero esto es opcional.
 */

@Entity
@Data
public class PerfilCitas {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPerfil;
    
    @Column(nullable = false)
    private String nombreCompleto;
    
    @Column(nullable = false)
    private int edad;
    
    @Column(nullable = false)
    private String sexo;
    
    @Column(nullable = false)
    private String direccion;
    
    @Column(nullable = false)
    private String ocupacion;
    
    private String telefono;
    
    private String email;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    // Relación con Paciente (opcional - para pacientes ya registrados)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;
    
    // Relación con Psicólogo asignado (OPCIONAL - nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psicologo_id", nullable = true)  // Cambiado a nullable = true
    private Psicologo psicologo;
    
    @OneToMany(mappedBy = "perfilCitas", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas = new ArrayList<>();
    
    // Constructor vacío
    public PerfilCitas() {
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // Constructor para paciente existente
    public PerfilCitas(Paciente paciente, String direccion, String ocupacion) {
        this();
        this.paciente = paciente;
        this.nombreCompleto = paciente.getNombre();
        this.edad = paciente.getEdad();
        this.sexo = "Por definir";
        this.direccion = direccion;
        this.ocupacion = ocupacion;
        this.telefono = paciente.getTelefono();
        this.email = paciente.getCorreo();
        
        // Agregar este perfil al paciente
        if (paciente != null) {
            paciente.agregarPerfilCitas(this);
        }
    }

    // Constructor para paciente existente (con psicólogo opcional)
    public PerfilCitas(Paciente paciente, Psicologo psicologo, String direccion, String ocupacion) {
        this();
        this.paciente = paciente;
        this.psicologo = psicologo;  // Puede ser null
        this.nombreCompleto = paciente.getNombre();
        this.edad = paciente.getEdad();
        this.sexo = "Por definir";
        this.direccion = direccion;
        this.ocupacion = ocupacion;
        this.telefono = paciente.getTelefono();
        this.email = paciente.getCorreo();
    
        // Agregar este perfil al paciente
        if (paciente != null) {
         paciente.agregarPerfilCitas(this);
        }
    }
    
    // Constructor para paciente nuevo (sin registro previo) - CON psicólogo OPCIONAL
    public PerfilCitas(String nombreCompleto, int edad, String sexo, String direccion, 
                      String ocupacion, String telefono, String email, Psicologo psicologo) {
        this();
        this.nombreCompleto = nombreCompleto;
        this.edad = edad;
        this.sexo = sexo;
        this.direccion = direccion;
        this.ocupacion = ocupacion;
        this.telefono = telefono;
        this.email = email;
        this.psicologo = psicologo; // Puede ser null
    }
    
    // NUEVO CONSTRUCTOR: Para paciente nuevo SIN psicólogo
    public PerfilCitas(String nombreCompleto, int edad, String sexo, String direccion, 
                      String ocupacion, String telefono, String email) {
        this();
        this.nombreCompleto = nombreCompleto;
        this.edad = edad;
        this.sexo = sexo;
        this.direccion = direccion;
        this.ocupacion = ocupacion;
        this.telefono = telefono;
        this.email = email;
        // psicologo se deja como null
    }
    
    public void agregarCita(Cita cita) {
        this.citas.add(cita);
        cita.setPerfilCitas(this);
    }
    
    public void removerCita(Cita cita) {
        this.citas.remove(cita);
        cita.setPerfilCitas(null);
    }
}