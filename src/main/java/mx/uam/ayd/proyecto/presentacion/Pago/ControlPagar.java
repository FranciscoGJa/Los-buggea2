package mx.uam.ayd.proyecto.presentacion.Pago;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ControlPagar {

    private final VentanaPago ventanaPago;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param ventanaPago vista encargada de mostrar la ventana de pago
     */

    @Autowired
    public ControlPagar(VentanaPago ventanaPago) {
        this.ventanaPago = ventanaPago;
    }
    
    /**
     * Inicia el flujo de pago.
     * <p>Recupera los datos necesarios y los envía a la vista para su visualización.</p>
     */
    public void inicia() {
        ventanaPago.setControlPago(this);
        ventanaPago.mostrar();
    }
}