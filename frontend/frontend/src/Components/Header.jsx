import React, { useState } from "react";
import { BookOpen, LogOut } from "lucide-react";
import { useNavigate } from "react-router-dom";

const Header = () => {
    
    const [userName] = useState(() => {
        return localStorage.getItem("userName") || "";
    });
    
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("userName");

        navigate("/login");
    };

    return (
        <header className="w-full bg-white border-b border-gray-200 px-4 sm:px-8 py-4 flex items-center justify-between shadow-sm">
            {/* Lado Esquerdo - Logo e Título */}
            <div className="flex items-center gap-2 sm:gap-3">
                <div className="bg-indigo-50 p-2 rounded-xl hidden sm:block">
                    <BookOpen className="w-6 h-6 text-indigo-600" />
                </div>
                <h1 className="text-lg sm:text-xl font-bold text-gray-900 truncate max-w-[160px] sm:max-w-none">
                    Minha Biblioteca
                </h1>
            </div>

            {/* Lado Direito - Saudação e Botão Sair */}
            <div className="flex items-center gap-4 sm:gap-6">
                <span className="hidden sm:block text-gray-600 font-medium truncate max-w-[150px] md:max-w-none">
                    Olá, {userName || "Usuário"}
                </span>
                <button
                    onClick={handleLogout}
                    className="flex items-center gap-2 text-red-500 hover:text-red-600 transition-colors duration-200 font-medium"
                    title="Sair do sistema"
                >
                    <LogOut className="w-5 h-5 cursor-pointer" />
                    <span>Sair</span>
                </button>
            </div>
        </header>
    );
};

export default Header;