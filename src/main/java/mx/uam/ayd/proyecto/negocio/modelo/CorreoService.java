package mx.uam.ayd.proyecto.negocio.modelo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class CorreoService {
    @Autowired
    private JavaMailSender mailSender;
    /*Envia credenciales al psic por correo
     * 
     * @param correo direccion de correo del psicologo
     * @param usuario nombre de usuario generado
     * @param contra contraseña generada
     */

    public void enviarCredenciales(String correo,String usuario,String contra){
        try{
            SimpleMailMessage mensaje=new SimpleMailMessage();
            mensaje.setTo(correo);
            mensaje.setSubject("Credenciales de acceso al sistema");
            mensaje.setText("Hola,\n\n" +
                "Se ha creado tu cuenta de psicólogo en el sistema.\n\n" +
                "Usuario: " + usuario + "\n" +
                "Contraseña: " + contra + "\n\n" +
                "Por favor, cambia tu contraseña después del primer inicio de sesión.\n\n" +
                "Saludos,\nCentro de Salud Mental");
            mailSender.send(mensaje);
        }catch(Exception e){
            System.err.println("Error al enviar correo: "+e.getMessage());
        }
    }
}
