package mx.uam.ayd.proyecto.presentacion;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Ventana para mostrar la encuesta dentro de la aplicación.
 * Se carga un archivo HTML desde la carpeta 'resources/static'.
 */
public class VentanaEncuesta {

    public void mostrarEncuesta() {
        // Crear una nueva ventana (Stage)
        Stage stage = new Stage();
        stage.setTitle("Encuesta de Satisfacción - Centro Psicológico 🧠");

        // Asignar un ícono personalizado (si tienes uno en /resources/images/logo.png)
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/Logo.png")));
        } catch (Exception e) {
            System.out.println("⚠️ No se encontró el ícono en /images/logo.png, se usará el predeterminado.");
        }

        // Crear el componente WebView (mini navegador)
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        // Cargar el HTML desde la carpeta static
        engine.load("http://localhost:8080/encuesta.html");

        // Crear la escena (ventana visual)
        Scene scene = new Scene(webView, 700, 800);

        // Opcional: quitar los bordes del sistema y hacerlo más limpio
        stage.initStyle(StageStyle.DECORATED); // puedes probar UNDECORATED para un look sin bordes

        // Asignar escena
        stage.setScene(scene);

        // Evitar que el usuario cambie el tamaño
        stage.setResizable(false);

        // Mostrar ventana
        stage.show();
    }
}
