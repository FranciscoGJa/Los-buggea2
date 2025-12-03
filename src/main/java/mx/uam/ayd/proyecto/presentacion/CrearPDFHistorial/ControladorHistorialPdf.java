/*package mx.uam.ayd.proyecto.presentacion.CrearPDFHistorial;


import mx.uam.ayd.proyecto.negocio.ServicioHistorialPdf;
import mx.uam.ayd.proyecto.negocio.modelo.*;
import mx.uam.ayd.proyecto.datos.PacienteRepository;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
public class ControladorHistorialPdf {

    private final ServicioHistorialPdf servicioHistorialPdf;
    private final PacienteRepository pacienteRepository;

    /**
     * Endpoint para generar y descargar PDF del historial clínico
     */
/*     @GetMapping("/paciente/{pacienteId}/pdf")
    @Transactional(readOnly = true)  // IMPORTANTE: Mantener sesión abierta
    public ResponseEntity<byte[]> generarPdfHistorial(@PathVariable Long pacienteId) {
        try {
            Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);
            
            if (pacienteOpt.isEmpty()) {
                log.warn("Paciente no encontrado con ID: {}", pacienteId);
                return ResponseEntity.notFound().build();
            }
            
            Paciente paciente = pacienteOpt.get();
            
            // Extraer todos los datos dentro del contexto transaccional
            String nombrePaciente = paciente.getNombre();
            int edad = paciente.getEdad();
            String telefono = paciente.getTelefono();
            String correo = paciente.getCorreo();
            
            // Extraer psicólogo de manera segura
            String nombrePsicologo = "No asignado";
            try {
                if (paciente.getPsicologo() != null) {
                    nombrePsicologo = paciente.getPsicologo().getNombre();
                }
            } catch (Exception e) {
                nombrePsicologo = "No disponible";
                log.debug("No se pudo cargar psicólogo: {}", e.getMessage());
            }
            
            // Extraer historial clínico
            HistorialClinico historial = paciente.getHistorialClinico();
            
            // Extraer baterías clínicas
            List<BateriaClinica> baterias = null;
            try {
                baterias = paciente.getBateriasClinicas();
                if (baterias != null) {
                    baterias.size(); // Forzar carga
                }
            } catch (Exception e) {
                log.debug("No se pudieron cargar baterías: {}", e.getMessage());
            }
            
            // Extraer perfiles de citas
            List<PerfilCitas> perfilesCitas = null;
            try {
                perfilesCitas = paciente.getPerfilesCitas();
                if (perfilesCitas != null) {
                    perfilesCitas.size(); // Forzar carga
                    // Cargar citas de cada perfil
                    for (PerfilCitas perfil : perfilesCitas) {
                        if (perfil.getCitas() != null) {
                            perfil.getCitas().size();
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("No se pudieron cargar perfiles de citas: {}", e.getMessage());
            }
            
            // Llamar al servicio con datos extraídos
            byte[] pdfBytes = servicioHistorialPdf.generarPdfHistorialSeguro(
                nombrePaciente,
                edad,
                telefono,
                correo,
                nombrePsicologo,
                historial,
                baterias,
                perfilesCitas
            );
            
            // Crear nombre de archivo
            String nombreArchivo = String.format("historial_%s_%s.pdf", 
                paciente.getNombre().toLowerCase().replace(" ", "_"),
                LocalDate.now().toString());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
                    
        } catch (DocumentException e) {
            log.error("Error al generar PDF para paciente ID {}: {}", pacienteId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            log.error("Error inesperado al generar PDF: {}", e.getMessage());
            e.printStackTrace(); // Para debugging
            return ResponseEntity.internalServerError().build();
        }
    }
}*/