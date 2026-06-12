const BASE = "/backend";

export interface Agendamento {
  id: string;
  clienteId: string;
  prestadorId: string;
  dataHora: string;
  servico: string;
  status: "PENDENTE" | "CONFIRMADO" | "CANCELADO" | "CONCLUIDO";
  criadoEm: string;
  atualizadoEm: string;
}

export interface Cliente {
  id: string;
  nome: string;
  telefone: string;
  criadoEm: string;
}

export interface Prestador {
  id: string;
  nomeNegocio: string;
  telefoneWhatsApp: string;
  criadoEm: string;
}

export interface HorarioDisponivel {
  id: string;
  diaDaSemana: string;
  horaInicio: string;
  horaFim: string;
  ativo: boolean;
}

async function get<T>(path: string): Promise<T> {
  const res = await fetch(`${BASE}${path}`);
  if (!res.ok) throw new Error(`Erro ${res.status}: ${path}`);
  return res.json();
}

async function put<T>(path: string, body: unknown): Promise<T> {
  const res = await fetch(`${BASE}${path}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  if (!res.ok) throw new Error(`Erro ${res.status}: ${path}`);
  return res.json();
}

async function patch<T>(path: string, body: unknown): Promise<T> {
  const res = await fetch(`${BASE}${path}`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  if (!res.ok) throw new Error(`Erro ${res.status}: ${path}`);
  return res.json();
}

export const api = {
  agendamentos: {
    listar: () => get<Agendamento[]>("/api/agendamentos"),
    atualizarStatus: (id: string, status: string) =>
      patch<Agendamento>(`/api/agendamentos/${id}/status`, { status }),
  },
  clientes: {
    listar: () => get<Cliente[]>("/api/clientes"),
    historico: (id: string) => get<Agendamento[]>(`/api/clientes/${id}/historico`),
  },
  prestadores: {
    listar: () => get<Prestador[]>("/api/prestadores"),
    buscar: (id: string) => get<Prestador>(`/api/prestadores/${id}`),
    link: (id: string) => get<{ link: string }>(`/api/prestadores/${id}/link`),
    horarios: {
      listar: (id: string) => get<HorarioDisponivel[]>(`/api/prestadores/${id}/horarios`),
      atualizar: (id: string, horarios: unknown[]) =>
        put<HorarioDisponivel[]>(`/api/prestadores/${id}/horarios`, { horarios }),
    },
  },
};
