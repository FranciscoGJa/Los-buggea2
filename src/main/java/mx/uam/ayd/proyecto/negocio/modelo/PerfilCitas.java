// PerfilCitas.java
package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
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
    
    @OneToMany(mappedBy = "perfilCitas")
    private List<Cita> citas = new ArrayList<>();
    
    // Constructor vacío
    public PerfilCitas() {
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // Constructor con parámetros
    public PerfilCitas(String nombreCompleto, int edad, String sexo, String direccion, String ocupacion) {
        this();
        this.nombreCompleto = nombreCompleto;
        this.edad = edad;
        this.sexo = sexo;
        this.direccion = direccion;
        this.ocupacion = ocupacion;
    }
    
    // Getters y Setters
    public Long getIdPerfil() {
        return idPerfil;
    }
    
    public void setIdPerfil(Long idPerfil) {
        this.idPerfil = idPerfil;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public int getEdad() {
        return edad;
    }
    
    public void setEdad(int edad) {
        this.edad = edad;
    }
    
    public String getSexo() {
        return sexo;
    }
    
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getOcupacion() {
        return ocupacion;
    }
    
    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public List<Cita> getCitas() {
        return citas;
    }
    
    public void setCitas(List<Cita> citas) {
        this.citas = citas;
    }
    
    public void agregarCita(Cita cita) {
        this.citas.add(cita);
        cita.setPerfilCitas(this);
    }
    
    @Override
    public String toString() {
        return "PerfilCitas{" +
                "idPerfil=" + idPerfil +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", edad=" + edad +
                ", sexo='" + sexo + '\'' +
                ", direccion='" + direccion + '\'' +
                ", ocupacion='" + ocupacion + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PerfilCitas)) return false;
        PerfilCitas that = (PerfilCitas) o;
        return idPerfil != null && idPerfil.equals(that.idPerfil);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}