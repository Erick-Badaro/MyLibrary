import React from "react";
import Header from "./Header";

const Home = () => {
  return (
    <div className="min-h-screen bg-gray-50 font-sans">
      <Header />
      <main className="p-8 max-w-7xl mx-auto">
        <h2 className="text-2xl font-bold text-gray-800 mb-4">
          Meus Livros
        </h2>
        <p className="text-gray-500">Sua biblioteca está vazia no momento.</p>
      </main>
    </div>
  );
};

export default Home;