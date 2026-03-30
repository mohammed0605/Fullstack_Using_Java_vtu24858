// ============================================
// EventPlatform - Shared JavaScript Utilities
// ============================================

/**
 * Require the user to be authenticated (redirects to login if not)
 */
function requireAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html';
    }
}

/**
 * Require the user to be an admin (redirects to dashboard if not admin)
 */
function requireAdmin() {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    if (!token) {
        window.location.href = '/login.html';
        return;
    }
    if (role !== 'ADMIN') {
        window.location.href = '/dashboard.html';
    }
}

/**
 * Logout the current user
 */
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('fullName');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    window.location.href = '/login.html';
}

/**
 * Show an alert message
 * @param {string} message - The message to display
 * @param {string} type - 'success' or 'error'
 */
function showAlert(message, type = 'info') {
    const alertBox = document.getElementById('alert-box');
    if (!alertBox) return;
    alertBox.className = `alert alert-${type === 'success' ? 'success' : type === 'error' ? 'error' : 'info'}`;
    alertBox.textContent = message;
    alertBox.classList.remove('hidden');
    if (type !== 'error') {
        setTimeout(() => alertBox.classList.add('hidden'), 4000);
    }
}

/**
 * Update navigation links based on auth state
 */
function updateNavLinks() {
    const container = document.getElementById('nav-user-links');
    if (!container) return;

    const token = localStorage.getItem('token');
    const fullName = localStorage.getItem('fullName');
    const role = localStorage.getItem('role');
    const username = localStorage.getItem('username');

    if (token) {
        const initial = (fullName || username || '?')[0].toUpperCase();
        container.innerHTML = `
            <a href="/dashboard.html">My Tickets</a>
            ${role === 'ADMIN' ? '<a href="/admin.html">Admin</a>' : ''}
            <div class="nav-user-info">
                <div class="nav-avatar">${initial}</div>
                <span>${fullName || username}</span>
                <button class="btn btn-sm btn-outline" onclick="logout()" style="margin-left:0.5rem">Logout</button>
            </div>`;
    } else {
        container.innerHTML = `
            <a href="/login.html">Login</a>
            <a href="/register.html" style="margin-left:0.25rem">Register</a>`;
    }
}

/**
 * Format currency
 */
function formatCurrency(amount) {
    return '₹' + parseFloat(amount).toFixed(2);
}

/**
 * Format date
 */
function formatDate(dateStr) {
    return new Date(dateStr).toLocaleDateString('en-US', {
        year: 'numeric', month: 'short', day: 'numeric'
    });
}

/**
 * Format datetime
 */
function formatDateTime(dateStr) {
    return new Date(dateStr).toLocaleString('en-US', {
        year: 'numeric', month: 'short', day: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
}

/**
 * Authenticated fetch wrapper
 */
async function authFetch(url, options = {}) {
    const token = localStorage.getItem('token');
    const headers = {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
        ...options.headers
    };
    return fetch(url, { ...options, headers });
}
