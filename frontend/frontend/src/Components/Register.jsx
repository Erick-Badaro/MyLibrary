import React, { useState } from "react";
import { BookOpen } from "lucide-react";
import { Link } from "react-router-dom";

const Register = () => {
  const [step, setStep] = useState(1);
  const [zipLoading, setZipLoading] = useState(false);
  const [zipError, setZipError] = useState("");
  
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    zipCode: "",
    street: "",
    neighborhood: "",
    city: "",
    state: "",
    number: "",
    complement: "",
  });

  const handleNext = () => {
    setStep(2);
  };

  const handleBack = () => {
    setStep(1);
  };

  const handleChange = (e) => {
    const { id, value } = e.target;
    setFormData((prevData) => ({ ...prevData, [id]: value }));
  };

    const handleZipCodeChange = async (e) => {
    const value = e.target.value;
    const zipCode = value.replace(/\D/g, "");
    setFormData((prev) => ({ ...prev, zipCode: value }));
    setZipError("");

    if (zipCode.length !== 8) return;

    setZipLoading(true);
    try {
      const response = await fetch(
        `http://localhost:8080/user/zipcode/${zipCode}`
      );
      if (!response.ok) {
        setZipError("CEP não encontrado.");
        return;
      }
      const data = await response.json();
      console.log("Resposta CEP:", data);

      setFormData((prev) => ({
        ...prev,
        zipCode: data.cep || value,
        street: data.logradouro || "",
        neighborhood: data.bairro || "",
        city: data.localidade || "",
        state: data.uf || "",
      }));
    } catch (error) {
      console.error("Erro ao buscar o CEP:", error);
      setZipError("Erro ao buscar o CEP.");
    } finally {
      setZipLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Dados do formulário:", formData);
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4 font-sans">
      <div className="bg-white w-full max-w-[400px] p-8 rounded-2xl shadow-[0_2px_10px_-3px_rgba(6,81,237,0.1)]">
        <div className="flex flex-col items-center mb-8">
          <div className="bg-indigo-50 p-3 rounded-2xl mb-4">
            <BookOpen className="w-6 h-6 text-indigo-600" />
          </div>
          <h1 className="text-2xl font-bold text-gray-900 mb-1">Criar Conta</h1>
          <p className="text-sm text-gray-500">
            {step === 1
              ? "Comece a organizar sua biblioteca"
              : "Informe seu endereço"}
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {step === 1 && (
            <>
              <div>
                <label
                  className="block text-sm font-medium text-gray-700 mb-1.5"
                  htmlFor="name"
                >
                  Nome
                </label>
                <input
                  id="name"
                  type="text"
                  placeholder="Seu nome"
                  value={formData.name}
                  onChange={handleChange}
                  className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all text-sm"
                />
              </div>

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
                  value={formData.email}
                  onChange={handleChange}
                  className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all text-sm"
                />
              </div>

              <div>
                <label
                  className="block text-sm font-medium text-gray-700 mb-1.5"
                  htmlFor="password"
                >
                  Senha
                </label>
                <input
                  id="password"
                  type="password"
                  placeholder="Mínimo 8 caracteres"
                  minLength={8}
                  value={formData.password}
                  onChange={handleChange}
                  className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all text-sm"
                />
              </div>

              <button
                type="button"
                onClick={handleNext}
                className="w-full mt-2 bg-indigo-500 hover:bg-indigo-600 text-white font-medium py-2.5 rounded-lg transition-colors duration-200"
              >
                Próximo
              </button>
            </>
          )}

          {step === 2 && (
            <>
              <div className="grid grid-cols-3 gap-4">
                <div className="col-span-1">
                  <label
                    className="block text-sm font-medium text-gray-700 mb-1.5"
                    htmlFor="zipCode"
                  >
                    CEP
                  </label>
                  <input
                    id="zipCode"
                    type="text"
                    placeholder="12345-678"
                    value={formData.zipCode}
                    onChange={handleZipCodeChange}
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg"
                  />
                  {zipLoading && (
                    <p className="text-xs text-gray-500 mt-1">Buscando CEP...</p>
                  )}
                  {zipError && (
                    <p className="text-xs text-red-500 mt-1">{zipError}</p>
                  )}
                </div>
                <div className="col-span-2">
                  <label
                    className="block text-sm font-medium text-gray-700 mb-1.5"
                    htmlFor="street"
                  >
                    Rua
                  </label>
                  <input
                    id="street"
                    type="text"
                    placeholder="Sua rua"
                    value={formData.street}
                    onChange={handleChange}
                    readOnly
                    className="w-full px-4 py-2.5 bg-gray-100 border border-gray-200 rounded-lg"
                  />
                </div>
              </div>

              <div className="grid grid-cols-3 gap-4">
                <div className="col-span-1">
                  <label
                    className="block text-sm font-medium text-gray-700 mb-1.5"
                    htmlFor="number"
                  >
                    Número
                  </label>
                  <input
                    id="number"
                    type="text"
                    placeholder="123"
                    value={formData.number}
                    onChange={handleChange}
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg"
                  />
                </div>
                <div className="col-span-2">
                  <label
                    className="block text-sm font-medium text-gray-700 mb-1.5"
                    htmlFor="complement"
                  >
                    Complemento
                  </label>
                  <input
                    id="complement"
                    type="text"
                    placeholder="Apto, bloco, etc."
                    value={formData.complement}
                    onChange={handleChange}
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg"
                  />
                </div>
              </div>

              <div>
                <label
                  className="block text-sm font-medium text-gray-700 mb-1.5"
                  htmlFor="neighborhood"
                >
                  Bairro
                </label>
                <input
                  id="neighborhood"
                  type="text"
                  placeholder="Seu bairro"
                  value={formData.neighborhood}
                  onChange={handleChange}
                  readOnly
                  className="w-full px-4 py-2.5 bg-gray-100 border border-gray-200 rounded-lg"
                />
              </div>

              <div className="grid grid-cols-3 gap-4">
                <div className="col-span-2">
                  <label
                    className="block text-sm font-medium text-gray-700 mb-1.5"
                    htmlFor="city"
                  >
                    Cidade
                  </label>
                  <input
                    id="city"
                    type="text"
                    placeholder="Sua cidade"
                    value={formData.city}
                    onChange={handleChange}
                    readOnly
                    className="w-full px-4 py-2.5 bg-gray-100 border border-gray-200 rounded-lg"
                  />
                </div>
                <div className="col-span-1">
                  <label
                    className="block text-sm font-medium text-gray-700 mb-1.5"
                    htmlFor="state"
                  >
                    Estado
                  </label>
                  <input
                    id="state"
                    type="text"
                    placeholder="UF"
                    value={formData.state}
                    onChange={handleChange}
                    readOnly
                    className="w-full px-4 py-2.5 bg-gray-100 border border-gray-200 rounded-lg"
                  />
                </div>
              </div>

              <div className="flex items-center gap-4 mt-4">
                <button
                  type="button"
                  onClick={handleBack}
                  className="w-full bg-gray-200 hover:bg-gray-300 text-gray-800 font-medium py-2.5 rounded-lg transition-colors duration-200"
                >
                  Voltar
                </button>
                <button
                  type="submit"
                  className="w-full bg-indigo-500 hover:bg-indigo-600 text-white font-medium py-2.5 rounded-lg transition-colors duration-200"
                >
                  Cadastrar
                </button>
              </div>
            </>
          )}
        </form>

        <p className="text-center text-sm text-gray-500 mt-6">
          Já tem uma conta?{" "}
          <Link
            to="/login"
            className="text-indigo-500 hover:text-indigo-600 font-medium"
          >
            Entrar
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Register;
