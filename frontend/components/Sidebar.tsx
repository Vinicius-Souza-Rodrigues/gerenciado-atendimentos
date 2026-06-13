"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { getNomeNegocio, getToken, limparSessao } from "@/lib/auth";

const links = [
  { href: "/agenda", label: "Agenda" },
  { href: "/clientes", label: "Clientes" },
  { href: "/prestador", label: "Prestador" },
];

export default function Sidebar() {
  const pathname = usePathname();
  const router = useRouter();
  const [dark, setDark] = useState(false);
  const [nomeNegocio, setNomeNegocio] = useState<string | null>(null);

  useEffect(() => {
    const savedTheme = localStorage.getItem("theme");
    if (savedTheme === "dark") {
      document.documentElement.classList.add("dark");
      setDark(true);
    }
    setNomeNegocio(getNomeNegocio());

    if (!getToken() && pathname !== "/login") {
      router.replace("/login");
    }
  }, [pathname, router]);

  function toggleTheme() {
    const next = !dark;
    setDark(next);
    if (next) {
      document.documentElement.classList.add("dark");
      localStorage.setItem("theme", "dark");
    } else {
      document.documentElement.classList.remove("dark");
      localStorage.setItem("theme", "light");
    }
  }

  function logout() {
    limparSessao();
    router.replace("/login");
  }

  if (pathname === "/login") return null;

  return (
    <aside className="w-56 min-h-screen bg-gray-900 dark:bg-gray-950 text-white flex flex-col border-r border-gray-800">
      <div className="p-4 border-b border-gray-800">
        <h1 className="font-bold text-base text-white">Agendamentos</h1>
        {nomeNegocio && (
          <p className="text-xs text-gray-400 mt-0.5 truncate">{nomeNegocio}</p>
        )}
      </div>
      <nav className="flex-1 p-2">
        {links.map((link) => (
          <Link
            key={link.href}
            href={link.href}
            className={`block px-3 py-2 rounded mb-1 text-sm transition-colors ${
              pathname.startsWith(link.href)
                ? "bg-gray-700 text-white"
                : "text-gray-400 hover:bg-gray-800 hover:text-white"
            }`}
          >
            {link.label}
          </Link>
        ))}
      </nav>
      <div className="p-3 border-t border-gray-800 space-y-1">
        <button
          onClick={toggleTheme}
          className="w-full flex items-center justify-between px-3 py-2 rounded text-sm text-gray-400 hover:bg-gray-800 hover:text-white transition-colors"
        >
          <span>{dark ? "Modo claro" : "Modo escuro"}</span>
          <span className="text-base">{dark ? "☀" : "☾"}</span>
        </button>
        <button
          onClick={logout}
          className="w-full flex items-center px-3 py-2 rounded text-sm text-gray-400 hover:bg-gray-800 hover:text-red-400 transition-colors"
        >
          Sair
        </button>
      </div>
    </aside>
  );
}
