package mx.uam.ayd.proyecto.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.CorreoService;
import mx.uam.ayd.proyecto.datos.CitaRepository;
import java.time.LocalDate;
import java.util.List;
@Service
public class ServicioRecordatorios {
    
    @Autowired
    private CitaRepository citaRepository;
    
    @Autowired
    private CorreoService correoService;
    
    /**
     * Método para probar recordatorios con las citas de HOY
     */
    public void probarRecordatoriosHoy() {
        try {
            LocalDate hoy = LocalDate.now();
            System.out.println("🧪 PROBANDO RECORDATORIOS PARA HOY: " + hoy);
            
            // ✅ USAR EL NUEVO MÉTODO CON RELACIONES CARGADAS
            List<Cita> citasHoy = citaRepository.findByFechaCitaWithRelations(hoy);
            System.out.println("📊 Citas encontradas para hoy: " + citasHoy.size());
            
            if (citasHoy.isEmpty()) {
                System.out.println("ℹ️ No hay citas para hoy");
                return;
            }
            
            int exitosas = 0;
            int fallidas = 0;
            
            // Procesar cada cita de hoy
            for (Cita cita : citasHoy) {
                System.out.println("\n--- Procesando cita ID: " + cita.getId() + " ---");
                
                if (enviarRecordatorioIndividual(cita)) {
                    exitosas++;
                } else {
                    fallidas++;
                }
            }
            
            System.out.println("\n📊 RESULTADO FINAL:");
            System.out.println("✅ Recordatorios exitosos: " + exitosas);
            System.out.println("❌ Recordatorios fallidos: " + fallidas);
            System.out.println("📅 Total citas procesadas: " + citasHoy.size());
            
        } catch (Exception e) {
            System.err.println("❌ Error en prueba de recordatorios: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra información de todas las citas de hoy (solo lectura)
     */
    public void mostrarCitasHoy() {
        try {
            LocalDate hoy = LocalDate.now();
            System.out.println("📅 CITAS PARA HOY (" + hoy + "):");
            
            // ✅ USAR EL NUEVO MÉTODO CON RELACIONES CARGADAS
            List<Cita> citasHoy = citaRepository.findByFechaCitaWithRelations(hoy);
            System.out.println("📊 Total citas: " + citasHoy.size());
            
            if (citasHoy.isEmpty()) {
                System.out.println("ℹ️ No hay citas para hoy");
                return;
            }
            
            for (int i = 0; i < citasHoy.size(); i++) {
                Cita cita = citasHoy.get(i);
                System.out.println("\n" + (i + 1) + ". Cita ID: " + cita.getId());
                System.out.println("   ⏰ Hora: " + cita.getHoraCita());
                System.out.println("   🧠 Psicólogo: " + (cita.getPsicologo() != null ? cita.getPsicologo().getNombre() : "No asignado"));
                
                if (cita.getPerfilCitas() != null) {
                    System.out.println("   👤 Paciente: " + cita.getPerfilCitas().getNombreCompleto());
                    System.out.println("   📧 Email: " + (cita.getPerfilCitas().getEmail() != null ? cita.getPerfilCitas().getEmail() : "No tiene"));
                    System.out.println("   📞 Teléfono: " + (cita.getPerfilCitas().getTelefono() != null ? cita.getPerfilCitas().getTelefono() : "No tiene"));
                    System.out.println("   🧑 Paciente registrado: " + (cita.getPerfilCitas().getPaciente() != null ? "Sí" : "No"));
                } else {
                    System.out.println("   ❌ NO TIENE PERFIL CITAS");
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error mostrando citas de hoy: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Envía recordatorio para una cita individual
     */
    private boolean enviarRecordatorioIndividual(Cita cita) {
        try {
            System.out.println("🔔 Procesando cita ID: " + cita.getId());
            
            // Verificar que tenga PerfilCitas
            if (cita.getPerfilCitas() == null) {
                System.err.println("❌ Cita no tiene PerfilCitas");
                return false;
            }
            
            // Obtener el correo - PRIMERO del PerfilCitas directamente
            String correoPaciente = cita.getPerfilCitas().getEmail();
            String nombrePaciente = cita.getPerfilCitas().getNombreCompleto();
            
            // Si el PerfilCitas no tiene email, intentar del Paciente asociado
            if (correoPaciente == null || correoPaciente.trim().isEmpty()) {
                if (cita.getPerfilCitas().getPaciente() != null) {
                    correoPaciente = cita.getPerfilCitas().getPaciente().getCorreo();
                    nombrePaciente = cita.getPerfilCitas().getPaciente().getNombre();
                    System.out.println("📧 Usando correo del Paciente registrado");
                }
            } else {
                System.out.println("📧 Usando correo del PerfilCitas");
            }
            
            // Validar que tengamos un correo
            if (correoPaciente == null || correoPaciente.trim().isEmpty()) {
                System.err.println("❌ No se encontró correo para el paciente");
                System.out.println("   Email PerfilCitas: " + cita.getPerfilCitas().getEmail());
                System.out.println("   Paciente asociado: " + (cita.getPerfilCitas().getPaciente() != null ? "Sí" : "No"));
                if (cita.getPerfilCitas().getPaciente() != null) {
                    System.out.println("   Correo Paciente: " + cita.getPerfilCitas().getPaciente().getCorreo());
                }
                return false;
            }
            
            // Verificar psicólogo
            if (cita.getPsicologo() == null) {
                System.err.println("❌ Cita no tiene psicólogo asignado");
                return false;
            }
            
            String nombrePsicologo = cita.getPsicologo().getNombre();
            
            System.out.println("👤 Paciente: " + nombrePaciente);
            System.out.println("📧 Correo: " + correoPaciente);
            System.out.println("🧠 Psicólogo: " + nombrePsicologo);
            System.out.println("⏰ Hora: " + cita.getHoraCita());
            
            // Enviar recordatorio
            correoService.enviarRecordatorioCita(
                correoPaciente, 
                nombrePaciente, 
                nombrePsicologo, 
                cita.getFechaCita(), 
                cita.getHoraCita()
            );
            
            System.out.println("✅ Recordatorio enviado exitosamente");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error enviando recordatorio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
