import React, { useState, useEffect } from "react";
import Header from "./Header";
import { Search, BookOpen, Plus, X, CheckCircle2, Pencil, Trash } from "lucide-react";
import { useNavigate } from "react-router-dom";

const Home = () => {
  const [books, setBooks] = useState([]);
  const [filter, setFilter] = useState("ALL");
  const [search, setSearch] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isModalUpdateOpen, setIsModalUpdateOpen] = useState(false);
  const [toast, setToast] = useState({ show: false, message: "" });
  const [isAdding, setIsAdding] = useState(false);
  const navigate = useNavigate();

  const [newBook, setNewBook] = useState({
    title: "",
    author: "",
    genre: "",
    publisher: "",
    status: "WANNA_READ",
  });
  const [editingBook, setEditingBook] = useState(null);

  const fetchBooks = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    try {
      let url = "http://localhost:8080/books";
      if (filter !== "ALL") {
        url += `?status=${filter}`;
      }

      const response = await fetch(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setBooks(data);
      } else if (response.status === 401) {
        localStorage.removeItem("token");
        navigate("/login");
      }
    } catch (error) {
      console.error("Erro ao buscar livros:", error);
    }
  };

  useEffect(() => {
    fetchBooks();
  }, [filter]);

  const handleAddBook = async (e) => {
    e.preventDefault();
    setIsAdding(true);
    const token = localStorage.getItem("token");

    try {
      const response = await fetch("http://localhost:8080/books", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(newBook),
      });

      if (response.status === 201) {
        setIsModalOpen(false);
        setNewBook({
          title: "",
          author: "",
          genre: "",
          publisher: "",
          status: "WANNA_READ",
        });
        showToast("Livro adicionado com sucesso!");
        fetchBooks();
      } else {
        alert("Erro ao adicionar livro.");
      }
    } catch (error) {
      console.error("Erro:", error);
    } finally {
      setIsAdding(false);
    }
  };

  const handleUpdateBook = async (e) => {
    e.preventDefault();
    setIsAdding(true);
    const token = localStorage.getItem("token");
    const book = {
      title: editingBook.title,
      author: editingBook.author,
      genre: editingBook.genre,
      publisher: editingBook.publisher,
      status: editingBook.status,
    };
    try {
      const response = await fetch(`http://localhost:8080/books/${editingBook.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(book),
      });

      if (response.status === 200) {
        setIsModalUpdateOpen(false);
        setEditingBook(null);
        showToast("Livro atualizado com sucesso!");
        fetchBooks();
      } else {
        alert("Erro ao atualizar livro.");
      }
    } catch (error) {
      console.error("Erro:", error);
    } finally {
      setIsAdding(false);
    }
  };

  const handleDeleteBook = async (book) => {
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(`http://localhost:8080/books/${book.id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        }
      });

      if (response.status === 200 || response.status === 204) {
        showToast("Livro apagado com sucesso!");
        fetchBooks();
      } else {
        alert("Erro ao apagar livro.");
      }
    } catch (error) {
      console.error("Erro:", error);
    }
  };

  const showToast = (message) => {
    setToast({ show: true, message });
    setTimeout(() => {
      setToast({ show: false, message: "" });
    }, 3000);
  };

  const displayedBooks = books.filter((book) =>
    book.title.toLowerCase().includes(search.toLowerCase()),
  );

  return (
    <div className="min-h-screen bg-[#f8f9fc] font-sans">
      <Header />

      {toast.show && (
        <div className="fixed top-20 right-8 z-50 bg-emerald-500 text-white px-4 py-3 rounded-lg shadow-lg flex items-center gap-2 animate-in fade-in slide-in-from-top-4">
          <CheckCircle2 className="w-5 h-5" />
          <span className="font-medium">{toast.message}</span>
        </div>
      )}

      <main className="p-8 max-w-7xl mx-auto">

        <div className="flex items-center justify-between mb-12">

          <div className="relative w-80">
            <Search className="w-5 h-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
            <input
              type="text"
              placeholder="Buscar por título..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-10 pr-4 py-2 bg-white border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-shadow text-sm text-gray-700"
            />
          </div>


          <div className="flex items-center gap-2">
            {[
              { id: "ALL", label: "Todos" },
              { id: "WANNA_READ", label: "Quero Ler" },
              { id: "READING", label: "Lendo" },
              { id: "READ", label: "Lido" },
            ].map((f) => (
              <button
                key={f.id}
                onClick={() => setFilter(f.id)}
                className={`px-4 py-1.5 rounded-full text-sm font-medium transition-colors ${filter === f.id
                  ? "bg-indigo-500 text-white"
                  : "bg-gray-200 text-gray-600 hover:bg-gray-300"
                  }`}
              >
                {f.label}
              </button>
            ))}
          </div>
        </div>


        {displayedBooks.length === 0 ? (
          <div className="flex flex-col items-center justify-center mt-20">
            <div className="bg-gray-100 p-4 rounded-2xl mb-4 text-gray-400">
              <BookOpen className="w-10 h-10" />
            </div>
            <h2 className="text-xl font-semibold text-gray-800 mb-2">
              Sua biblioteca está vazia
            </h2>
            <p className="text-gray-500 text-sm text-center mb-6 max-w-xs">
              Adicione seu primeiro livro para começar a organizar suas leituras.
            </p>
            <button
              onClick={() => setIsModalOpen(true)}
              className="flex items-center gap-2 bg-indigo-500 hover:bg-indigo-600 text-white font-medium px-5 py-2.5 rounded-lg transition-colors"
            >
              <Plus className="w-4 h-4" />
              Adicionar Livro
            </button>
          </div>
        ) : (
          <div>
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold text-gray-800">Meus Livros</h2>
              <button
                onClick={() => setIsModalOpen(true)}
                className="flex items-center gap-2 bg-indigo-500 hover:bg-indigo-600 text-white font-medium px-4 py-2 rounded-lg transition-colors text-sm"
              >
                <Plus className="w-4 h-4" />
                Adicionar
              </button>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              {displayedBooks.map((book) => (
                <div key={book.id} className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm hover:shadow-md transition-shadow">
                  <h3 className="font-bold text-gray-900 mb-1 truncate">{book.title}</h3>
                  <p className="text-sm text-gray-500 mb-3 truncate">{book.author}</p>
                  <div>
                    <button onClick={() => {
                      setEditingBook(book);
                      setIsModalUpdateOpen(true);
                    }}>
                      <Pencil color="yellow" />
                    </button>
                    <button onClick={() => {
                      handleDeleteBook(book);
                    }}>
                      <Trash color="red" />
                    </button>
                  </div>
                  <div className="flex items-center justify-between mt-auto">
                    <span className="text-xs bg-gray-100 px-2 py-1 rounded-md text-gray-600 truncate max-w-[60%]">
                      {book.genre}
                    </span>
                    <span className="text-xs font-medium text-indigo-500">
                      {book.status === 'WANNA_READ' ? 'Quero Ler' : book.status === 'READING' ? 'Lendo' : 'Lido'}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </main>


      {isModalOpen && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl w-full max-w-[450px] shadow-xl overflow-hidden">
            <div className="flex justify-between items-center p-6 border-b border-gray-100">
              <h3 className="font-bold text-lg text-gray-900">Adicionar Livro</h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleAddBook} className="p-6 space-y-4">
              <div>
                <label className="block text-sm text-gray-600 mb-1.5" htmlFor="title">Título</label>
                <input
                  id="title"
                  type="text"
                  placeholder="Nome do livro"
                  required
                  value={newBook.title}
                  onChange={(e) => setNewBook({ ...newBook, title: e.target.value })}
                  className="w-full px-4 py-2.5 bg-white border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label className="block text-sm text-gray-600 mb-1.5" htmlFor="author">Autor</label>
                <input
                  id="author"
                  type="text"
                  placeholder="Nome do autor"
                  required
                  value={newBook.author}
                  onChange={(e) => setNewBook({ ...newBook, author: e.target.value })}
                  className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:bg-white focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm text-gray-600 mb-1.5" htmlFor="genre">Gênero</label>
                  <input
                    id="genre"
                    type="text"
                    placeholder="Ex: Ficção"
                    required
                    value={newBook.genre}
                    onChange={(e) => setNewBook({ ...newBook, genre: e.target.value })}
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:bg-white focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-sm text-gray-600 mb-1.5" htmlFor="publisher">Editora</label>
                  <input
                    id="publisher"
                    type="text"
                    placeholder="Alta Books"
                    required
                    value={newBook.publisher}
                    onChange={(e) => setNewBook({ ...newBook, publisher: e.target.value })}
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:bg-white focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm text-gray-600 mb-1.5" htmlFor="status">Status de Leitura</label>
                <div className="relative">
                  <select
                    id="status"
                    value={newBook.status}
                    onChange={(e) => setNewBook({ ...newBook, status: e.target.value })}
                    className="w-full px-4 py-2.5 bg-white border border-gray-200 rounded-lg appearance-none focus:outline-none focus:ring-2 focus:ring-indigo-500 text-gray-700"
                  >
                    <option value="WANNA_READ">Quero Ler</option>
                    <option value="READING">Lendo</option>
                    <option value="READ">Lido</option>
                  </select>
                  <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-4 text-gray-500">
                    <svg className="w-4 h-4 fill-current" viewBox="0 0 20 20">
                      <path d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" />
                    </svg>
                  </div>
                </div>
              </div>

              <button
                type="submit"
                disabled={isAdding}
                className="w-full mt-4 bg-indigo-500 hover:bg-indigo-600 disabled:bg-indigo-300 text-white font-medium py-2.5 rounded-lg transition-colors"
              >
                {isAdding ? "Adicionando..." : "Adicionar"}
              </button>
            </form>
          </div>
        </div>
      )}
      {isModalUpdateOpen && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl w-full max-w-[450px] shadow-xl overflow-hidden">
            <div className="flex justify-between items-center p-6 border-b border-gray-100">
              <h3 className="font-bold text-lg text-gray-900">Atualizar Livro</h3>
              <button
                onClick={() => setIsModalUpdateOpen(false)}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleUpdateBook} className="p-6 space-y-4">
              <div>
                <label className="block text-sm text-gray-600 mb-1.5" htmlFor="title">Título</label>
                <input
                  id="title"
                  type="text"
                  placeholder="Nome do livro"
                  required
                  value={editingBook.title}
                  onChange={(e) => setEditingBook({ ...editingBook, title: e.target.value })}
                  className="w-full px-4 py-2.5 bg-white border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label className="block text-sm text-gray-600 mb-1.5" htmlFor="author">Autor</label>
                <input
                  id="author"
                  type="text"
                  placeholder="Nome do autor"
                  required
                  value={editingBook.author}
                  onChange={(e) => setEditingBook({ ...editingBook, author: e.target.value })}
                  className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:bg-white focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm text-gray-600 mb-1.5" htmlFor="genre">Gênero</label>
                  <input
                    id="genre"
                    type="text"
                    placeholder="Ex: Ficção"
                    required
                    value={editingBook.genre}
                    onChange={(e) => setEditingBook({ ...editingBook, genre: e.target.value })}
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:bg-white focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-sm text-gray-600 mb-1.5" htmlFor="publisher">Editora</label>
                  <input
                    id="publisher"
                    type="text"
                    placeholder="Alta Books"
                    required
                    value={editingBook.publisher}
                    onChange={(e) => setEditingBook({ ...editingBook, publisher: e.target.value })}
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:bg-white focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm text-gray-600 mb-1.5" htmlFor="status">Status de Leitura</label>
                <div className="relative">
                  <select
                    id="status"
                    value={editingBook.status}
                    onChange={(e) => setEditingBook({ ...editingBook, status: e.target.value })}
                    className="w-full px-4 py-2.5 bg-white border border-gray-200 rounded-lg appearance-none focus:outline-none focus:ring-2 focus:ring-indigo-500 text-gray-700"
                  >
                    <option value="WANNA_READ">Quero Ler</option>
                    <option value="READING">Lendo</option>
                    <option value="READ">Lido</option>
                  </select>
                  <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-4 text-gray-500">
                    <svg className="w-4 h-4 fill-current" viewBox="0 0 20 20">
                      <path d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" />
                    </svg>
                  </div>
                </div>
              </div>

              <button
                type="submit"
                disabled={isAdding}
                className="w-full mt-4 bg-indigo-500 hover:bg-indigo-600 disabled:bg-indigo-300 text-white font-medium py-2.5 rounded-lg transition-colors"
              >
                {isAdding ? "Atualizando..." : "Atualizar"}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Home;
