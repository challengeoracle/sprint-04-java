package br.com.fiap.medix.chat.util;

import br.com.fiap.medix.model.Sala;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ChatTextUtils {

    private ChatTextUtils() {
    }

    public static String normalizar(String texto) {
        return Normalizer
                .normalize(texto == null ? "" : texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }

    public static String extrairEmail(String mensagem) {
        if (mensagem == null) {
            return null;
        }

        String[] partes = mensagem.split("\\s+");

        for (String parte : partes) {
            String limpa = parte.trim()
                    .replace(",", "")
                    .replace(";", "")
                    .replace("(", "")
                    .replace(")", "");

            if (limpa.contains("@") && limpa.contains(".")) {
                return limpa.toLowerCase();
            }
        }

        return null;
    }

    public static Integer extrairNumero(String mensagem) {
        if (mensagem == null) {
            return null;
        }

        String apenasNumeros = mensagem.replaceAll("\\D", "");

        if (apenasNumeros.isBlank()) {
            return null;
        }

        try {
            return Integer.parseInt(apenasNumeros);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String extrairEspecialidade(String mensagem) {
        String texto = normalizar(mensagem);

        if (texto.contains("cardio") || texto.contains("cardilogia")) {
            return "Cardiologia";
        }

        if (texto.contains("pediatria") || texto.contains("crianca")) {
            return "Pediatria";
        }

        if (texto.contains("gineco")) {
            return "Ginecologia";
        }

        if (texto.contains("orto")) {
            return "Ortopedia";
        }

        if (texto.contains("clinica") || texto.contains("geral")) {
            return "Clínica Geral";
        }

        if (mensagem == null || mensagem.isBlank()) {
            return null;
        }

        return mensagem.trim();
    }

    public static LocalDate parseDataFlexivel(String mensagem) {
        String texto = normalizar(mensagem);

        if (texto.contains("hoje")) {
            return LocalDate.now();
        }

        if (texto.contains("depois de amanha")) {
            return LocalDate.now().plusDays(2);
        }

        if (texto.contains("amanha")) {
            return LocalDate.now().plusDays(1);
        }

        try {
            return LocalDate.parse(mensagem.trim());
        } catch (Exception ignored) {
        }

        try {
            String limpo = mensagem.trim().replaceAll("[^0-9/]", "");
            String[] partes = limpo.split("/");

            if (partes.length == 2) {
                int dia = Integer.parseInt(partes[0]);
                int mes = Integer.parseInt(partes[1]);
                int ano = LocalDate.now().getYear();

                return LocalDate.of(ano, mes, dia);
            }

            if (partes.length == 3) {
                int dia = Integer.parseInt(partes[0]);
                int mes = Integer.parseInt(partes[1]);
                int ano = Integer.parseInt(partes[2]);

                if (ano < 100) {
                    ano += 2000;
                }

                return LocalDate.of(ano, mes, dia);
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public static String nomeSala(Sala sala) {
        if (sala.getNome() != null && !sala.getNome().isBlank()) {
            return sala.getNome();
        }

        if (sala.getNumero() != null && !sala.getNumero().isBlank()) {
            return "Sala " + sala.getNumero();
        }

        return "Sala " + sala.getId();
    }

    public static String formatarHora(LocalDateTime dataHora) {
        return "%02d:%02d".formatted(dataHora.getHour(), dataHora.getMinute());
    }

    public static String formatarDataHora(LocalDateTime dataHora) {
        return "%02d/%02d/%d às %02d:%02d".formatted(
                dataHora.getDayOfMonth(),
                dataHora.getMonthValue(),
                dataHora.getYear(),
                dataHora.getHour(),
                dataHora.getMinute()
        );
    }
}