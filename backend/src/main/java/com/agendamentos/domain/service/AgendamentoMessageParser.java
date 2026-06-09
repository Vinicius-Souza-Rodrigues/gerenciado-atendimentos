package com.agendamentos.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AgendamentoMessageParser {

    private static final Pattern PATTERN_MARCAR = Pattern.compile(
        "(?i)(quero|gostaria|preciso)\\s+(marcar|agendar)\\s+(um|uma)?\\s*(.+?)\\s+(em|para|no)\\s+(.+?)\\s+(às|em|às)?\\s*(\\d{1,2}[:\\.]\\d{2})");
    private static final Pattern PATTERN_HORARIOS = Pattern.compile(
        "(?i)(quais|que|quem)\\s+(horários|horarios|horas|disponível|disponivel)");
    private static final Pattern PATTERN_CHEGADA = Pattern.compile(
        "(?i)(cheguei|já estou|estou chegando|to chegando)");
    private static final Pattern PATTERN_CANCELAR = Pattern.compile(
        "(?i)(cancelar|desmarcar)\\s+(.+)");

    public TipoComando identificarComando(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            return TipoComando.DESCONHECIDO;
        }

        if (PATTERN_MARCAR.matcher(mensagem).find()) {
            return TipoComando.MARCAR_AGENDAMENTO;
        }
        if (PATTERN_HORARIOS.matcher(mensagem).find()) {
            return TipoComando.LISTAR_HORARIOS;
        }
        if (PATTERN_CHEGADA.matcher(mensagem).find()) {
            return TipoComando.AVISAR_CHEGADA;
        }
        if (PATTERN_CANCELAR.matcher(mensagem).find()) {
            return TipoComando.CANCELAR_AGENDAMENTO;
        }

        return TipoComando.DESCONHECIDO;
    }

    public DadosAgendamento extrairDados(String mensagem) {
        Matcher matcher = PATTERN_MARCAR.matcher(mensagem);

        if (!matcher.find()) {
            return null;
        }

        String servico = matcher.group(4).trim();
        String data = matcher.group(6).trim();
        String hora = matcher.group(8).trim();

        return new DadosAgendamento(servico, data, hora);
    }

    public LocalDateTime parseDataHora(String data, String hora) {
        try {
            String horaFormatada = hora.replace(".", ":");
            // adiciona o ano corrente se vier apenas dd/MM
            String dataCompleta = data.matches("\\d{2}/\\d{2}")
                ? data + "/" + Year.now().getValue()
                : data;
            String dataHora = dataCompleta + " " + horaFormatada;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return LocalDateTime.parse(dataHora, formatter);
        } catch (Exception e) {
            log.warn("Erro ao fazer parse de data/hora: {} {}", data, hora, e);
            return null;
        }
    }

    public enum TipoComando {
        MARCAR_AGENDAMENTO,
        LISTAR_HORARIOS,
        AVISAR_CHEGADA,
        CANCELAR_AGENDAMENTO,
        DESCONHECIDO
    }

    public record DadosAgendamento(String servico, String data, String hora) {}
}
