package com.agendamentos.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AgendamentoMessageParser {

    // aceita: [quero/gostaria/preciso] [marcar/agendar] SERVIÇO [em/para/no/na/dia] DATA [às/as/a] HORA
    private static final Pattern PATTERN_MARCAR = Pattern.compile(
        "(?i)(?:(?:quero|gostaria|preciso)\\s+(?:de\\s+)?)?(?:marcar|agendar)\\s+(?:um\\s+|uma\\s+)?"
        + "(?<servico>.+?)\\s+"
        + "(?:em|para|no|na|dia)\\s+"
        + "(?<data>amanhã|amanha|hoje"
        + "|segunda(?:-feira)?|terça(?:-feira)?|terca(?:-feira)?"
        + "|quarta(?:-feira)?|quinta(?:-feira)?|sexta(?:-feira)?"
        + "|sábado|sabado|domingo"
        + "|\\d{1,2}/\\d{2}(?:/\\d{4})?)"
        + "\\s*(?:às?|as|a)?\\s*"
        + "(?<hora>\\d{1,2}[hH]\\d{0,2}|\\d{1,2}[:\\.]\\d{2})");

    // aceita: horário, horarios, disponível, quando tem
    private static final Pattern PATTERN_HORARIOS = Pattern.compile(
        "(?i)(?:horários?|horarios?|disponível|disponivel|quando\\s+tem)");

    // aceita: cheguei, já cheguei, estou aqui, tô aqui, chegando etc.
    private static final Pattern PATTERN_CHEGADA = Pattern.compile(
        "(?i)(?:cheguei|já\\s+cheguei|ja\\s+cheguei|já\\s+estou|ja\\s+estou"
        + "|estou\\s+chegando|to\\s+chegando|tô\\s+chegando"
        + "|estou\\s+aqui|to\\s+aqui|tô\\s+aqui|chegando)");

    // aceita: cancelar, quero cancelar, desmarcar, quero desmarcar
    private static final Pattern PATTERN_CANCELAR = Pattern.compile(
        "(?i)(?:quero\\s+)?(?:cancelar|desmarcar)(?:\\s+.+)?");

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

        return new DadosAgendamento(
            matcher.group("servico").trim(),
            matcher.group("data").trim(),
            matcher.group("hora").trim()
        );
    }

    public LocalDateTime parseDataHora(String data, String hora) {
        try {
            String horaFormatada = normalizarHora(hora);
            LocalDate localDate = normalizarData(data);

            if (localDate == null) {
                return null;
            }

            String[] parts = horaFormatada.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            return localDate.atTime(hour, minute);
        } catch (Exception e) {
            log.warn("Erro ao fazer parse de data/hora: {} {}", data, hora, e);
            return null;
        }
    }

    private String normalizarHora(String hora) {
        // 14h30 → 14:30
        if (hora.matches("\\d{1,2}[hH]\\d{2}")) {
            return hora.replaceAll("[hH]", ":");
        }
        // 14h → 14:00
        if (hora.matches("\\d{1,2}[hH]")) {
            return hora.replaceAll("[hH]", ":00");
        }
        // 14.30 → 14:30
        return hora.replace(".", ":");
    }

    private LocalDate normalizarData(String data) {
        String dataSemAcento = data.toLowerCase().trim()
            .replace("ã", "a").replace("á", "a").replace("à", "a")
            .replace("ç", "c").replace("é", "e").replace("ê", "e");

        switch (dataSemAcento) {
            case "hoje": return LocalDate.now();
            case "amanha": return LocalDate.now().plusDays(1);
            default: break;
        }

        DayOfWeek dia = parseDiaDaSemana(dataSemAcento);
        if (dia != null) {
            return LocalDate.now().with(TemporalAdjusters.next(dia));
        }

        String dataCompleta = data.matches("\\d{1,2}/\\d{2}")
            ? data + "/" + Year.now().getValue()
            : data;
        return LocalDate.parse(dataCompleta, DateTimeFormatter.ofPattern("d/MM/yyyy"));
    }

    private DayOfWeek parseDiaDaSemana(String dia) {
        if (dia.startsWith("segunda")) return DayOfWeek.MONDAY;
        if (dia.startsWith("terca"))   return DayOfWeek.TUESDAY;
        if (dia.startsWith("quarta"))  return DayOfWeek.WEDNESDAY;
        if (dia.startsWith("quinta"))  return DayOfWeek.THURSDAY;
        if (dia.startsWith("sexta"))   return DayOfWeek.FRIDAY;
        if (dia.startsWith("sabado"))  return DayOfWeek.SATURDAY;
        if (dia.startsWith("domingo")) return DayOfWeek.SUNDAY;
        return null;
    }

    public enum TipoComando {
        MARCAR_AGENDAMENTO,
        LISTAR_HORARIOS,
        AVISAR_CHEGADA,
        CANCELAR_AGENDAMENTO,
        DESCONHECIDO
    }

    public record DadosAgendamento(String servico, String data, String hora) { }
}
