package com.agendamentos.domain.service;

import com.agendamentos.domain.entity.Agendamento;
import com.agendamentos.domain.entity.Cliente;
import com.agendamentos.domain.entity.Prestador;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class MensagensPersonalizadasService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String mensagemBoasVindas(Prestador prestador) {
        return """
                <b>Bem-vindo ao agendamento de %s! 👋</b>

                Aqui você pode:
                ✅ Marcar uma consulta
                ✅ Ver horários disponíveis
                ✅ Cancelar um agendamento

                <b>Como usar:</b>
                • "Quero marcar corte em 10/06 às 14h"
                • "Que horários tem disponível?"
                • "Cheguei!" (quando estiver chegando)

                %s
                """.formatted(prestador.getNomeNegocio(), prestador.getMensagemPersonalizada());
    }

    public String mensagemAgendamentoConfirmado(Agendamento agendamento, Cliente cliente, Prestador prestador) {
        return """
                <b>✅ Agendamento Confirmado, %s!</b>

                <b>📅 Data/Hora:</b> %s
                <b>💇 Serviço:</b> %s
                <b>🏢 Local:</b> %s
                <b>📍 Endereço:</b> %s

                Qualquer dúvida, entre em contato!
                """.formatted(
            cliente.getNome(),
            agendamento.getDataHora().format(FORMATTER),
            agendamento.getServico().getValor(),
            prestador.getNomeNegocio(),
            prestador.getEndereco()
        );
    }

    public String mensagemHorariosDisponiveis(List<String> horarios, String servico) {
        StringBuilder sb = new StringBuilder("<b>📅 Horários disponíveis para %s:</b>\n\n".formatted(servico));
        horarios.forEach(h -> sb.append("✅ ").append(h).append("\n"));
        return sb.toString();
    }

    public String mensagemErroAgendamento(String motivo) {
        return "<b>❌ Não foi possível agendar</b>\n\nMotivo: " + motivo + "\n\nTente novamente!";
    }

    public String mensagemCancelamento(String servico) {
        return "<b>❌ Agendamento cancelado</b>\n\nServiço: " + servico + "\n\nSe precisar, é só marcar de novo! 😊";
    }

    public String mensagemLembrete(Agendamento agendamento, Prestador prestador) {
        return """
                <b>⏰ Lembrete de Agendamento!</b>

                Sua consulta está próxima:
                <b>📅</b> %s
                <b>💇</b> %s
                <b>🏢</b> %s

                Avise quando estiver chegando! 👋
                """.formatted(
            agendamento.getDataHora().format(FORMATTER),
            agendamento.getServico().getValor(),
            prestador.getNomeNegocio()
        );
    }

    public String mensagemClienteChegou(Agendamento agendamento, Prestador prestador) {
        return """
                <b>✅ Cliente chegando!</b>

                <b>%s</b> está a caminho para:
                📍 %s
                🕐 %s
                """.formatted(
            agendamento.getClienteId(),
            prestador.getNomeNegocio(),
            agendamento.getDataHora().format(FORMATTER)
        );
    }
}
