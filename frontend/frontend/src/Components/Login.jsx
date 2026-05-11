import React from "react";
import { BookOpen } from "lucide-react";
import { Link } from "react-router-dom";

const Login = () => {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4 font-sans">
      <div className="bg-white w-full max-w-[400px] p-8 rounded-2xl shadow-[0_2px_10px_-3px_rgba(6,81,237,0.1)]">
        <div className="flex flex-col items-center mb-8">
          <div className="bg-indigo-50 p-3 rounded-2xl mb-4">
            <BookOpen className="w-6 h-6 text-indigo-600" />
          </div>
          <h1 className="text-2xl font-bold text-gray-900 mb-1">
            Entrar na Conta
          </h1>
          <p className="text-sm text-gray-500">Bem-vindo de volta!</p>
        </div>

        <form className="space-y-4">
          <div>
            <label
              className="block text-sm font-medium text-gray-700 mb-1.5"
              htmlFor="email"
            >
              E-mail
            </label>
            <input
              id="email"
              type="email"
              placeholder="seu@email.com"
              className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all text-sm"
            />
          </div>

          <div>
            <label
              className="block text-sm font-medium text-gray-700 mb-1.5"
              htmlFor="senha"
            >
              Senha
            </label>
            <input
              id="senha"
              type="password"
              placeholder="Sua senha"
              className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all text-sm"
            />
          </div>

          <button
            type="submit"
            className="w-full mt-2 bg-indigo-500 hover:bg-indigo-600 text-white font-medium py-2.5 rounded-lg transition-colors duration-200"
          >
            Entrar
          </button>
        </form>

        <p className="text-center text-sm text-gray-500 mt-6">
          Não tem uma conta?{" "}
          <Link
            to="/register"
            className="text-indigo-500 hover:text-indigo-600 font-medium"
          >
            Cadastre-se
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Login;