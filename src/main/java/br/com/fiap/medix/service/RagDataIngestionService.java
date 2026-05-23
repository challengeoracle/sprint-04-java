package br.com.fiap.medix.service;

import br.com.fiap.medix.model.RagContext;
import br.com.fiap.medix.repository.RagContextRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.stereotype.Service;

@Service
public class RagDataIngestionService {

    private final RagContextRepository repository;

    public RagDataIngestionService(RagContextRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void loadPdfToOracle() {
        try {
            if (repository.count() == 0) {
                String pdfPath = "classpath:doc/medix_regras.pdf";
                var reader = new ParagraphPdfDocumentReader(pdfPath);

                var documents = reader.get();
                for (var doc : documents) {
                    RagContext context = new RagContext();
                    context.setConteudo(doc.getFormattedContent());
                    repository.save(context);
                }
                System.out.println(">>> RAG Workaround: " + documents.size() + " parágrafos salvos no Oracle 19c!");
            }
        } catch (Exception e) {
            System.err.println(">>> Erro na carga do RAG Workaround: " + e.getMessage());
        }
    }
}