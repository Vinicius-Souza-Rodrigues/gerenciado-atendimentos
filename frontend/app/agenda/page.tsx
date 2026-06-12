"use client";

import { useEffect, useState } from "react";
import { api, Agendamento } from "@/lib/api";

const STATUS_LABEL: Record<string, string> = {
  PENDENTE: "Pendente",
  CONFIRMADO: "Confirmado",
  CANCELADO: "Cancelado",
  CONCLUIDO: "Concluído",
};

const STATUS_COLOR: Record<string, string> = {
  PENDENTE: "bg-yellow-100 text-yellow-800",
  CONFIRMADO: "bg-green-100 text-green-800",
  CANCELADO: "bg-red-100 text-red-800",
  CONCLUIDO: "bg-gray-100 text-gray-800",
};

export default function AgendaPage() {
  const [agendamentos, setAgendamentos] = useState<Agendamento[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);

  const hoje = new Date().toLocaleDateString("pt-BR");

  async function carregar() {
    try {
      const data = await api.agendamentos.listar();
      const doHoje = data.filter((a) => {
        const data = new Date(a.dataHora).toLocaleDateString("pt-BR");
        return data === hoje;
      });
      setAgendamentos(doHoje);
    } catch (e) {
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

  return (
    <div className="p-6">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Agenda do Dia</h2>
        <p className="text-gray-500 text-sm">{hoje}</p>
      </div>

      {loading && <p className="text-gray-500">Carregando...</p>}
      {erro && <p className="text-red-500">{erro}</p>}

      {!loading && agendamentos.length === 0 && (
        <p className="text-gray-500">Nenhum agendamento para hoje.</p>
      )}

      {!loading && agendamentos.length > 0 && (
        <table className="w-full bg-white rounded-lg shadow text-sm">
          <thead>
            <tr className="border-b text-left text-gray-500">
              <th className="px-4 py-3">Horário</th>
              <th className="px-4 py-3">Serviço</th>
              <th className="px-4 py-3">Status</th>
              <th className="px-4 py-3">Ações</th>
            </tr>
          </thead>
          <tbody>
            {agendamentos
              .sort((a, b) => a.dataHora.localeCompare(b.dataHora))
              .map((a) => (
                <tr key={a.id} className="border-b last:border-0">
                  <td className="px-4 py-3 font-mono">
                    {new Date(a.dataHora).toLocaleTimeString("pt-BR", {
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </td>
                  <td className="px-4 py-3">{a.servico}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-1 rounded text-xs font-medium ${STATUS_COLOR[a.status]}`}>
                      {STATUS_LABEL[a.status]}
                    </span>
                  </td>
                  <td className="px-4 py-3 flex gap-2">
                    {a.status === "PENDENTE" && (
                      <button
                        onClick={() => atualizarStatus(a.id, "CONFIRMADO")}
                        className="text-xs px-2 py-1 bg-green-600 text-white rounded hover:bg-green-700"
                      >
                        Confirmar
                      </button>
                    )}
                    {(a.status === "PENDENTE" || a.status === "CONFIRMADO") && (
                      <button
                        onClick={() => atualizarStatus(a.id, "CANCELADO")}
                        className="text-xs px-2 py-1 bg-red-600 text-white rounded hover:bg-red-700"
                      >
                        Cancelar
                      </button>
                    )}
                    {a.status === "CONFIRMADO" && (
                      <button
                        onClick={() => atualizarStatus(a.id, "CONCLUIDO")}
                        className="text-xs px-2 py-1 bg-gray-600 text-white rounded hover:bg-gray-700"
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
    </div>
  );
}
