const API = "http://localhost:8080";

window.onload = () => {
    loadBooks();
    loadFavorites();
    loadRecommendations();
};

// 📚 LOAD ALL BOOKS
function loadBooks() {
    fetch(`${API}/books?page=0&size=20`)
    .then(res => res.json())
    .then(data => renderBooks(data.content));
}

// 🔍 SEARCH
function searchBooks() {
    const query = document.getElementById("search").value;

    if (!query) {
        loadBooks();
        return;
    }

    fetch(`${API}/books/search?query=${query}&page=0&size=20`)
    .then(res => res.json())
    .then(data => renderBooks(data.content));
}

// 🎀 RENDER BOOKS
function renderBooks(books) {
    const list = document.getElementById("bookList");
    list.innerHTML = "";

    books.forEach(book => {
        list.innerHTML += `
            <div class="book">
                <div class="heart" onclick="addFavorite('${book.bookId}')">💖</div>
                <h4>${book.title}</h4>
                <p>${book.authors}</p>
            </div>
        `;
    });
}

// 💖 ADD FAVORITE
function addFavorite(bookId) {
    const userId = localStorage.getItem("userId");

    fetch(`${API}/favorites?userId=${userId}&bookId=${bookId}`, {
        method: "POST"
    })
    .then(() => {
        loadFavorites();
        loadRecommendations();
    });
}

// ⭐ LOAD FAVORITES
function loadFavorites() {
    const userId = localStorage.getItem("userId");

    fetch(`${API}/favorites/user/${userId}`)
    .then(res => res.json())
    .then(data => {
        const list = document.getElementById("favoriteList");
        list.innerHTML = "";

        data.forEach(f => {
            list.innerHTML += `<p>💖 ${f.book.title}</p>`;
        });
    });
}

// 💡 LOAD RECOMMENDATIONS
function loadRecommendations() {
    const userId = localStorage.getItem("userId");

    fetch(`${API}/favorites/recommend/${userId}`)
    .then(res => res.json())
    .then(data => {
        const list = document.getElementById("recommendList");
        list.innerHTML = "";

        data.forEach(book => {
            list.innerHTML += `<p>✨ ${book.title}</p>`;
        });
    });
}