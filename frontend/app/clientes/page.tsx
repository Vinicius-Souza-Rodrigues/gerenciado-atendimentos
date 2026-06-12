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
        <h2 className="text-2xl font-bold text-gray-800">Clientes</h2>
        <p className="text-gray-500 text-sm">{clientes.length} cadastrados</p>
      </div>

      <input
        type="text"
        placeholder="Buscar por nome ou telefone..."
        value={busca}
        onChange={(e) => setBusca(e.target.value)}
        className="w-full mb-4 px-3 py-2 border rounded-lg text-sm outline-none focus:ring-2 focus:ring-gray-300"
      />

      {loading && <p className="text-gray-500">Carregando...</p>}
      {erro && <p className="text-red-500">{erro}</p>}

      {!loading && filtrados.length === 0 && (
        <p className="text-gray-500">Nenhum cliente encontrado.</p>
      )}

      {!loading && filtrados.length > 0 && (
        <table className="w-full bg-white rounded-lg shadow text-sm">
          <thead>
            <tr className="border-b text-left text-gray-500">
              <th className="px-4 py-3">Nome</th>
              <th className="px-4 py-3">Telefone</th>
              <th className="px-4 py-3">Cadastrado em</th>
              <th className="px-4 py-3"></th>
            </tr>
          </thead>
          <tbody>
            {filtrados.map((c) => (
              <tr key={c.id} className="border-b last:border-0">
                <td className="px-4 py-3 font-medium">{c.nome}</td>
                <td className="px-4 py-3 font-mono text-gray-600">{c.telefone}</td>
                <td className="px-4 py-3 text-gray-500">
                  {new Date(c.criadoEm).toLocaleDateString("pt-BR")}
                </td>
                <td className="px-4 py-3">
                  <Link
                    href={`/clientes/${c.id}`}
                    className="text-xs px-2 py-1 bg-gray-800 text-white rounded hover:bg-gray-700"
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
