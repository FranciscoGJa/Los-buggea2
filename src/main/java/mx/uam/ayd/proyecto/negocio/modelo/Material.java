package mx.uam.ayd.proyecto.negocio.modelo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Material {

    private int idMaterial;
    private Integer idPsicologo;
    private StringProperty nombre;
    private StringProperty tipo;
    private StringProperty estado;
    private StringProperty imagen;
    private BooleanProperty seleccionado;

    public Material(int idMaterial, String nombre, String tipo, String estado, Integer idPsicologo, String imagen) {
        this.idMaterial = idMaterial;
        this.idPsicologo = idPsicologo;
        this.nombre = new SimpleStringProperty(nombre);
        this.tipo = new SimpleStringProperty(tipo);
        this.estado = new SimpleStringProperty(estado);
        this.imagen = new SimpleStringProperty(imagen);
        this.seleccionado = new SimpleBooleanProperty(false);
    }

    public int getIdMaterial() { return idMaterial; }
    public String getNombre() { return nombre.get(); }
    public String getTipo() { return tipo.get(); }
    public String getEstado() { return estado.get(); }
    public String getImagen() { return imagen.get(); }
    public Integer getIdPsicologo() { return idPsicologo; }
    public boolean isSeleccionado() { return seleccionado.get(); }

    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public void setTipo(String tipo) { this.tipo.set(tipo); }
    public void setEstado(String estado) { this.estado.set(estado); }
    public void setImagen(String imagen) { this.imagen.set(imagen); }
    public void setIdPsicologo(Integer idPsicologo) { this.idPsicologo = idPsicologo; }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado.set(seleccionado); }

    public StringProperty nombreProperty() { return nombre; }
    public StringProperty tipoProperty() { return tipo; }
    public StringProperty estadoProperty() { return estado; }
    public StringProperty imagenProperty() { return imagen; }
    public BooleanProperty seleccionadoProperty() { return seleccionado; }
}
