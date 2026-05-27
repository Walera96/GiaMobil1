package com.spbutu.gia.core.infrastructure.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.spbutu.gia.core.application.dto.VedomostDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Сервис генерации PDF ведомости через Flying Saucer + OpenPDF.
 */
@Service
public class VedomostPdfService {

    private static final Logger log = LoggerFactory.getLogger(VedomostPdfService.class);
    private final TemplateEngine templateEngine;

    public VedomostPdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generatePdf(VedomostDto vedomost) {
        try {
            Context context = new Context(Locale.forLanguageTag("ru"));
            context.setVariables(buildContextMap(vedomost));

            String html = templateEngine.process("vedomost", context);

            ITextRenderer renderer = new ITextRenderer();
            registerFonts(renderer);

            String baseUrl = ResourceUtils.getURL("classpath:static/").toString();
            renderer.setDocumentFromString(html, baseUrl);
            renderer.layout();

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                renderer.createPDF(baos);
                return baos.toByteArray();
            }
        } catch (DocumentException | IOException e) {
            log.error("Ошибка генерации PDF ведомости", e);
            throw new RuntimeException("Не удалось сгенерировать PDF ведомости", e);
        }
    }

    private Map<String, Object> buildContextMap(VedomostDto vedomost) {
        Map<String, Object> map = new HashMap<>();
        map.put("vedomost", vedomost);

        String formattedDate = vedomost.getDate() != null
                ? vedomost.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : "—";
        map.put("formattedDate", formattedDate);

        String chairmanFull = "";
        if (vedomost.getChairmanName() != null && !vedomost.getChairmanName().isBlank()) {
            chairmanFull = vedomost.getChairmanName();
            if (vedomost.getChairmanDegree() != null && !vedomost.getChairmanDegree().isBlank()) {
                chairmanFull += ", " + vedomost.getChairmanDegree();
            }
        }
        map.put("chairmanFull", chairmanFull);

        String membersLine = "";
        if (vedomost.getCommitteeMembers() != null && !vedomost.getCommitteeMembers().isEmpty()) {
            membersLine = vedomost.getCommitteeMembers().stream()
                    .map(m -> {
                        String s = m.getFullName() != null ? m.getFullName() : "";
                        if (m.getDegree() != null && !m.getDegree().isBlank()) {
                            s += ", " + m.getDegree();
                        }
                        return s;
                    })
                    .collect(Collectors.joining("; "));
        }
        map.put("membersLine", membersLine);

        return map;
    }

    private void registerFonts(ITextRenderer renderer) {
        String[] fontFiles = {
                "fonts/TimesNewRoman.ttf",
                "fonts/TimesNewRoman-Bold.ttf",
                "fonts/TimesNewRoman-Italic.ttf",
                "fonts/TimesNewRoman-BoldItalic.ttf"
        };
        for (String fontFile : fontFiles) {
            try {
                ClassPathResource fontResource = new ClassPathResource(Objects.requireNonNull(fontFile));
                if (fontResource.exists()) {
                    String fontUrl = fontResource.getURL().toString();
                    renderer.getFontResolver().addFont(fontUrl, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    log.debug("Registered font: {}", fontFile);
                }
            } catch (Exception e) {
                log.warn("Failed to register font {}: {}", fontFile, e.getMessage());
            }
        }
    }
}
