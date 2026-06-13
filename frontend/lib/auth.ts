const KEY_TOKEN = "authToken";
const KEY_PRESTADOR_ID = "prestadorId";
const KEY_NOME = "nomeNegocio";

export function getToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(KEY_TOKEN);
}

export function salvarSessao(token: string, prestadorId: string, nomeNegocio: string) {
  localStorage.setItem(KEY_TOKEN, token);
  localStorage.setItem(KEY_PRESTADOR_ID, prestadorId);
  localStorage.setItem(KEY_NOME, nomeNegocio);
}

export function limparSessao() {
  localStorage.removeItem(KEY_TOKEN);
  localStorage.removeItem(KEY_PRESTADOR_ID);
  localStorage.removeItem(KEY_NOME);
}

export function getNomeNegocio(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(KEY_NOME);
}
