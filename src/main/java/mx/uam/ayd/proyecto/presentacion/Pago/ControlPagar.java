package mx.uam.ayd.proyecto.presentacion.Pago;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ControlPagar {

    private final VentanaPago ventanaPago;

    @Autowired
    public ControlPagar(VentanaPago ventanaPago) {
        this.ventanaPago = ventanaPago;
    }

    public void inicia() {
        ventanaPago.setControlPago(this);
        ventanaPago.mostrar();
    }
}