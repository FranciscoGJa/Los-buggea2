package mx.uam.ayd.proyecto.presentacion.agendaPsicologo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ControlAgendaPsicologo {

    @Autowired
    private VentanaAgendaPsicologo ventanaAgenda;

    public void inicia() {
        ventanaAgenda.muestra();
    }
}
