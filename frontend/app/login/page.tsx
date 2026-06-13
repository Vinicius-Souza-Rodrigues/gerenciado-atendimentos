"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { api, Prestador } from "@/lib/api";
import { getToken, salvarSessao } from "@/lib/auth";

export default function LoginPage() {
  const router = useRouter();
  const [prestadores, setPrestadores] = useState<Prestador[]>([]);
  const [prestadorId, setPrestadorId] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState<string | null>(null);
  const [carregando, setCarregando] = useState(false);

  useEffect(() => {
    if (getToken()) {
      router.replace("/agenda");
      return;
    }
    api.prestadores.listar().then(setPrestadores).catch(() => {});
  }, [router]);

  async function handleLogin(e: React.FormEvent) {
    e.preventDefault();
    if (!prestadorId) { setErro("Selecione o estabelecimento"); return; }
    setCarregando(true);
    setErro(null);
    try {
      const res = await api.auth.login(prestadorId, senha);
      salvarSessao(res.token, res.prestadorId, res.nomeNegocio);
      router.replace("/agenda");
    } catch {
      setErro("Usuário ou senha incorretos");
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 dark:bg-gray-900 px-4">
      <div className="w-full max-w-sm">
        <h1 className="text-2xl font-bold text-gray-800 dark:text-gray-100 text-center mb-2">
          Agendamentos
        </h1>
        <p className="text-gray-500 dark:text-gray-400 text-sm text-center mb-8">
          Acesse o painel do seu estabelecimento
        </p>

        <form
          onSubmit={handleLogin}
          className="bg-white dark:bg-gray-800 rounded-lg shadow p-6 space-y-4"
        >
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Estabelecimento
            </label>
            <select
              value={prestadorId}
              onChange={(e) => setPrestadorId(e.target.value)}
              className="w-full border border-gray-200 dark:border-gray-600 rounded-lg px-3 py-2 text-sm bg-white dark:bg-gray-700 text-gray-800 dark:text-gray-200 outline-none focus:ring-2 focus:ring-gray-300 dark:focus:ring-gray-500"
            >
              <option value="">Selecione...</option>
              {prestadores.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.nomeNegocio}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Senha
            </label>
            <input
              type="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              placeholder="••••••••"
              className="w-full border border-gray-200 dark:border-gray-600 rounded-lg px-3 py-2 text-sm bg-white dark:bg-gray-700 text-gray-800 dark:text-gray-200 placeholder-gray-400 dark:placeholder-gray-500 outline-none focus:ring-2 focus:ring-gray-300 dark:focus:ring-gray-500"
            />
          </div>

          {erro && (
            <p className="text-red-600 dark:text-red-400 text-sm">{erro}</p>
          )}

          <button
            type="submit"
            disabled={carregando}
            className="w-full py-2 bg-gray-800 dark:bg-gray-600 text-white text-sm font-medium rounded-lg hover:bg-gray-700 dark:hover:bg-gray-500 disabled:opacity-50 transition-colors"
          >
            {carregando ? "Entrando..." : "Entrar"}
          </button>
        </form>
      </div>
    </div>
  );
}
