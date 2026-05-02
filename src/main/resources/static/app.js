const API = "http://localhost:8080";

window.onload = () => {
    loadBooks(); // önce ana içerik

    setTimeout(() => {
        loadCategories();
        loadFavorites();
        loadRecommendations();
    }, 300);
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
function addFavorite(bookId) {
    const userId = localStorage.getItem("userId");

    if (!userId || userId === "undefined" || userId === "null") {
        alert("Favorilere eklemek için önce giriş yapmalısın.");
        localStorage.removeItem("userId");
        window.location.href = "login.html";
        return;
    }

    fetch(`${API}/favorites?userId=${encodeURIComponent(userId)}&bookId=${encodeURIComponent(bookId)}`, {
        method: "POST"
    })
    .then(res => {
        if (!res.ok) {
            return res.text().then(text => {
                throw new Error(text);
            });
        }
        return res.json();
    })
    .then(() => {
        alert("Favorilere eklendi 💖");
        loadFavorites();
        loadRecommendations();
    })
    .catch(err => {
        console.error("Favorite error:", err);
        alert("Favoriye eklenemedi. Console veya backend loguna bak.");
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

// 🔐 REGISTER
function register() {
    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    fetch(`${API}/users/register`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ username, email, password })
    })
    .then(res => res.json())
    .then(data => {
        alert("Registered!");
        window.location.href = "login.html";
    });
}

// 🔐 LOGIN
function login() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    fetch(`${API}/users/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, password })
    })
    .then(res => res.json())
    .then(user => {
        localStorage.setItem("userId", user.id);
        window.location.href = "books.html";
    });
}
function loadCategories() {
    const container = document.getElementById("categoryFilters");
    container.innerHTML = "<p>Kategoriler yükleniyor...</p>";

    fetch(`${API}/books/categories`)
    .then(res => res.json())
    .then(categories => {
        container.innerHTML = "";

        let uniqueCategories = new Set();

        categories.forEach(cat => {
            if (!cat) return;

            cat.split(",").forEach(c => {
                uniqueCategories.add(c.trim());
            });
        });

        uniqueCategories.forEach(cat => {
            container.innerHTML += `
                <label class="filter-item">
                    <input type="checkbox" onchange="filterBooks('${cat}')">
                    ${cat}
                </label>
            `;
        });
    });
}
function filterBooks() {
    const checked = document.querySelectorAll("#categoryFilters input:checked");

    let categories = [];
    checked.forEach(c => categories.push(c.nextSibling.textContent.trim()));

    if (categories.length === 0) {
        loadBooks();
        return;
    }

    fetch(`${API}/books/filter?category=${categories[0]}`)
    .then(res => res.json())
    .then(data => renderBooks(data));
}