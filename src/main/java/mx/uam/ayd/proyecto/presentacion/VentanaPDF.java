package mx.uam.ayd.proyecto.presentacion;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la ventana de gestión de PDFs.
 */
@Component
public class VentanaPDF implements Initializable {

    @FXML private ComboBox<String> comboTipo;
    @FXML private TableView<ArchivoPDF> tablaPDFs;
    @FXML private TableColumn<ArchivoPDF, String> colNombre;
    @FXML private TableColumn<ArchivoPDF, String> colTipo;
    @FXML private WebView webView;
    @FXML private Label labelInfo;
    @FXML private Label pageLabel;
    @FXML private ChoiceBox<String> zoomBox;

    private ObservableList<ArchivoPDF> listaPDFs;
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarComponentes();
        cargarListaPDFs();
    }

    /**
     * Método para mostrar la ventana (requerido por Spring)
     */
    public void muestra() {
        try {
            if (stage == null) {
                // Cargar el FXML y crear la ventana
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaRespiracion.fxml"));
                loader.setController(this);
                Parent root = loader.load();
                
                stage = new Stage();
                stage.setTitle("Ejercicios de Respiración - Técnicas de Relajación");
                stage.setScene(new Scene(root));
                stage.setMinWidth(1000);
                stage.setMinHeight(700);
                
                // Configurar cierre de ventana
                stage.setOnCloseRequest(event -> {
                    stage = null;
                });
            }
            
            stage.show();
            stage.toFront(); // Traer al frente si ya está abierta
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la ventana de ejercicios de respiración");
        }
    }

    private void configurarComponentes() {
        // Configurar ComboBox de tipos
        comboTipo.setItems(FXCollections.observableArrayList(
                "Todos", "Respiración", "Relajación", "Meditación"
        ));
        comboTipo.getSelectionModel().select("Todos");
        comboTipo.setOnAction(event -> filtrarPorTipo());

        // Configurar Zoom
        zoomBox.setItems(FXCollections.observableArrayList(
                "75%", "100%", "125%", "150%"
        ));
        zoomBox.setValue("100%");
        zoomBox.setOnAction(event -> aplicarZoom());

        // Configurar Tabla
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        tablaPDFs.setPlaceholder(new Label("No hay PDFs disponibles"));
        tablaPDFs.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        mostrarPDF(newSel);
                    }
                }
        );
    }

    private void cargarListaPDFs() {
        listaPDFs = FXCollections.observableArrayList(
            new ArchivoPDF("Respiración Profunda", "Respiración", 
                         "/pdf/respiracion.pdf", "Técnica de respiración profunda para reducir el estrés"),
            new ArchivoPDF("Relajación Muscular", "Relajación", 
                         "/pdf/relajacion.pdf", "Relajación muscular progresiva"),
            new ArchivoPDF("Meditación Guiada", "Meditación", 
                         "/pdf/meditacion.pdf", "Meditación mindfulness para principiantes")
        );

        tablaPDFs.setItems(listaPDFs);
    }

    private void filtrarPorTipo() {
        String tipo = comboTipo.getValue();
        if (tipo.equals("Todos")) {
            tablaPDFs.setItems(listaPDFs);
        } else {
            ObservableList<ArchivoPDF> filtrados = listaPDFs.filtered(
                pdf -> pdf.getTipo().equals(tipo)
            );
            tablaPDFs.setItems(filtrados);
        }
    }

    private void mostrarPDF(ArchivoPDF archivoPDF) {
    try {
        URL pdfUrl = getClass().getResource(archivoPDF.getArchivo());
        URL viewerUrl = getClass().getResource("/pdfjs/web/viewer.html");

        if (pdfUrl == null || viewerUrl == null) {
            mostrarPDFNoDisponible(archivoPDF);
            return;
        }

        String finalUrl = viewerUrl.toExternalForm() + "?file=" + pdfUrl.toExternalForm();
        webView.getEngine().load(finalUrl);

        mostrarInfo(archivoPDF.getDescripcion());
        pageLabel.setText("Documento: " + archivoPDF.getNombre());

    } catch (Exception e) {
        e.printStackTrace();
        mostrarPDFNoDisponible(archivoPDF);
    }
}


    private void mostrarPDFNoDisponible(ArchivoPDF archivoPDF) {
        String htmlContent = "<html><body style='font-family: Arial; padding: 20px; text-align: center;'>" +
                "<h2 style='color: #e74c3c;'>PDF No Disponible</h2>" +
                "<p><strong>" + archivoPDF.getNombre() + "</strong></p>" +
                "<p>El archivo PDF no está disponible en este momento.</p>" +
                "<p><em>Ruta: " + archivoPDF.getArchivo() + "</em></p>" +
                "<hr><p><strong>Descripción:</strong> " + archivoPDF.getDescripcion() + "</p>" +
                "</body></html>";
        webView.getEngine().loadContent(htmlContent);
        mostrarInfo(archivoPDF.getDescripcion());
        pageLabel.setText("PDF no disponible");
    }

    @FXML
    private void aplicarZoom() {
        String zoom = zoomBox.getValue();
        switch (zoom) {
            case "75%": webView.setZoom(0.75); break;
            case "100%": webView.setZoom(1.0); break;
            case "125%": webView.setZoom(1.25); break;
            case "150%": webView.setZoom(1.5); break;
        }
    }

    @FXML
    private void onPrev() {
        // Navegación simple en el historial del WebView
        webView.getEngine().executeScript("window.history.back()");
    }

    @FXML
    private void onNext() {
        // Navegación simple en el historial del WebView
        webView.getEngine().executeScript("window.history.forward()");
    }

    @FXML
    private void onLimpiarSeleccion() {
        tablaPDFs.getSelectionModel().clearSelection();
        webView.getEngine().loadContent("<html><body style='font-family: Arial; padding: 20px; text-align: center;'><h2>Seleccione un PDF de la lista</h2></body></html>");
        labelInfo.setText("Seleccione una técnica de la lista");
        pageLabel.setText("Ningún documento seleccionado");
    }

    @FXML
    private void onRecargar() {
        ArchivoPDF seleccionado = tablaPDFs.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mostrarPDF(seleccionado);
        }
    }

    @FXML
    private void onAcercaDe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText(null);
        alert.setContentText("Sistema de Técnicas de Relajación\n\nVersión 1.0\n\nIncluye técnicas de respiración, relajación y meditación.");
        alert.showAndWait();
    }

    private void mostrarInfo(String info) {
        labelInfo.setText(info);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    public static class ArchivoPDF {
        private final String nombre;
        private final String tipo;
        private final String archivo;
        private final String descripcion;

        public ArchivoPDF(String nombre, String tipo, String archivo, String descripcion) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.archivo = archivo;
            this.descripcion = descripcion;
        }

        public String getNombre() { return nombre; }
        public String getTipo() { return tipo; }
        public String getArchivo() { return archivo; }
        public String getDescripcion() { return descripcion; }
    }
}