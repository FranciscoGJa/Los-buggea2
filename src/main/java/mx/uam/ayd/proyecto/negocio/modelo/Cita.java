package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

/*
 * Modelo que representa una cita entre un paciente y un psicólogo.
 * Contiene detalles sobre la fecha, hora, estado de confirmación y notas adicionales.
 */

@Entity
@Data
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate fechaCita;
    private LocalTime horaCita;

    @Enumerated(EnumType.STRING)
    private TipoConfirmacionCita estadoCita;
    
    @Column(length = 1000)
    private String detallesAdicionalesPsicologo;
    
    @Column(length = 1000)
    private String detallesAdicionalesPaciente;
    
    @Column(length = 2000)
    private String notaPostSesion;
    
    @Column(length = 500)
    private String motivoCancelacion;

    // Relación con PerfilCitas (principal)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_citas_id", nullable = false)
    private PerfilCitas perfilCitas;

    // Relación con Psicólogo que atiende la cita
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psicologo_id", nullable = false)
    private Psicologo psicologo;

    // Relación opcional con Paciente (para compatibilidad)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    // Constructor que establece estado por defecto
    public Cita() {
        this.estadoCita = TipoConfirmacionCita.PENDIENTE;
    }

    /**
     * Método para establecer el perfil de citas y sincronizar el paciente
     */
    public void setPerfilCitas(PerfilCitas perfilCitas) {
        this.perfilCitas = perfilCitas;
        if (perfilCitas != null && perfilCitas.getPaciente() != null) {
            this.paciente = perfilCitas.getPaciente();
        }
    }
}