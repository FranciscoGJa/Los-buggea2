package mx.uam.ayd.proyecto.presentacion.CrearCita;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;

/*
 * Esta clase representa la ventana para crear una nueva cita.
 * Permite al usuario ingresar los detalles de la cita y
 * mostrar la interfaz correspondiente.
 * Utiliza JavaFX para la interfaz gráfica y Spring para la gestión de dependencias.
 * Contiene métodos para mostrar y cerrar la ventana.
 */

@Component
public class VentanaNuevaCita {
    
    private Stage stage;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    private ControladorNuevaCita controlador;
    
    public void mostrar(PerfilCitas perfil) {
        try {
            if (stage == null) {
                stage = new Stage();
                stage.setTitle("Nueva Cita - " + perfil.getNombreCompleto());
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-nueva-cita.fxml"));
                controlador = applicationContext.getBean(ControladorNuevaCita.class);
                loader.setController(controlador);
                
                Scene scene = new Scene(loader.load(), 900, 800);
                stage.setScene(scene);
                
                stage.setOnCloseRequest(event -> {
                    event.consume();
                    cerrar();
                });
            }
            
            // Cargar datos del perfil
            controlador.cargarPerfil(perfil);
            
            stage.show();
            stage.toFront();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void cerrar() {
        if (stage != null) {
            stage.close();
        }
    }
}