"use client";

import { useEffect, useState } from "react";
import { api, Agendamento } from "@/lib/api";

type Visualizacao = "dia" | "calendario";

const STATUS_LABEL: Record<string, string> = {
  PENDENTE: "Pendente",
  CONFIRMADO: "Confirmado",
  CANCELADO: "Cancelado",
  CONCLUIDO: "Concluído",
};

const STATUS_COLOR: Record<string, string> = {
  PENDENTE: "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/40 dark:text-yellow-300",
  CONFIRMADO: "bg-green-100 text-green-800 dark:bg-green-900/40 dark:text-green-300",
  CANCELADO: "bg-red-100 text-red-800 dark:bg-red-900/40 dark:text-red-300",
  CONCLUIDO: "bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300",
};

const DIAS_SEMANA = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"];
const MESES = [
  "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
  "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro",
];

function toDateKey(date: Date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
}

function agendamentoDateKey(dataHora: string) {
  return toDateKey(new Date(dataHora));
}

export default function AgendaPage() {
  const [visualizacao, setVisualizacao] = useState<Visualizacao>("calendario");
  const [agendamentos, setAgendamentos] = useState<Agendamento[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [dataSelecionada, setDataSelecionada] = useState<Date>(new Date());
  const [mesAtual, setMesAtual] = useState<Date>(new Date());

  async function carregar() {
    try {
      const data = await api.agendamentos.listar();
      setAgendamentos(data);
    } catch {
      setErro("Erro ao carregar agendamentos");
    } finally {
      setLoading(false);
    }
  }

  async function atualizarStatus(id: string, status: string) {
    try {
      await api.agendamentos.atualizarStatus(id, status);
      carregar();
    } catch {
      alert("Erro ao atualizar status");
    }
  }

  useEffect(() => { carregar(); }, []);

  function buildCalendario() {
    const ano = mesAtual.getFullYear();
    const mes = mesAtual.getMonth();
    const primeiroDia = new Date(ano, mes, 1).getDay();
    const totalDias = new Date(ano, mes + 1, 0).getDate();
    const cells: (number | null)[] = [
      ...Array(primeiroDia).fill(null),
      ...Array.from({ length: totalDias }, (_, i) => i + 1),
    ];
    while (cells.length % 7 !== 0) cells.push(null);
    return cells;
  }

  function contagemPorDia(dia: number) {
    const key = toDateKey(new Date(mesAtual.getFullYear(), mesAtual.getMonth(), dia));
    return agendamentos.filter((a) => agendamentoDateKey(a.dataHora) === key).length;
  }

  function abrirDia(dia: number) {
    setDataSelecionada(new Date(mesAtual.getFullYear(), mesAtual.getMonth(), dia));
    setVisualizacao("dia");
  }

  const agendamentosDoDia = agendamentos.filter(
    (a) => agendamentoDateKey(a.dataHora) === toDateKey(dataSelecionada)
  );

  const hoje = toDateKey(new Date());

  return (
    <div className="p-6">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-800 dark:text-gray-100">Agenda</h2>
          {visualizacao === "dia" && (
            <p className="text-gray-500 dark:text-gray-400 text-sm">
              {dataSelecionada.toLocaleDateString("pt-BR", {
                weekday: "long", day: "2-digit", month: "long", year: "numeric",
              })}
            </p>
          )}
        </div>
        <div className="flex gap-2">
          <button
            onClick={() => setVisualizacao("calendario")}
            className={`px-3 py-1.5 text-sm rounded transition-colors ${
              visualizacao === "calendario"
                ? "bg-gray-800 dark:bg-gray-600 text-white"
                : "bg-white dark:bg-gray-800 text-gray-600 dark:text-gray-300 border border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700"
            }`}
          >
            Calendário
          </button>
          <button
            onClick={() => { setDataSelecionada(new Date()); setVisualizacao("dia"); }}
            className={`px-3 py-1.5 text-sm rounded transition-colors ${
              visualizacao === "dia"
                ? "bg-gray-800 dark:bg-gray-600 text-white"
                : "bg-white dark:bg-gray-800 text-gray-600 dark:text-gray-300 border border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700"
            }`}
          >
            Dia
          </button>
        </div>
      </div>

      {loading && <p className="text-gray-400 text-sm">Carregando...</p>}
      {erro && (
        <div className="mb-4 bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800 text-red-700 dark:text-red-300 text-sm px-4 py-3 rounded-lg">
          {erro}
        </div>
      )}

      {/* Vista Calendário */}
      {!loading && visualizacao === "calendario" && (
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow">
          <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100 dark:border-gray-700">
            <button
              onClick={() => setMesAtual(new Date(mesAtual.getFullYear(), mesAtual.getMonth() - 1))}
              className="px-2 py-1 text-gray-500 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-100 text-lg"
            >
              ‹
            </button>
            <span className="font-semibold text-gray-800 dark:text-gray-100">
              {MESES[mesAtual.getMonth()]} {mesAtual.getFullYear()}
            </span>
            <button
              onClick={() => setMesAtual(new Date(mesAtual.getFullYear(), mesAtual.getMonth() + 1))}
              className="px-2 py-1 text-gray-500 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-100 text-lg"
            >
              ›
            </button>
          </div>

          <div className="grid grid-cols-7 border-b border-gray-100 dark:border-gray-700">
            {DIAS_SEMANA.map((d) => (
              <div key={d} className="py-2 text-center text-xs font-medium text-gray-500 dark:text-gray-400">
                {d}
              </div>
            ))}
          </div>

          <div className="grid grid-cols-7">
            {buildCalendario().map((dia, i) => {
              if (!dia) return (
                <div key={i} className="h-20 border-b border-r border-gray-100 dark:border-gray-700 last:border-r-0" />
              );
              const count = contagemPorDia(dia);
              const key = toDateKey(new Date(mesAtual.getFullYear(), mesAtual.getMonth(), dia));
              const isHoje = key === hoje;
              return (
                <div
                  key={i}
                  onClick={() => abrirDia(dia)}
                  className={`h-20 border-b border-r border-gray-100 dark:border-gray-700 last:border-r-0 p-2 cursor-pointer transition-colors ${
                    isHoje
                      ? "bg-blue-50 dark:bg-blue-900/20"
                      : "hover:bg-gray-50 dark:hover:bg-gray-700/50"
                  }`}
                >
                  <span className={`text-sm font-medium ${
                    isHoje ? "text-blue-600 dark:text-blue-400" : "text-gray-700 dark:text-gray-300"
                  }`}>
                    {dia}
                  </span>
                  {count > 0 && (
                    <div className="mt-1">
                      <span className="inline-block bg-gray-800 dark:bg-gray-600 text-white text-xs px-1.5 py-0.5 rounded-full">
                        {count}
                      </span>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Vista Dia */}
      {!loading && visualizacao === "dia" && (
        <>
          <button
            onClick={() => setVisualizacao("calendario")}
            className="text-sm text-gray-500 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-200 hover:underline mb-4 block"
          >
            ← Voltar ao calendário
          </button>

          {agendamentosDoDia.length === 0 && (
            <div className="bg-white dark:bg-gray-800 rounded-lg shadow px-4 py-10 text-center text-gray-400 dark:text-gray-500 text-sm">
              Nenhum agendamento neste dia.
            </div>
          )}

          {agendamentosDoDia.length > 0 && (
            <table className="w-full bg-white dark:bg-gray-800 rounded-lg shadow text-sm">
              <thead>
                <tr className="border-b border-gray-100 dark:border-gray-700 text-left text-gray-500 dark:text-gray-400">
                  <th className="px-4 py-3">Horário</th>
                  <th className="px-4 py-3">Serviço</th>
                  <th className="px-4 py-3">Status</th>
                  <th className="px-4 py-3">Ações</th>
                </tr>
              </thead>
              <tbody>
                {agendamentosDoDia
                  .sort((a, b) => a.dataHora.localeCompare(b.dataHora))
                  .map((a) => (
                    <tr key={a.id} className="border-b border-gray-100 dark:border-gray-700 last:border-0">
                      <td className="px-4 py-3 font-mono text-gray-700 dark:text-gray-300">
                        {new Date(a.dataHora).toLocaleTimeString("pt-BR", {
                          hour: "2-digit", minute: "2-digit",
                        })}
                      </td>
                      <td className="px-4 py-3 text-gray-800 dark:text-gray-200">{a.servico}</td>
                      <td className="px-4 py-3">
                        <span className={`px-2 py-1 rounded text-xs font-medium ${STATUS_COLOR[a.status]}`}>
                          {STATUS_LABEL[a.status]}
                        </span>
                      </td>
                      <td className="px-4 py-3 flex gap-2">
                        {a.status === "PENDENTE" && (
                          <button
                            onClick={() => atualizarStatus(a.id, "CONFIRMADO")}
                            className="text-xs px-2 py-1 bg-green-600 hover:bg-green-700 text-white rounded"
                          >
                            Confirmar
                          </button>
                        )}
                        {(a.status === "PENDENTE" || a.status === "CONFIRMADO") && (
                          <button
                            onClick={() => atualizarStatus(a.id, "CANCELADO")}
                            className="text-xs px-2 py-1 bg-red-600 hover:bg-red-700 text-white rounded"
                          >
                            Cancelar
                          </button>
                        )}
                        {a.status === "CONFIRMADO" && (
                          <button
                            onClick={() => atualizarStatus(a.id, "CONCLUIDO")}
                            className="text-xs px-2 py-1 bg-gray-600 dark:bg-gray-500 hover:bg-gray-700 text-white rounded"
                          >
                            Concluir
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
              </tbody>
            </table>
          )}
        </>
      )}
    </div>
  );
}
