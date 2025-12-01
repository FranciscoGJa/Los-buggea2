package mx.uam.ayd.proyecto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import mx.uam.ayd.proyecto.datos.PsicologoRepository;
import mx.uam.ayd.proyecto.presentacion.principal.ControlPrincipalCentro;


/**
 * 
 * Clase principal que arranca la aplicación 
 * construida usando el principio de 
 * inversión de control
 * Adaptada para usar JavaFX
 * 
 * @author Humberto Cervantes (c) 21 Nov 2022
 */
@SpringBootApplication
public class ProyectoApplication {

    private final ControlPrincipalCentro controlPrincipalCentro;
    
    @Autowired
    public ProyectoApplication(
            ControlPrincipalCentro controlPrincipalCentro,
            PsicologoRepository psicologoRepository) {
        this.controlPrincipalCentro = controlPrincipalCentro;
    }

    /**
     * Método principal
     *
     * @param args argumentos de la línea de comando
     */
    public static void main(String[] args) {
        // Launch JavaFX application
        Application.launch(JavaFXApplication.class, args);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Clase interna para manejar la inicialización de JavaFX
     */
    public static class JavaFXApplication extends Application {
        
        private static ConfigurableApplicationContext applicationContext;
        
        @Override
        public void init() throws Exception {
            // Create Spring application context
            SpringApplicationBuilder builder = new SpringApplicationBuilder(ProyectoApplication.class);
            builder.headless(false);
            applicationContext = builder.run(getParameters().getRaw().toArray(new String[0]));
        }
        
        @Override
        public void start(Stage primaryStage) {
            // Initialize the application on the JavaFX thread
            Platform.runLater(() -> {
                applicationContext.getBean(ProyectoApplication.class).inicia();
            });
        }
        
        @Override
        public void stop() throws Exception {
            applicationContext.close();
            Platform.exit();
        }
    }
    
    /**
     * Metodo que arranca la aplicacion
     * inicializa la bd y arranca los controladores
     */
    public void inicia() {
        
        // Make sure controllers are created on JavaFX thread
        Platform.runLater(() -> {
            // Nuestro proyecto arranca con loggin
            controlPrincipalCentro.inicia();      
            
            // Se comenta el del profe
            // controlPrincipal.inicia();
        });
    }
}
