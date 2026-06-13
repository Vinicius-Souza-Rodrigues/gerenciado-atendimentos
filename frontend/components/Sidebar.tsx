"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useEffect, useState } from "react";

const links = [
  { href: "/agenda", label: "Agenda" },
  { href: "/clientes", label: "Clientes" },
  { href: "/prestador", label: "Prestador" },
];

export default function Sidebar() {
  const pathname = usePathname();
  const [dark, setDark] = useState(false);

  useEffect(() => {
    const saved = localStorage.getItem("theme");
    if (saved === "dark") {
      document.documentElement.classList.add("dark");
      setDark(true);
    }
  }, []);

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

  return (
    <aside className="w-56 min-h-screen bg-gray-900 dark:bg-gray-950 text-white flex flex-col border-r border-gray-800">
      <div className="p-4 border-b border-gray-800">
        <h1 className="font-bold text-base text-white">Agendamentos</h1>
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
      <div className="p-3 border-t border-gray-800">
        <button
          onClick={toggleTheme}
          className="w-full flex items-center justify-between px-3 py-2 rounded text-sm text-gray-400 hover:bg-gray-800 hover:text-white transition-colors"
        >
          <span>{dark ? "Modo claro" : "Modo escuro"}</span>
          <span className="text-base">{dark ? "☀" : "☾"}</span>
        </button>
      </div>
    </aside>
  );
}
