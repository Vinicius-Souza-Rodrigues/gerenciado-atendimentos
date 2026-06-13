"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { api, Agendamento, Cliente } from "@/lib/api";

const STATUS_COLOR: Record<string, string> = {
  PENDENTE: "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/40 dark:text-yellow-300",
  CONFIRMADO: "bg-green-100 text-green-800 dark:bg-green-900/40 dark:text-green-300",
  CANCELADO: "bg-red-100 text-red-800 dark:bg-red-900/40 dark:text-red-300",
  CONCLUIDO: "bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300",
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

  if (loading) return <div className="p-6 text-gray-400 text-sm">Carregando...</div>;
  if (!cliente) return (
    <div className="p-6">
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow px-4 py-10 text-center text-gray-400 dark:text-gray-500 text-sm">
        Cliente não encontrado.
      </div>
    </div>
  );

  return (
    <div className="p-6">
      <div className="mb-4">
        <Link href="/clientes" className="text-sm text-gray-500 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-200 hover:underline">
          ← Clientes
        </Link>
      </div>

      <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-4 mb-6">
        <h2 className="text-xl font-bold text-gray-800 dark:text-gray-100">{cliente.nome}</h2>
        <p className="text-gray-500 dark:text-gray-400 text-sm font-mono mt-1">{cliente.telefone}</p>
        <p className="text-gray-400 dark:text-gray-500 text-xs mt-1">
          Cadastrado em {new Date(cliente.criadoEm).toLocaleDateString("pt-BR")}
        </p>
      </div>

      <h3 className="font-semibold text-gray-700 dark:text-gray-300 mb-3">
        Histórico ({historico.length} agendamentos)
      </h3>

      {historico.length === 0 && (
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow px-4 py-8 text-center text-gray-400 dark:text-gray-500 text-sm">
          Nenhum agendamento registrado.
        </div>
      )}

      {historico.length > 0 && (
        <table className="w-full bg-white dark:bg-gray-800 rounded-lg shadow text-sm">
          <thead>
            <tr className="border-b border-gray-100 dark:border-gray-700 text-left text-gray-500 dark:text-gray-400">
              <th className="px-4 py-3">Data / Hora</th>
              <th className="px-4 py-3">Serviço</th>
              <th className="px-4 py-3">Status</th>
            </tr>
          </thead>
          <tbody>
            {historico
              .sort((a, b) => b.dataHora.localeCompare(a.dataHora))
              .map((a) => (
                <tr key={a.id} className="border-b border-gray-100 dark:border-gray-700 last:border-0">
                  <td className="px-4 py-3 font-mono text-gray-700 dark:text-gray-300">
                    {new Date(a.dataHora).toLocaleString("pt-BR", {
                      day: "2-digit", month: "2-digit",
                      hour: "2-digit", minute: "2-digit",
                    })}
                  </td>
                  <td className="px-4 py-3 text-gray-800 dark:text-gray-200">{a.servico}</td>
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
