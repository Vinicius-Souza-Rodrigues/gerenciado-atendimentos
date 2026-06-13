"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { api, Cliente } from "@/lib/api";

export default function ClientesPage() {
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [busca, setBusca] = useState("");
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    api.clientes
      .listar()
      .then(setClientes)
      .catch(() => setErro("Erro ao carregar clientes"))
      .finally(() => setLoading(false));
  }, []);

  const filtrados = clientes.filter(
    (c) =>
      c.nome.toLowerCase().includes(busca.toLowerCase()) ||
      c.telefone.includes(busca)
  );

  return (
    <div className="p-6">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-gray-800 dark:text-gray-100">Clientes</h2>
        <p className="text-gray-500 dark:text-gray-400 text-sm">{clientes.length} cadastrados</p>
      </div>

      <input
        type="text"
        placeholder="Buscar por nome ou telefone..."
        value={busca}
        onChange={(e) => setBusca(e.target.value)}
        className="w-full mb-4 px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg text-sm bg-white dark:bg-gray-800 text-gray-800 dark:text-gray-200 placeholder-gray-400 dark:placeholder-gray-500 outline-none focus:ring-2 focus:ring-gray-300 dark:focus:ring-gray-600"
      />

      {loading && <p className="text-gray-400 text-sm">Carregando...</p>}
      {erro && (
        <div className="bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800 text-red-700 dark:text-red-300 text-sm px-4 py-3 rounded-lg">
          {erro}
        </div>
      )}

      {!loading && !erro && filtrados.length === 0 && (
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow px-4 py-10 text-center text-gray-400 dark:text-gray-500 text-sm">
          {busca ? `Nenhum resultado para "${busca}"` : "Nenhum cliente cadastrado ainda."}
        </div>
      )}

      {!loading && filtrados.length > 0 && (
        <table className="w-full bg-white dark:bg-gray-800 rounded-lg shadow text-sm">
          <thead>
            <tr className="border-b border-gray-100 dark:border-gray-700 text-left text-gray-500 dark:text-gray-400">
              <th className="px-4 py-3">Nome</th>
              <th className="px-4 py-3">Telefone</th>
              <th className="px-4 py-3">Cadastrado em</th>
              <th className="px-4 py-3"></th>
            </tr>
          </thead>
          <tbody>
            {filtrados.map((c) => (
              <tr key={c.id} className="border-b border-gray-100 dark:border-gray-700 last:border-0 hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors">
                <td className="px-4 py-3 font-medium text-gray-800 dark:text-gray-200">{c.nome}</td>
                <td className="px-4 py-3 font-mono text-gray-600 dark:text-gray-400">{c.telefone}</td>
                <td className="px-4 py-3 text-gray-500 dark:text-gray-400">
                  {new Date(c.criadoEm).toLocaleDateString("pt-BR")}
                </td>
                <td className="px-4 py-3">
                  <Link
                    href={`/clientes/${c.id}`}
                    className="text-xs px-2 py-1 bg-gray-800 dark:bg-gray-600 text-white rounded hover:bg-gray-700 dark:hover:bg-gray-500"
                  >
                    Histórico
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
