import React, { useState, useEffect } from "react";
import Header from "./Header";
import {
  Search,
  BookOpen,
  Plus,
  X,
  CheckCircle2,
  Pencil,
  Trash2,
  AlertTriangle,
} from "lucide-react";
import { useNavigate } from "react-router-dom";

const Home = () => {
  const [books, setBooks] = useState([]);
  const [filter, setFilter] = useState("ALL");
  const [search, setSearch] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isModalUpdateOpen, setIsModalUpdateOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [bookToDelete, setBookToDelete] = useState(null);
  const [toast, setToast] = useState({ show: false, message: "" });
  const [isAdding, setIsAdding] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
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
      const response = await fetch(
        `http://localhost:8080/books/${editingBook.id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(book),
        },
      );

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

  const confirmDeleteBook = (book) => {
    setBookToDelete(book);
    setIsDeleteModalOpen(true);
  };

  const handleDeleteBook = async () => {
    if (!bookToDelete) return;
    setIsDeleting(true);
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `http://localhost:8080/books/${bookToDelete.id}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );

      if (response.status === 200 || response.status === 204) {
        setIsDeleteModalOpen(false);
        setBookToDelete(null);
        showToast("Livro removido com sucesso!");
        fetchBooks();
      } else {
        alert("Erro ao apagar livro.");
      }
    } catch (error) {
      console.error("Erro:", error);
    } finally {
      setIsDeleting(false);
    }
  };

  const showToast = (message) => {
    setToast({ show: true, message });
    setTimeout(() => setToast({ show: false, message: "" }), 3000);
  };

  const statusLabel = (status) => {
    if (status === "WANNA_READ") return "Quero Ler";
    if (status === "READING") return "Lendo";
    return "Lido";
  };

  const statusColor = (status) => {
    if (status === "WANNA_READ") return "text-violet-500 bg-violet-50";
    if (status === "READING") return "text-amber-600 bg-amber-50";
    return "text-emerald-600 bg-emerald-50";
  };

  const displayedBooks = books.filter((book) =>
    book.title.toLowerCase().includes(search.toLowerCase()),
  );

  const inputClass =
    "w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:bg-white focus:ring-2 focus:ring-indigo-500 transition text-sm text-gray-800";

  return (
    <div className="min-h-screen bg-[#f8f9fc] font-sans">
      <Header />

      {toast.show && (
        <div className="fixed top-20 right-4 sm:right-8 z-50 bg-emerald-500 text-white px-4 py-3 rounded-lg shadow-lg flex items-center gap-2 max-w-[90%] w-auto">
          <CheckCircle2 className="w-5 h-5 shrink-0" />
          <span className="font-medium text-sm">{toast.message}</span>
        </div>
      )}

      <main className="p-4 sm:p-8 max-w-7xl mx-auto">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between mb-8 gap-4">
          <div className="relative w-full sm:w-72 shrink-0">
            <Search className="w-4 h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Buscar por título..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-9 pr-4 py-2 bg-white border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm text-gray-700"
            />
          </div>

          <div className="flex items-center gap-2 overflow-x-auto pb-1 sm:pb-0 w-full sm:w-auto hide-scrollbar">
            {[
              { id: "ALL", label: "Todos" },
              { id: "WANNA_READ", label: "Quero Ler" },
              { id: "READING", label: "Lendo" },
              { id: "READ", label: "Lido" },
            ].map((f) => (
              <button
                key={f.id}
                onClick={() => setFilter(f.id)}
                className={`px-4 py-1.5 rounded-full text-sm font-medium transition-colors ${
                  filter === f.id
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
          <div className="flex flex-col items-center justify-center mt-24">
            <div className="bg-gray-100 p-4 rounded-2xl mb-4 text-gray-400">
              <BookOpen className="w-10 h-10" />
            </div>
            <h2 className="text-xl font-semibold text-gray-800 mb-2">
              Sua biblioteca está vazia
            </h2>
            <p className="text-gray-500 text-sm text-center mb-6 max-w-xs">
              Adicione seu primeiro livro para começar a organizar suas
              leituras.
            </p>
            <button
              onClick={() => setIsModalOpen(true)}
              className="flex items-center gap-2 bg-indigo-500 hover:bg-indigo-600 text-white font-medium px-5 py-2.5 rounded-lg transition-colors text-sm"
            >
              <Plus className="w-4 h-4" />
              Adicionar Livro
            </button>
          </div>
        ) : (
          <div>
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-bold text-gray-800">Meus Livros</h2>
              <button
                onClick={() => setIsModalOpen(true)}
                className="flex items-center gap-1.5 sm:gap-2 bg-indigo-500 hover:bg-indigo-600 text-white font-medium px-3 sm:px-4 py-2 rounded-lg transition-colors text-sm"
              >
                <Plus className="w-4 h-4" />
                <span className="hidden sm:inline">Adicionar</span>
              </button>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-5">
              {displayedBooks.map((book) => (
                <div
                  key={book.id}
                  className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm hover:shadow-md transition-shadow flex flex-col gap-3"
                >
                  <div className="flex-1 min-w-0">
                    <h3 className="font-bold text-gray-900 truncate text-base leading-snug">
                      {book.title}
                    </h3>
                    <p className="text-sm text-gray-400 truncate mt-0.5">
                      {book.author}
                    </p>
                  </div>

                  <div className="flex items-center justify-between gap-2">
                    <span className="text-xs bg-gray-100 px-2 py-1 rounded-md text-gray-500 truncate max-w-[55%]">
                      {book.genre}
                    </span>
                    <span
                      className={`text-xs font-semibold px-2 py-1 rounded-md whitespace-nowrap ${statusColor(book.status)}`}
                    >
                      {statusLabel(book.status)}
                    </span>
                  </div>

                  <div className="flex items-center gap-2 pt-1 border-t border-gray-100">
                    <button
                      onClick={() => {
                        setEditingBook(book);
                        setIsModalUpdateOpen(true);
                      }}
                      className="flex items-center gap-1.5 flex-1 justify-center text-xs font-medium text-gray-500 hover:text-indigo-600 hover:bg-indigo-50 py-1.5 rounded-lg transition-colors"
                    >
                      <Pencil className="w-3.5 h-3.5" />
                      Editar
                    </button>
                    <div className="w-px h-4 bg-gray-200" />
                    <button
                      onClick={() => confirmDeleteBook(book)}
                      className="flex items-center gap-1.5 flex-1 justify-center text-xs font-medium text-gray-500 hover:text-red-600 hover:bg-red-50 py-1.5 rounded-lg transition-colors"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                      Excluir
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </main>

      {isModalOpen && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl w-full max-w-[450px] shadow-xl overflow-hidden max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center p-5 sm:p-6 border-b border-gray-100 sticky top-0 bg-white z-10">
              <h3 className="font-bold text-lg text-gray-900">
                Adicionar Livro
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
            <form onSubmit={handleAddBook} className="p-6 space-y-4">
              <div>
                <label className="block text-sm text-gray-600 mb-1.5">
                  Título
                </label>
                <input
                  type="text"
                  placeholder="Nome do livro"
                  required
                  value={newBook.title}
                  onChange={(e) =>
                    setNewBook({ ...newBook, title: e.target.value })
                  }
                  className={inputClass}
                />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1.5">
                  Autor
                </label>
                <input
                  type="text"
                  placeholder="Nome do autor"
                  required
                  value={newBook.author}
                  onChange={(e) =>
                    setNewBook({ ...newBook, author: e.target.value })
                  }
                  className={inputClass}
                />
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm text-gray-600 mb-1.5">
                    Gênero
                  </label>
                  <input
                    type="text"
                    placeholder="Ex: Ficção"
                    required
                    value={newBook.genre}
                    onChange={(e) =>
                      setNewBook({ ...newBook, genre: e.target.value })
                    }
                    className={inputClass}
                  />
                </div>
                <div>
                  <label className="block text-sm text-gray-600 mb-1.5">
                    Editora
                  </label>
                  <input
                    type="text"
                    placeholder="Alta Books"
                    required
                    value={newBook.publisher}
                    onChange={(e) =>
                      setNewBook({ ...newBook, publisher: e.target.value })
                    }
                    className={inputClass}
                  />
                </div>
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1.5">
                  Status de Leitura
                </label>
                <div className="relative">
                  <select
                    value={newBook.status}
                    onChange={(e) =>
                      setNewBook({ ...newBook, status: e.target.value })
                    }
                    className={inputClass + " appearance-none"}
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
                className="w-full mt-2 bg-indigo-500 hover:bg-indigo-600 disabled:bg-indigo-300 text-white font-medium py-2.5 rounded-lg transition-colors text-sm"
              >
                {isAdding ? "Adicionando..." : "Adicionar"}
              </button>
            </form>
          </div>
        </div>
      )}

      {isModalUpdateOpen && editingBook && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl w-full max-w-[450px] shadow-xl overflow-hidden max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center p-5 sm:p-6 border-b border-gray-100 sticky top-0 bg-white z-10">
              <h3 className="font-bold text-lg text-gray-900">
                Atualizar Livro
              </h3>
              <button
                onClick={() => setIsModalUpdateOpen(false)}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
            <form onSubmit={handleUpdateBook} className="p-6 space-y-4">
              <div>
                <label className="block text-sm text-gray-600 mb-1.5">
                  Título
                </label>
                <input
                  type="text"
                  placeholder="Nome do livro"
                  required
                  value={editingBook.title}
                  onChange={(e) =>
                    setEditingBook({ ...editingBook, title: e.target.value })
                  }
                  className={inputClass}
                />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1.5">
                  Autor
                </label>
                <input
                  type="text"
                  placeholder="Nome do autor"
                  required
                  value={editingBook.author}
                  onChange={(e) =>
                    setEditingBook({ ...editingBook, author: e.target.value })
                  }
                  className={inputClass}
                />
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm text-gray-600 mb-1.5">
                    Gênero
                  </label>
                  <input
                    type="text"
                    placeholder="Ex: Ficção"
                    required
                    value={editingBook.genre}
                    onChange={(e) =>
                      setEditingBook({ ...editingBook, genre: e.target.value })
                    }
                    className={inputClass}
                  />
                </div>
                <div>
                  <label className="block text-sm text-gray-600 mb-1.5">
                    Editora
                  </label>
                  <input
                    type="text"
                    placeholder="Alta Books"
                    required
                    value={editingBook.publisher}
                    onChange={(e) =>
                      setEditingBook({
                        ...editingBook,
                        publisher: e.target.value,
                      })
                    }
                    className={inputClass}
                  />
                </div>
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1.5">
                  Status de Leitura
                </label>
                <div className="relative">
                  <select
                    value={editingBook.status}
                    onChange={(e) =>
                      setEditingBook({ ...editingBook, status: e.target.value })
                    }
                    className={inputClass + " appearance-none"}
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
                className="w-full mt-2 bg-indigo-500 hover:bg-indigo-600 disabled:bg-indigo-300 text-white font-medium py-2.5 rounded-lg transition-colors text-sm"
              >
                {isAdding ? "Atualizando..." : "Atualizar"}
              </button>
            </form>
          </div>
        </div>
      )}

      {isDeleteModalOpen && bookToDelete && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl w-full max-w-[380px] shadow-xl overflow-hidden">
            <div className="p-5 sm:p-6 flex flex-col items-center text-center gap-4">
              <div className="w-12 h-12 bg-red-50 rounded-full flex items-center justify-center">
                <AlertTriangle className="w-6 h-6 text-red-500" />
              </div>
              <div>
                <h3 className="font-bold text-gray-900 text-lg">
                  Excluir livro?
                </h3>
                <p className="text-sm text-gray-500 mt-1">
                  Tem certeza que deseja excluir{" "}
                  <span className="font-semibold text-gray-700">
                    "{bookToDelete.title}"
                  </span>
                  ? Esta ação não pode ser desfeita.
                </p>
              </div>
              <div className="flex gap-3 w-full mt-2">
                <button
                  onClick={() => {
                    setIsDeleteModalOpen(false);
                    setBookToDelete(null);
                  }}
                  className="flex-1 py-2.5 rounded-lg border border-gray-200 text-sm font-medium text-gray-600 hover:bg-gray-50 transition-colors"
                >
                  Cancelar
                </button>
                <button
                  onClick={handleDeleteBook}
                  disabled={isDeleting}
                  className="flex-1 py-2.5 rounded-lg bg-red-500 hover:bg-red-600 disabled:bg-red-300 text-white text-sm font-medium transition-colors"
                >
                  {isDeleting ? "Excluindo..." : "Sim, excluir"}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Home;
