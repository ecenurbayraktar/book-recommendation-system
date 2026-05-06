const API = "http://localhost:8080";
const PAGE_SIZE = 20;
const CATEGORY_LIMIT = 15;

let currentPage = 0;
let currentMode = "all";
let currentQuery = "";
let isLoadingBooks = false;
let hasMoreBooks = true;
let allCategories = [];
let showAllCategories = false;
let toastTimer;

window.onload = () => {
    loadBooks();

    setTimeout(() => {
        loadCategories();
        loadFavorites();
        loadRecommendations();
    }, 300);
};

function loadBooks(page = 0, append = false) {
    currentMode = "all";
    currentQuery = "";
    fetchBooks(`${API}/books?page=${page}&size=${PAGE_SIZE}`, page, append);
}

function loadMoreBooks() {
    if (isLoadingBooks || !hasMoreBooks) {
        return;
    }

    if (currentMode === "search") {
        searchBooks(currentPage + 1, true);
        return;
    }

    loadBooks(currentPage + 1, true);
}

function fetchBooks(url, page, append) {
    isLoadingBooks = true;
    updateLoadMoreButton();

    fetch(url)
    .then(res => res.json())
    .then(data => {
        currentPage = page;
        hasMoreBooks = !data.last && data.content.length === PAGE_SIZE;
        renderBooks(data.content, append);
    })
    .finally(() => {
        isLoadingBooks = false;
        updateLoadMoreButton();
    });
}

function searchBooks(page = 0, append = false) {
    const query = document.getElementById("search").value.trim();

    if (!query) {
        loadBooks();
        return;
    }

    currentMode = "search";
    currentQuery = query;
    fetchBooks(`${API}/books/search?query=${encodeURIComponent(currentQuery)}&page=${page}&size=${PAGE_SIZE}`, page, append);
}

function renderBooks(books, append = false) {
    const list = document.getElementById("bookList");

    if (!append) {
        list.innerHTML = "";
    }

    books.forEach(book => {
        list.innerHTML += `
            <div class="book">
                <button class="heart" type="button" onclick="addFavorite('${book.bookId}')" title="Favorilere ekle">
    <img src="fav.png" alt="favorite">
</button>
                <h4>${escapeHtml(book.title || "")}</h4>
                <p>${escapeHtml(book.authors || book.author || "")}</p>
            </div>
        `;
    });
}

function updateLoadMoreButton() {
    const button = document.getElementById("loadMoreBtn");

    if (!button) {
        return;
    }

    button.style.display = currentMode === "filter" || !hasMoreBooks ? "none" : "block";
    button.disabled = isLoadingBooks;
    button.textContent = isLoadingBooks ? "Yükleniyor..." : "Daha fazla yükle";
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
        showToast("Favorilere eklendi", "success");
        loadFavorites();
        loadRecommendations();
    })
    .catch(err => {
        console.error("Favorite error:", err);
        showToast("Favoriye eklenemedi", "error");
    });
}

function showToast(message, type = "success") {
    const toast = document.getElementById("toast");

    if (!toast) {
        return;
    }

    clearTimeout(toastTimer);
    toast.textContent = message;
    toast.className = `toast show ${type}`;

    toastTimer = setTimeout(() => {
        toast.className = "toast";
    }, 2800);
}

function loadFavorites() {
    const userId = localStorage.getItem("userId");

    fetch(`${API}/favorites/user/${userId}`)
    .then(res => res.json())
    .then(data => {
        const list = document.getElementById("favoriteList");
        list.innerHTML = "";

        data.forEach(f => {
            list.innerHTML += `<p>♡ ${escapeHtml(f.book.title)}</p>`;
        });
    });
}

function loadRecommendations() {
    const userId = localStorage.getItem("userId");

    fetch(`${API}/favorites/recommend/${userId}`)
    .then(res => res.json())
    .then(data => {
        const list = document.getElementById("recommendList");
        list.innerHTML = "";

        data.forEach(book => {
            list.innerHTML += `<p>• ${escapeHtml(book.title)}</p>`;
        });
    });
}

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
    .then(() => {
        alert("Registered!");
        window.location.href = "login.html";
    });
}

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
        let uniqueCategories = new Set();

        categories.forEach(cat => {
            if (!cat) {
                return;
            }

            cat.split(",").forEach(c => {
                const category = c.trim();

                if (category) {
                    uniqueCategories.add(category);
                }
            });
        });

        allCategories = Array.from(uniqueCategories)
            .sort((a, b) => a.localeCompare(b));

        renderCategoryFilters();
    });
}

function renderCategoryFilters() {
    const container = document.getElementById("categoryFilters");
    container.innerHTML = `
        <input id="categorySearch" class="category-search" placeholder="Kategori ara..." oninput="updateCategoryList()">
        <div id="selectedCategories" class="selected-categories"></div>
        <div id="categoryList"></div>
        <button id="toggleCategoriesBtn" class="category-toggle" onclick="toggleCategories()"></button>
    `;

    updateCategoryList();
}

function updateCategoryList() {
    const searchInput = document.getElementById("categorySearch");
    const query = searchInput ? searchInput.value.trim() : "";
    const selectedCategories = getSelectedCategories();
    const filteredCategories = allCategories.filter(category =>
        category.toLowerCase().includes(query.toLowerCase())
    );
    const visibleCategories = showAllCategories || query
        ? filteredCategories
        : filteredCategories.slice(0, CATEGORY_LIMIT);

    const list = document.getElementById("categoryList");
    const selectedList = document.getElementById("selectedCategories");
    const toggleButton = document.getElementById("toggleCategoriesBtn");

    selectedList.innerHTML = selectedCategories.map(category =>
        `<span class="category-chip">${escapeHtml(category)}</span>`
    ).join("");

    list.innerHTML = visibleCategories.map(category => `
        <label class="filter-item">
            <input type="checkbox" onchange="filterBooks()" value="${escapeHtml(category)}" ${selectedCategories.includes(category) ? "checked" : ""}>
            <span>${escapeHtml(category)}</span>
        </label>
    `).join("");

    toggleButton.style.display = query || filteredCategories.length <= CATEGORY_LIMIT ? "none" : "block";
    toggleButton.textContent = showAllCategories ? "Daha az göster" : `Daha fazla göster (${filteredCategories.length - CATEGORY_LIMIT})`;
}

function toggleCategories() {
    showAllCategories = !showAllCategories;
    updateCategoryList();
}

function getSelectedCategories() {
    return Array.from(document.querySelectorAll("#categoryList input:checked"))
        .map(input => input.value);
}

function filterBooks() {
    const categories = getSelectedCategories();

    if (categories.length === 0) {
        loadBooks();
        return;
    }

    currentMode = "filter";
    hasMoreBooks = false;
    updateLoadMoreButton();

    fetch(`${API}/books/filter?category=${encodeURIComponent(categories[0])}`)
    .then(res => res.json())
    .then(data => {
        renderBooks(data);
        updateCategoryList();
    });
}

function escapeHtml(value) {
    return String(value).replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        "\"": "&quot;",
        "'": "&#39;"
    }[character]));
}
