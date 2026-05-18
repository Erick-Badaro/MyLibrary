import React, { useState } from "react";
import { BookOpen, CheckCircle2 } from "lucide-react";
import { Link, useNavigate } from "react-router-dom";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await fetch("http://localhost:8080/user/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      if (response.ok) {
        const data = await response.json();
        console.log("Resposta do Backend:", data);
        localStorage.setItem("token", data.token);
        localStorage.setItem("userName", data.name);
        setSuccessMessage("Login realizado com sucesso! Entrando...");
        setTimeout(() => {
          navigate("/");
        }, 1500);
      } else {
        setError("E-mail ou senha inválidos.");
      }
    } catch (err) {
      console.error("Erro ao fazer login:", err);
      setError("Erro ao tentar fazer login. Tente novamente.");
    } finally {
      setLoading(false);
    }
  };

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

        <form onSubmit={handleSubmit} className="space-y-4">
          {successMessage && (
            <div className="p-4 mb-4 text-sm text-emerald-700 bg-emerald-50 rounded-xl flex items-center gap-2 border border-emerald-100 transition-all">
              <CheckCircle2 className="w-5 h-5 text-emerald-500 flex-shrink-0" />
              <span className="font-medium">{successMessage}</span>
            </div>
          )}

          {error && !successMessage && (
            <div className="p-3 text-sm text-red-500 bg-red-50 rounded-lg">
              {error}
            </div>
          )}

           {!successMessage && (
            <>
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
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="seu@email.com"
              required
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
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Sua senha"
              required
              className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all text-sm"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full mt-2 bg-indigo-500 hover:bg-indigo-600 disabled:bg-indigo-300 text-white font-medium py-2.5 rounded-lg transition-colors duration-200"
          >
            {loading ? "Entrando..." : "Entrar"}
          </button>
          </>
           )}
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