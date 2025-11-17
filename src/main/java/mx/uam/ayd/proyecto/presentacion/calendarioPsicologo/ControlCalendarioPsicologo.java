package mx.uam.ayd.proyecto.presentacion.calendarioPsicologo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controlador principal del módulo de calendario para psicólogos.
 * Se encarga de mostrar la ventana y manejar su ciclo de vida.
 */
@Component
public class ControlCalendarioPsicologo {

    @Autowired
    private VentanaCalendarioPsicologo ventanaCalendarioPsicologo;

    /**
     * Inicia la ventana del calendario de psicólogos.
     */
    public void inicia() {
        ventanaCalendarioPsicologo.muestra(this);
    }

    /**
     * Acción temporal de ejemplo para probar comunicación con la vista.
     */
    public void mostrarCitas() {
        System.out.println("📅 Mostrando citas del psicólogo...");
    }
}
