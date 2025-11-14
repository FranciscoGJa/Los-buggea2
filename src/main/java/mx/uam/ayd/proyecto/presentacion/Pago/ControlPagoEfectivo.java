package mx.uam.ayd.proyecto.presentacion.Pago;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ControlPagoEfectivo {

    private final VentanaPagoEfectivo ventanaPagoEfectivo;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param ventanaPagoEfectivo vista encargada de mostrar la ventana de pago
     */

    @Autowired
    public ControlPagoEfectivo(VentanaPagoEfectivo ventanaPagoEfectivo) {
        this.ventanaPagoEfectivo = ventanaPagoEfectivo;
    }
    
    /**
     * Inicia el flujo de pago.
     * <p>Recupera los datos necesarios y los envía a la vista para su visualización.</p>
     */
    public void inicia() {
        ventanaPagoEfectivo.setControlPagoEfectivo(this);
        ventanaPagoEfectivo.mostrar();
    }
}