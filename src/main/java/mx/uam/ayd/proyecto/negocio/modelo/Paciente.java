package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList; 
import java.util.List;

@Entity
@Data
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String telefono;
    private String correo;
    private int edad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psicologo_id")
    private Psicologo psicologo;

    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HistorialClinico historialClinico;

    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BateriaClinica> bateriasClinicas;

    // RELACIÓN COMENTADA TEMPORALMENTE - Se usará PerfilCitas en su lugar
    // @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // private List<Cita> citas;

    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PerfilCitas> perfilesCitas;

    /**
     * Constructor por defecto que inicializa las listas
     */
    public Paciente(){
        this.bateriasClinicas = new ArrayList<>();
        // this.citas = new ArrayList<>();
        this.perfilesCitas = new ArrayList<>();
    }

    /**
     * Método para agregar un perfil de citas al paciente
     */
    public void agregarPerfilCitas(PerfilCitas perfilCitas) {
        this.perfilesCitas.add(perfilCitas);
        perfilCitas.setPaciente(this);
    }
}