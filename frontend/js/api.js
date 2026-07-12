/* =========================================================
   ADIMS - api.js
   Central fetch wrapper, auth/session helpers, shared UI utils
   ========================================================= */

const API_BASE_URL = window.ADIMS_API_BASE_URL || "http://localhost:8080/api";

const Session = {
  getToken() { return localStorage.getItem("adims_token"); },
  getUser() {
    const raw = localStorage.getItem("adims_user");
    return raw ? JSON.parse(raw) : null;
  },
  save(loginResponse) {
    localStorage.setItem("adims_token", loginResponse.token);
    localStorage.setItem("adims_user", JSON.stringify({
      userId: loginResponse.userId,
      username: loginResponse.username,
      fullName: loginResponse.fullName,
      role: loginResponse.role
    }));
  },
  clear() {
    localStorage.removeItem("adims_token");
    localStorage.removeItem("adims_user");
  },
  isAuthenticated() { return !!this.getToken(); },
  hasAnyRole(...roles) {
    const user = this.getUser();
    return !!user && roles.includes(user.role);
  }
};

function requireAuth() {
  if (!Session.isAuthenticated()) {
    window.location.href = "index.html";
  }
}

async function apiRequest(path, options = {}) {
  const headers = Object.assign(
    { "Content-Type": "application/json" },
    options.headers || {}
  );

  const token = Session.getToken();
  if (token) {
    headers["Authorization"] = "Bearer " + token;
  }

  let response;
  try {
    response = await fetch(API_BASE_URL + path, {
      ...options,
      headers
    });
  } catch (networkErr) {
    throw new Error("Could not reach the server. Please check your connection or that the backend is running.");
  }

  if (response.status === 401) {
    Session.clear();
    window.location.href = "index.html";
    throw new Error("Session expired. Please log in again.");
  }

  if (response.status === 204) {
    return null;
  }

  let body = null;
  const text = await response.text();
  if (text) {
    try { body = JSON.parse(text); } catch (e) { body = text; }
  }

  if (!response.ok) {
    const message = (body && body.message) ? body.message : `Request failed (${response.status})`;
    const error = new Error(message);
    error.details = body && body.details;
    error.status = response.status;
    throw error;
  }

  return body;
}

const api = {
  get: (path) => apiRequest(path, { method: "GET" }),
  post: (path, data) => apiRequest(path, { method: "POST", body: JSON.stringify(data) }),
  put: (path, data) => apiRequest(path, { method: "PUT", body: JSON.stringify(data) }),
  patch: (path, data) => apiRequest(path, { method: "PATCH", body: JSON.stringify(data) }),
  delete: (path) => apiRequest(path, { method: "DELETE" })
};

/* ---------- Toast notifications ---------- */

function showToast(message, type = "default") {
  let container = document.querySelector(".toast-container");
  if (!container) {
    container = document.createElement("div");
    container.className = "toast-container";
    document.body.appendChild(container);
  }
  const toast = document.createElement("div");
  toast.className = "toast" + (type !== "default" ? " " + type : "");
  toast.textContent = message;
  container.appendChild(toast);
  setTimeout(() => toast.remove(), 4000);
}

function showError(err) {
  console.error(err);
  const message = err && err.message ? err.message : "Something went wrong";
  showToast(message, "error");
}

/* ---------- Formatting helpers ---------- */

function formatDate(value) {
  if (!value) return "\u2014";
  const d = new Date(value);
  if (isNaN(d.getTime())) return value;
  return d.toLocaleDateString(undefined, { year: "numeric", month: "short", day: "numeric" });
}

function formatDateTime(value) {
  if (!value) return "\u2014";
  const d = new Date(value);
  if (isNaN(d.getTime())) return value;
  return d.toLocaleString(undefined, { year: "numeric", month: "short", day: "numeric", hour: "2-digit", minute: "2-digit" });
}

function badge(value, extraClass) {
  if (!value) return `<span class="badge badge-neutral">\u2014</span>`;
  const cls = "badge-" + value.toString().toLowerCase();
  const label = value.toString().replaceAll("_", " ");
  return `<span class="badge ${cls} ${extraClass || ""}"><span class="badge-dot"></span>${label}</span>`;
}

function humanize(value) {
  if (!value) return "\u2014";
  return value.toString().replaceAll("_", " ").replace(/\w\S*/g, (t) => t.charAt(0).toUpperCase() + t.substring(1).toLowerCase());
}

function escapeHtml(str) {
  if (str === null || str === undefined) return "";
  return str.toString()
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;");
}

function qs(params) {
  const usp = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => {
    if (v !== null && v !== undefined && v !== "") usp.append(k, v);
  });
  const s = usp.toString();
  return s ? "?" + s : "";
}
