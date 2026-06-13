"use client";

import { useEffect, useState } from "react";
import { api, HorarioDisponivel, Prestador } from "@/lib/api";

const DIAS = [
  { key: "MONDAY", label: "Segunda" },
  { key: "TUESDAY", label: "Terça" },
  { key: "WEDNESDAY", label: "Quarta" },
  { key: "THURSDAY", label: "Quinta" },
  { key: "FRIDAY", label: "Sexta" },
  { key: "SATURDAY", label: "Sábado" },
  { key: "SUNDAY", label: "Domingo" },
];

interface SlotForm {
  dia_semana: string;
  hora_inicio: string;
  hora_fim: string;
  ativo: boolean;
}

export default function PrestadorPage() {
  const [prestador, setPrestador] = useState<Prestador | null>(null);
  const [link, setLink] = useState<string | null>(null);
  const [horarios, setHorarios] = useState<SlotForm[]>([]);
  const [loading, setLoading] = useState(true);
  const [salvando, setSalvando] = useState(false);
  const [mensagem, setMensagem] = useState<string | null>(null);

  useEffect(() => {
    api.prestadores.listar().then(async (lista) => {
      if (lista.length === 0) { setLoading(false); return; }
      const p = lista[0];
      setPrestador(p);

      const [horariosData, linkData] = await Promise.all([
        api.prestadores.horarios.listar(p.id).catch(() => []),
        api.prestadores.link(p.id).catch(() => ({ link: "" })),
      ]);

      setLink(linkData.link);

      const slots: SlotForm[] = DIAS.map((d) => {
        const existente = (horariosData as HorarioDisponivel[]).find(
          (h) => h.diaDaSemana === d.key
        );
        return {
          dia_semana: d.key,
          hora_inicio: existente?.horaInicio ?? "08:00",
          hora_fim: existente?.horaFim ?? "18:00",
          ativo: existente?.ativo ?? false,
        };
      });
      setHorarios(slots);
      setLoading(false);
    });
  }, []);

  function atualizar(index: number, campo: keyof SlotForm, valor: string | boolean) {
    setHorarios((prev) =>
      prev.map((s, i) => (i === index ? { ...s, [campo]: valor } : s))
    );
  }

  async function salvar() {
    if (!prestador) return;
    setSalvando(true);
    setMensagem(null);
    try {
      await api.prestadores.horarios.atualizar(prestador.id, horarios);
      setMensagem("Horários salvos com sucesso!");
    } catch {
      setMensagem("Erro ao salvar horários.");
    } finally {
      setSalvando(false);
    }
  }

  if (loading) return <div className="p-6 text-gray-400 text-sm">Carregando...</div>;
  if (!prestador) return (
    <div className="p-6">
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow px-4 py-10 text-center text-gray-400 dark:text-gray-500 text-sm">
        Nenhum prestador cadastrado.
      </div>
    </div>
  );

  return (
    <div className="p-6 max-w-2xl">
      <h2 className="text-2xl font-bold text-gray-800 dark:text-gray-100 mb-6">Prestador</h2>

      <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-4 mb-6">
        <h3 className="font-semibold text-gray-700 dark:text-gray-300 mb-2">Dados</h3>
        <p className="text-gray-800 dark:text-gray-100 font-medium">{prestador.nomeNegocio}</p>
        <p className="text-gray-500 dark:text-gray-400 text-sm font-mono">{prestador.telefoneWhatsApp}</p>
      </div>

      {link && (
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-4 mb-6">
          <h3 className="font-semibold text-gray-700 dark:text-gray-300 mb-2">Link do Telegram</h3>
          <p className="text-xs text-gray-500 dark:text-gray-400 mb-2">Compartilhe com seus clientes</p>
          <a
            href={link}
            target="_blank"
            rel="noopener noreferrer"
            className="text-blue-600 dark:text-blue-400 text-sm break-all hover:underline"
          >
            {link}
          </a>
        </div>
      )}

      <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-4">
        <h3 className="font-semibold text-gray-700 dark:text-gray-300 mb-4">Horários de Atendimento</h3>
        <div className="space-y-3">
          {DIAS.map((dia, i) => (
            <div key={dia.key} className="flex items-center gap-3 text-sm">
              <input
                type="checkbox"
                checked={horarios[i]?.ativo ?? false}
                onChange={(e) => atualizar(i, "ativo", e.target.checked)}
                className="w-4 h-4 accent-gray-700 dark:accent-gray-400"
              />
              <span className="w-20 text-gray-700 dark:text-gray-300">{dia.label}</span>
              <input
                type="time"
                value={horarios[i]?.hora_inicio ?? "08:00"}
                onChange={(e) => atualizar(i, "hora_inicio", e.target.value)}
                disabled={!horarios[i]?.ativo}
                className="border border-gray-200 dark:border-gray-600 rounded px-2 py-1 bg-white dark:bg-gray-700 text-gray-800 dark:text-gray-200 disabled:opacity-40"
              />
              <span className="text-gray-400 dark:text-gray-500">até</span>
              <input
                type="time"
                value={horarios[i]?.hora_fim ?? "18:00"}
                onChange={(e) => atualizar(i, "hora_fim", e.target.value)}
                disabled={!horarios[i]?.ativo}
                className="border border-gray-200 dark:border-gray-600 rounded px-2 py-1 bg-white dark:bg-gray-700 text-gray-800 dark:text-gray-200 disabled:opacity-40"
              />
            </div>
          ))}
        </div>

        <div className="mt-4 flex items-center gap-3">
          <button
            onClick={salvar}
            disabled={salvando}
            className="px-4 py-2 bg-gray-800 dark:bg-gray-600 text-white text-sm rounded hover:bg-gray-700 dark:hover:bg-gray-500 disabled:opacity-50"
          >
            {salvando ? "Salvando..." : "Salvar horários"}
          </button>
          {mensagem && (
            <span className={`text-sm ${
              mensagem.includes("Erro")
                ? "text-red-600 dark:text-red-400"
                : "text-green-600 dark:text-green-400"
            }`}>
              {mensagem}
            </span>
          )}
        </div>
      </div>
    </div>
  );
}
