package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.negocio.modelo.*;
import mx.uam.ayd.proyecto.datos.PacienteRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicioHistorialPdf {

    private final PacienteRepository pacienteRepository;

    /**
     * Método principal que maneja todo
     * */
    @Transactional(readOnly = true)
    public byte[] generarPdfHistorial(Long pacienteId) throws DocumentException {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);
        
        if (pacienteOpt.isEmpty()) {
            throw new IllegalArgumentException("Paciente no encontrado con ID: " + pacienteId);
        }
        
        Paciente paciente = pacienteOpt.get();
        log.info("Generando PDF para paciente: {}", paciente.getNombre());
        
        return generarPdfDesdePaciente(paciente);
    }

    /**
     * Método interno que asume que ya estamos en contexto transaccional
     */
    private byte[] generarPdfDesdePaciente(Paciente paciente) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        
        document.open();
        
        try {
            agregarEncabezado(document, paciente.getNombre());
            agregarInformacionPaciente(document, paciente);
            agregarHistorialClinico(document, paciente.getHistorialClinico());
            agregarCitasPaciente(document, paciente);
            agregarPieConfidencial(document);
            
        } finally {
            document.close();
        }
        
        return outputStream.toByteArray();
    }

    private void agregarEncabezado(Document document, String nombrePaciente) throws DocumentException {
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 10);
        
        Paragraph encabezado = new Paragraph();
        encabezado.add(new Chunk("HISTORIAL CLÍNICO - " + nombrePaciente.toUpperCase(), fontTitulo));
        encabezado.add(Chunk.NEWLINE);
        encabezado.add(new Chunk("Fecha de generación: " + 
            java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), 
            fontSubtitulo));
        encabezado.setAlignment(Element.ALIGN_CENTER);
        encabezado.setSpacingAfter(20);
        document.add(encabezado);
    }

    private void agregarInformacionPaciente(Document document, Paciente paciente) throws DocumentException {
        Font fontTituloSeccion = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font fontInfo = FontFactory.getFont(FontFactory.HELVETICA, 10);
        
        Paragraph tituloSeccion = new Paragraph("INFORMACIÓN DEL PACIENTE", fontTituloSeccion);
        tituloSeccion.setSpacingAfter(10);
        document.add(tituloSeccion);
        
        String nombrePsicologo = "No asignado";
        try {
            if (paciente.getPsicologo() != null) {
                nombrePsicologo = paciente.getPsicologo().getNombre();
            }
        } catch (Exception e) {
            log.debug("No se pudo obtener nombre del psicólogo: {}", e.getMessage());
        }
        
        String infoPaciente = String.format(
            "Nombre: %s\nEdad: %d años\nTeléfono: %s\nCorreo: %s\nPsicólogo asignado: %s",
            paciente.getNombre(),
            paciente.getEdad(),
            paciente.getTelefono() != null ? paciente.getTelefono() : "No especificado",
            paciente.getCorreo() != null ? paciente.getCorreo() : "No especificado",
            nombrePsicologo
        );
        
        Paragraph parrafoInfo = new Paragraph(infoPaciente, fontInfo);
        parrafoInfo.setSpacingAfter(15);
        document.add(parrafoInfo);
    }

    private void agregarHistorialClinico(Document document, HistorialClinico historial) throws DocumentException {
        Font fontTituloSeccion = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font fontInfo = FontFactory.getFont(FontFactory.HELVETICA, 10);
        
        Paragraph tituloSeccion = new Paragraph("HISTORIAL CLÍNICO", fontTituloSeccion);
        tituloSeccion.setSpacingAfter(10);
        document.add(tituloSeccion);
        
        if (historial == null) {
            Paragraph sinHistorial = new Paragraph("No se ha registrado historial clínico para este paciente.", fontInfo);
            sinHistorial.setSpacingAfter(15);
            document.add(sinHistorial);
            return;
        }
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Paragraph contenido = new Paragraph();
        
        contenido.add(new Chunk("Fecha de Elaboración: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        contenido.add(historial.getFechaElaboracion() != null ? 
                     formatter.format(historial.getFechaElaboracion()) : "No especificada");
        contenido.add(Chunk.NEWLINE);
        contenido.add(Chunk.NEWLINE);
        
        contenido.add(new Chunk("Motivo de la Consulta:\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        contenido.add(historial.getMotivo() != null ? historial.getMotivo() : "No especificado");
        contenido.add(Chunk.NEWLINE);
        contenido.add(Chunk.NEWLINE);
        
        contenido.add(new Chunk("Consumo de Sustancias: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        contenido.add(historial.getConsumoDrogas() != null ? historial.getConsumoDrogas() : "No especificado");
        contenido.add(Chunk.NEWLINE);
        
        if (historial.getDescripcionDrogas() != null && !historial.getDescripcionDrogas().trim().isEmpty()) {
            contenido.add(new Chunk("Descripción: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            contenido.add(historial.getDescripcionDrogas());
            contenido.add(Chunk.NEWLINE);
        }
        
        contenido.add(Chunk.NEWLINE);
        contenido.add(new Chunk("Observaciones del Psicólogo:\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        contenido.add(historial.getObservaciones() != null ? historial.getObservaciones() : "Sin observaciones");
        contenido.add(Chunk.NEWLINE);
        contenido.add(Chunk.NEWLINE);
        
        contenido.add(new Chunk("Consentimiento Informado Aceptado: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        contenido.add(historial.isConsentimientoAceptado() ? "Sí" : "No");
        
        document.add(contenido);
        document.add(Chunk.NEWLINE);
    }

    private void agregarCitasPaciente(Document document, Paciente paciente) throws DocumentException {
        Font fontTituloSeccion = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font fontInfo = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font fontCitaTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        
        Paragraph tituloSeccion = new Paragraph("HISTORIAL DE CITAS", fontTituloSeccion);
        tituloSeccion.setSpacingAfter(10);
        document.add(tituloSeccion);
        
        try {
            List<PerfilCitas> perfilesCitas = paciente.getPerfilesCitas();
            
            if (perfilesCitas == null || perfilesCitas.isEmpty()) {
                Paragraph sinCitas = new Paragraph("No se han registrado citas para este paciente.", fontInfo);
                sinCitas.setSpacingAfter(15);
                document.add(sinCitas);
                return;
            }
            
            int contadorCitas = 0;
            for (PerfilCitas perfil : perfilesCitas) {
                List<Cita> citas = perfil.getCitas();
                
                if (citas != null) {
                    for (Cita cita : citas) {
                        contadorCitas++;
                        
                        if (contadorCitas > 1 && contadorCitas % 3 == 1) {
                            document.newPage();
                        }
                        
                        Paragraph infoCita = new Paragraph();
                        infoCita.add(new Chunk(
                            String.format("Cita #%d - %s", contadorCitas, 
                                cita.getFechaCita() != null ? 
                                cita.getFechaCita().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : 
                                "Sin fecha"), 
                            fontCitaTitulo));
                        infoCita.add(Chunk.NEWLINE);
                        
                        if (cita.getHoraCita() != null) {
                            infoCita.add(String.format("Hora: %s", 
                                cita.getHoraCita().format(DateTimeFormatter.ofPattern("HH:mm"))));
                            infoCita.add(Chunk.NEWLINE);
                        }
                        
                        if (cita.getEstadoCita() != null) {
                            infoCita.add(String.format("Estado: %s", cita.getEstadoCita()));
                            infoCita.add(Chunk.NEWLINE);
                        }
                        
                        if (cita.getDetallesAdicionalesPaciente() != null && 
                            !cita.getDetallesAdicionalesPaciente().trim().isEmpty()) {
                            infoCita.add(String.format("Detalles paciente: %s", 
                                cita.getDetallesAdicionalesPaciente().length() > 100 ?
                                cita.getDetallesAdicionalesPaciente().substring(0, 100) + "..." :
                                cita.getDetallesAdicionalesPaciente()));
                            infoCita.add(Chunk.NEWLINE);
                        }
                        
                        infoCita.add(Chunk.NEWLINE);
                        infoCita.setSpacingAfter(10);
                        document.add(infoCita);
                    }
                }
            }
            
            Paragraph resumenCitas = new Paragraph(
                String.format("Total de citas registradas: %d", contadorCitas), 
                fontInfo);
            resumenCitas.setSpacingAfter(15);
            document.add(resumenCitas);
            
        } catch (Exception e) {
            log.debug("No se pudieron cargar citas: {}", e.getMessage());
            Paragraph errorCitas = new Paragraph("No se pudieron cargar las citas del paciente.", fontInfo);
            errorCitas.setSpacingAfter(15);
            document.add(errorCitas);
        }
    }

    private void agregarPieConfidencial(Document document) throws DocumentException {
        Font fontPie = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9);
        
        Paragraph pie = new Paragraph();
        pie.add(Chunk.NEWLINE);
        pie.add(Chunk.NEWLINE);
        pie.add(new Chunk("DOCUMENTO CONFIDENCIAL - Información médica protegida por secreto profesional", fontPie));
        pie.add(Chunk.NEWLINE);
        pie.add(new Chunk("Generado por Sistema de Gestión Psicológica - UAM", fontPie));
        pie.setAlignment(Element.ALIGN_CENTER);
        document.add(pie);
    }
}