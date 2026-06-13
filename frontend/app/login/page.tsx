"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { getToken, salvarSessao } from "@/lib/auth";

type Modo = "login" | "registro";

export default function LoginPage() {
  const router = useRouter();
  const [modo, setModo] = useState<Modo>("login");
  const [nomeNegocio, setNomeNegocio] = useState("");
  const [telefone, setTelefone] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState<string | null>(null);
  const [carregando, setCarregando] = useState(false);

  useEffect(() => {
    if (getToken()) router.replace("/agenda");
  }, [router]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErro(null);
    setCarregando(true);
    try {
      const res = modo === "login"
        ? await api.auth.login(nomeNegocio, senha)
        : await api.auth.registro(nomeNegocio, telefone, senha);
      salvarSessao(res.token, res.prestadorId, res.nomeNegocio);
      router.replace("/agenda");
    } catch (err: unknown) {
      const status = err instanceof Error && err.message.includes("409");
      if (status) {
        setErro("Estabelecimento já cadastrado");
      } else {
        setErro(modo === "login" ? "Nome ou senha incorretos" : "Erro ao criar conta");
      }
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
          {modo === "login" ? "Acesse o painel do seu estabelecimento" : "Crie sua conta"}
        </p>

        <form
          onSubmit={handleSubmit}
          className="bg-white dark:bg-gray-800 rounded-lg shadow p-6 space-y-4"
        >
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Nome do estabelecimento
            </label>
            <input
              type="text"
              value={nomeNegocio}
              onChange={(e) => setNomeNegocio(e.target.value)}
              placeholder="Ex: Barbearia do João"
              required
              className="w-full border border-gray-200 dark:border-gray-600 rounded-lg px-3 py-2 text-sm bg-white dark:bg-gray-700 text-gray-800 dark:text-gray-200 placeholder-gray-400 dark:placeholder-gray-500 outline-none focus:ring-2 focus:ring-gray-300 dark:focus:ring-gray-500"
            />
          </div>

          {modo === "registro" && (
            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                Telefone / WhatsApp
              </label>
              <input
                type="text"
                value={telefone}
                onChange={(e) => setTelefone(e.target.value)}
                placeholder="11999999999"
                required
                className="w-full border border-gray-200 dark:border-gray-600 rounded-lg px-3 py-2 text-sm bg-white dark:bg-gray-700 text-gray-800 dark:text-gray-200 placeholder-gray-400 dark:placeholder-gray-500 outline-none focus:ring-2 focus:ring-gray-300 dark:focus:ring-gray-500"
              />
            </div>
          )}

          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Senha
            </label>
            <input
              type="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              placeholder="••••••••"
              required
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
            {carregando ? "Aguarde..." : modo === "login" ? "Entrar" : "Criar conta"}
          </button>

          <p className="text-center text-sm text-gray-500 dark:text-gray-400">
            {modo === "login" ? (
              <>Não tem conta?{" "}
                <button type="button" onClick={() => { setModo("registro"); setErro(null); }}
                  className="text-gray-800 dark:text-gray-200 font-medium hover:underline">
                  Cadastre-se
                </button>
              </>
            ) : (
              <>Já tem conta?{" "}
                <button type="button" onClick={() => { setModo("login"); setErro(null); }}
                  className="text-gray-800 dark:text-gray-200 font-medium hover:underline">
                  Entrar
                </button>
              </>
            )}
          </p>
        </form>
      </div>
    </div>
  );
}
