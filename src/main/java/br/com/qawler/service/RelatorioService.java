package br.com.qawler.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Generates reports (PDF) and retrieves test-result data for reporting views.
 *
 * Expected entity: {@code br.com.qawler.model.Relatorio} with fields:
 *   id (Long), testeId (Long), geradoEm (LocalDateTime), pdfBytes (byte[])
 */
@Service
public class RelatorioService {

    private static final Logger log = LoggerFactory.getLogger(RelatorioService.class);

    // Replace with real repositories when entity layer exists
    // private final RelatorioRepository relatorioRepository;
    // private final TesteRepository testeRepository;

    public RelatorioService(/* RelatorioRepository relatorioRepository, TesteRepository testeRepository */) {
        // this.relatorioRepository = relatorioRepository;
        // this.testeRepository = testeRepository;
    }

    /**
     * Generates a PDF report for the given test.
     *
     * @param testId        the test ID
     * @param sistemaNome   name of the system under test
     * @param bugs          list of bug descriptions found during the test
     * @return byte array containing the generated PDF
     */
    public byte[] gerarPdf(Long testId, String sistemaNome, List<String> bugs) {
        log.info("Generating PDF report for test {} (sistema: {})", testId, sistemaNome);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Relatório de Teste — QAwler", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Metadata
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            document.add(new Paragraph("Sistema: " + sistemaNome, metaFont));
            document.add(new Paragraph("Teste ID: " + testId, metaFont));
            document.add(new Paragraph("Gerado em: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), metaFont));
            document.add(Chunk.NEWLINE);

            // Divider
            document.add(new Paragraph("─────────────────────────────────────────"));
            document.add(Chunk.NEWLINE);

            // Bugs section
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            document.add(new Paragraph("Bugs Encontrados (" + bugs.size() + ")", sectionFont));
            document.add(Chunk.NEWLINE);

            if (bugs.isEmpty()) {
                document.add(new Paragraph("Nenhum bug encontrado. ✓", metaFont));
            } else {
                for (int i = 0; i < bugs.size(); i++) {
                    document.add(new Paragraph((i + 1) + ". " + bugs.get(i), metaFont));
                }
            }

            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("── Fim do Relatório ──", metaFont));

        } catch (Exception e) {
            log.error("Error generating PDF for test {}: {}", testId, e.getMessage(), e);
            throw new RuntimeException("Falha ao gerar PDF do relatório", e);
        } finally {
            document.close();
        }

        byte[] pdfBytes = baos.toByteArray();
        log.info("PDF generated — {} bytes", pdfBytes.length);

        // TODO: persist via relatorioRepository
        return pdfBytes;
    }

    /**
     * Finds a previously generated report by its ID.
     */
    public Optional<Object> buscarPorId(Long id) {
        // return relatorioRepository.findById(id);
        return Optional.empty();  // stub
    }

    /**
     * Lists all reports for a given test.
     */
    public List<Object> listarPorTeste(Long testeId) {
        // return relatorioRepository.findByTesteId(testeId);
        return List.of();  // stub
    }
}
