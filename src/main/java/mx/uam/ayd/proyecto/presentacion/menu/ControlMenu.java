package mx.uam.ayd.proyecto.presentacion.menu;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.presentacion.listarpacientes.ControlListarPacientes;
import mx.uam.ayd.proyecto.presentacion.material.ControlMaterialDidactico;
import mx.uam.ayd.proyecto.presentacion.principal.ControlPrincipalCentro;
import mx.uam.ayd.proyecto.presentacion.agregarPsicologo.ControlAgregarPsicologo;
import mx.uam.ayd.proyecto.presentacion.listarPsicologo.ControlListarPsicologo;
import mx.uam.ayd.proyecto.presentacion.agregarPaciente.ControlAgregarPaciente;
import mx.uam.ayd.proyecto.presentacion.Pago.ControlPagar;
import mx.uam.ayd.proyecto.presentacion.PerfilCitas.VentanaPelfil;

/**
 * Controlador principal del menú de la aplicación.
 */
@Component
public class ControlMenu {

    private final VentanaMenu ventana;
    
    private final ControlListarPacientes controlListarPacientes;
    private final ControlAgregarPaciente controlAgregarPaciente;
    private final ControlAgregarPsicologo controlAgregarPsicologo;
    private final ControlListarPsicologo controlListarPsicologo;
    private final ControlPagar controlPagar;
    private final VentanaPelfil ventanaPelfil;
    @Autowired
    @Lazy
    private ControlPrincipalCentro controlPrincipalCentro;
    @Autowired
    private ApplicationContext context; // Contexto de Spring

    
    @FXML
    private StackPane contentArea;
    /**
     * Constructor que inyecta todas las dependencias necesarias
     * @param controlPagar controlador para la funcionalidad de pago de servicios
     */
    @Autowired
    public ControlMenu(
            VentanaMenu ventana,
            ControlListarPacientes controlListarPacientes,
            ControlAgregarPsicologo controlAgregarPsicologo,
            ControlListarPsicologo controlListarPsicologo,
            ControlAgregarPaciente controlAgregarPaciente,
            VentanaPelfil ventanaPelfil,
            ControlPagar controlPagar
            ) 
            {
        this.ventana = ventana;
        this.controlListarPacientes = controlListarPacientes;
        this.controlAgregarPsicologo = controlAgregarPsicologo;
        this.controlListarPsicologo = controlListarPsicologo;
        this.controlAgregarPaciente = controlAgregarPaciente;
        this.controlPagar = controlPagar;
        this.ventanaPelfil = ventanaPelfil;
    }
    
    /**
     * Inicializa la conexión entre este controlador y la ventana de menú.
     */
    @PostConstruct
    public void init() {
        ventana.setControlMenu(this);
    }
    
    /**
     * Inicia la visualización del menú principal.
     */
    public void inicia() {
        ventana.muestra();
    }
    
    /**
     * Abre el flujo para agregar un nuevo paciente.
     */
   public void agregarPaciente() {
    // Actualiza el breadcrumb
    ventana.actualizaBreadcrumb(List.of("Inicio", "Pacientes", "Agregar Paciente"));
    
    // Inicializa el módulo (si tiene método getVista() que retorna un Node)
    controlAgregarPaciente.inicia();
    
    // Carga la vista en el StackPane del menú
    ventana.cargarVista(controlAgregarPaciente.getVista());
}

     public void cargarVista(Node nuevaVista) {
       System.out.println("Cargando vista: " + nuevaVista);
       contentArea.getChildren().setAll(nuevaVista);
    }
    
    /**
     * Abre la ventana para listar todos los pacientes registrados.
     */

   public void listarPacientes() {
    ventana.actualizaBreadcrumb(List.of("Inicio", "Pacientes", "Listar Pacientes"));
    controlListarPacientes.inicia();
    ventana.cargarVista(controlListarPacientes.getVista());
}

public void agregarPsicologo() {
    ventana.actualizaBreadcrumb(List.of("Inicio", "Psicólogos", "Agregar Psicólogo"));
    controlAgregarPsicologo.inicia();
    ventana.cargarVista(controlAgregarPsicologo.getVista());
}

public void listarPsicologo() {
    ventana.actualizaBreadcrumb(List.of("Inicio", "Psicólogos", "Listar Psicólogos"));
    controlListarPsicologo.inicia();
    ventana.cargarVista(controlListarPsicologo.getVista());
}

    /**
     * Abre el flujo para el pago de servicios.
     */
    public void pagoServicio() {
        // Aquí se podría agregar la lógica para iniciar el proceso de pago
        controlPagar.inicia();
    }

  
     /**
     * Abre la ventana para consultar perfiles de citas.
     */
    public void consultarPerfilCitas() {
        ventanaPelfil.muestra();
    }

 public void mostrarMaterialDidactico() {
        try {
            // Crea el FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-GestionRecursos.fxml"));

            // Usa Spring para crear el controlador y que se inyecten los beans
            loader.setControllerFactory(context::getBean);

            // Carga la UI
            Parent root = loader.load();

            // Crea y muestra la ventana
            Stage stage = new Stage();
            stage.setTitle("Gestión de Material Didáctico");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Aquí puedes mostrar un diálogo de error si quieres
        }
    }

    /**
     * Finaliza la ejecución de la aplicación.
     */
    public void salir() {
        System.exit(0);
    }
}