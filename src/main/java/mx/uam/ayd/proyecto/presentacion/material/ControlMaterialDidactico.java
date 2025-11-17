package mx.uam.ayd.proyecto.presentacion.material;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import mx.uam.ayd.proyecto.datos.MaterialDAO;
import mx.uam.ayd.proyecto.negocio.modelo.Material;
import mx.uam.ayd.proyecto.presentacion.principal.ControlPrincipalCentro;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;



@Component
public class ControlMaterialDidactico {

    @Autowired
    private MaterialDAO materialDAO;

    // Control de sesión para obtener el id del psicólogo
    @Autowired
    private ControlPrincipalCentro controlPrincipalCentro;
    @FXML private TableColumn<Material, String> colImagen;
    @FXML private TableView<Material> tablaMateriales;
    @FXML private TableColumn<Material, Boolean> colSeleccion;
    @FXML private TableColumn<Material, String> colNombre;
    @FXML private TableColumn<Material, String> colTipo;
    @FXML private TableColumn<Material, String> colEstado;
    @FXML private Button btnApartar;
    @FXML private Button btnLiberar;
    private int idPsicologo;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarBotones();
        cargarTabla();
    }

   private void cargarTabla() {
    List<Material> lista = materialDAO.obtenerMateriales();
    System.out.println("Materiales obtenidos: " + lista.size());

    for (Material m : lista) {
        System.out.println(
            m.getIdMaterial() + " | " +
            m.getNombre() + " | " +
            m.getTipo() + " | " +
            m.getEstado() + " | " +
            m.getImagen()
        );
    }

    tablaMateriales.setItems(FXCollections.observableArrayList(lista));
}


  
private void configurarColumnas() {

    tablaMateriales.setEditable(true); 
    colSeleccion.setEditable(true);
    colImagen.setVisible(false);


    colSeleccion.setCellValueFactory(cellData -> cellData.getValue().seleccionadoProperty());
    colSeleccion.setCellFactory(CheckBoxTableCell.forTableColumn(colSeleccion));

    colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
    colTipo.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
    colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
    colImagen.setCellValueFactory(cellData -> cellData.getValue().imagenProperty());

    colImagen.setCellFactory(col -> new TableCell<Material, String>() {

        private final ImageView imageView = new ImageView();

        @Override
        protected void updateItem(String ruta, boolean empty) {
            super.updateItem(ruta, empty);

            if (empty || ruta == null || ruta.isEmpty()) {
                setGraphic(null);
                return;
            }

            try {
                // Forzar ruta absoluta dentro de resources
                String rutaFinal = ruta.startsWith("/") ? ruta : "/" + ruta;

                System.out.println("Buscando imagen en: " + rutaFinal);
                System.out.println("URL: " + getClass().getResource(rutaFinal));

                Image img = new Image(getClass().getResourceAsStream(rutaFinal));

                imageView.setImage(img);
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);

                setGraphic(imageView);

            } catch (Exception e) {
                System.out.println("❌ Imagen NO encontrada: " + ruta);
                setGraphic(null);
            }
        }
    });
}



    private void configurarBotones() {
        btnApartar.setOnAction(e -> apartarSeleccionados());
        btnLiberar.setOnAction(e -> liberarSeleccionados());
    }

    private void apartarSeleccionados() {
        // Obtener id del psicólogo desde la sesión
        int idPsicologo = controlPrincipalCentro.obtenerIdPsicologoLogueado();

        for (Material m : tablaMateriales.getItems()) {
            if (m.isSeleccionado() && m.getEstado().equalsIgnoreCase("Disponible")) {
                materialDAO.apartarMaterial(m.getIdMaterial(), idPsicologo);
            }
        }
        cargarTabla();
    }

    private void liberarSeleccionados() {
        for (Material m : tablaMateriales.getItems()) {
            if (m.isSeleccionado() && m.getEstado().equalsIgnoreCase("Apartado")) {
                materialDAO.liberarMaterial(m.getIdMaterial());
            }
        }
        cargarTabla();
    }
   

public void setIdPsicologo(int idPsicologo) {
    this.idPsicologo = idPsicologo;
}

}