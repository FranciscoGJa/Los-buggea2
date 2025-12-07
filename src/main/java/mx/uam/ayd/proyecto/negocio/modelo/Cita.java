package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/*
 * Modelo que representa una cita entre un paciente y un psicólogo.
 * Contiene detalles sobre la fecha, hora, estado de confirmación y notas adicionales.
 */

@Entity
@Getter
@Setter
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;   // ID interno de la entidad

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

    // ----- Constructor por defecto -----
    public Cita() {
        this.estadoCita = TipoConfirmacionCita.PENDIENTE;
    }

    // ----- Getters/Setters explícitos para la hora (opcional, Lombok también los genera) -----
    public LocalTime getHoraCita() {
        return horaCita;
    }

    public void setHoraCita(LocalTime horaCita) {
        this.horaCita = horaCita;
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

    // ===================== ID de la cita (usado en controladores/servicios) =====================

    /**
     * Conveniencia para usar getIdCita() en vez de getId()
     */
    public Integer getIdCita() {
        return id;
    }

    public void setIdCita(Integer idCita) {
        this.id = idCita;
    }

    // ===================== equals / hashCode seguros para JPA =====================

    /**
     * Importante: equals SOLO usa el id para evitar LazyInitializationException
     * cuando TableView u otras estructuras comparan objetos y Hibernate
     * intenta inicializar proxies de relaciones LAZY.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cita)) return false;
        Cita other = (Cita) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        // Recomendado para entidades JPA: constante hasta que tenga ID
        return 31;
    }
}
