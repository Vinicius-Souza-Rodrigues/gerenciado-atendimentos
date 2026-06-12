"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { api, Agendamento, Cliente } from "@/lib/api";

const STATUS_COLOR: Record<string, string> = {
  PENDENTE: "bg-yellow-100 text-yellow-800",
  CONFIRMADO: "bg-green-100 text-green-800",
  CANCELADO: "bg-red-100 text-red-800",
  CONCLUIDO: "bg-gray-100 text-gray-800",
};

export default function ClienteDetalhePage() {
  const { id } = useParams<{ id: string }>();
  const [cliente, setCliente] = useState<Cliente | null>(null);
  const [historico, setHistorico] = useState<Agendamento[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([api.clientes.listar(), api.clientes.historico(id)])
      .then(([clientes, hist]) => {
        setCliente(clientes.find((c) => c.id === id) ?? null);
        setHistorico(hist);
      })
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="p-6 text-gray-500">Carregando...</div>;
  if (!cliente) return <div className="p-6 text-red-500">Cliente não encontrado.</div>;

  return (
    <div className="p-6">
      <div className="mb-4">
        <Link href="/clientes" className="text-sm text-gray-500 hover:underline">
          ← Clientes
        </Link>
      </div>

      <div className="bg-white rounded-lg shadow p-4 mb-6">
        <h2 className="text-xl font-bold text-gray-800">{cliente.nome}</h2>
        <p className="text-gray-500 text-sm font-mono">{cliente.telefone}</p>
        <p className="text-gray-400 text-xs mt-1">
          Cadastrado em {new Date(cliente.criadoEm).toLocaleDateString("pt-BR")}
        </p>
      </div>

      <h3 className="font-semibold text-gray-700 mb-3">
        Histórico ({historico.length} agendamentos)
      </h3>

      {historico.length === 0 && (
        <p className="text-gray-500 text-sm">Nenhum agendamento.</p>
      )}

      {historico.length > 0 && (
        <table className="w-full bg-white rounded-lg shadow text-sm">
          <thead>
            <tr className="border-b text-left text-gray-500">
              <th className="px-4 py-3">Data / Hora</th>
              <th className="px-4 py-3">Serviço</th>
              <th className="px-4 py-3">Status</th>
            </tr>
          </thead>
          <tbody>
            {historico
              .sort((a, b) => b.dataHora.localeCompare(a.dataHora))
              .map((a) => (
                <tr key={a.id} className="border-b last:border-0">
                  <td className="px-4 py-3 font-mono">
                    {new Date(a.dataHora).toLocaleString("pt-BR", {
                      day: "2-digit",
                      month: "2-digit",
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </td>
                  <td className="px-4 py-3">{a.servico}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-1 rounded text-xs font-medium ${STATUS_COLOR[a.status]}`}>
                      {a.status}
                    </span>
                  </td>
                </tr>
              ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
