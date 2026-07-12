/* =========================================================
   ADIMS - nav.js
   Renders sidebar/topbar shell and handles logout.
   Include after api.js on every authenticated page.
   ========================================================= */

const NAV_ITEMS = [
  { href: "dashboard.html", label: "Dashboard", icon: "grid", roles: null },
  { href: "athletes.html", label: "Athletes", icon: "user", roles: null },
  { href: "tips.html", label: "Intelligence Tips", icon: "flag", roles: null },
  { href: "cases.html", label: "Investigations", icon: "folder", roles: null },
  { href: "testing.html", label: "Testing Records", icon: "check", roles: null },
  { href: "users.html", label: "Users", icon: "shield", roles: ["ADMIN"] },
  { href: "audit-log.html", label: "Audit Log", icon: "clock", roles: ["ADMIN"] }
];

const ICONS = {
  grid: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/><rect x="3" y="14" width="7" height="7" rx="1.5"/><rect x="14" y="14" width="7" height="7" rx="1.5"/></svg>',
  user: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 3.5-7 8-7s8 3 8 7"/></svg>',
  flag: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 21V4"/><path d="M5 4h11l-2 4 2 4H5"/></svg>',
  folder: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6a1 1 0 011-1h5l2 2h9a1 1 0 011 1v10a1 1 0 01-1 1H4a1 1 0 01-1-1V6z"/></svg>',
  check: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 12l6 6 12-13"/></svg>',
  shield: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 3l7 3v6c0 4.5-3 7.5-7 9-4-1.5-7-4.5-7-9V6l7-3z"/></svg>',
  clock: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 3"/></svg>'
};

function renderShell(activeHref, pageTitle, pageSubtitle) {
  requireAuth();
  const user = Session.getUser();

  const navHtml = NAV_ITEMS
    .filter(item => !item.roles || Session.hasAnyRole(...item.roles))
    .map(item => {
      const active = item.href === activeHref ? " active" : "";
      return `<a href="${item.href}" class="${active.trim()}">${ICONS[item.icon]}<span>${item.label}</span></a>`;
    }).join("");

  const initials = (user.fullName || user.username || "?").split(" ").map(p => p[0]).join("").substring(0, 2).toUpperCase();

  document.body.insertAdjacentHTML("afterbegin", `
    <div class="app-shell">
      <aside class="sidebar">
        <div class="sidebar-brand">
          <div class="logo-mark">AD</div>
          <div class="title">ADIMS</div>
          <div class="subtitle">Anti-Doping Intelligence &amp;<br/>Investigation Management</div>
        </div>
        <nav class="sidebar-nav">${navHtml}</nav>
        <div class="sidebar-footer">
          <div class="user-name">${escapeHtml(user.fullName || user.username)}</div>
          <div>${escapeHtml(user.role)} &middot; ${initials}</div>
          <button class="btn btn-secondary btn-sm" id="logoutBtn">Log out</button>
        </div>
      </aside>
      <div class="main-content">
        <div class="topbar">
          <div>
            <h2 style="margin:0">${pageTitle || ""}</h2>
            ${pageSubtitle ? `<div class="text-muted text-sm">${pageSubtitle}</div>` : ""}
          </div>
          <div class="flex-gap">
            <span class="text-sm text-muted">Signed in as <strong>${escapeHtml(user.username)}</strong></span>
          </div>
        </div>
        <div class="page-body" id="pageBody"></div>
      </div>
    </div>
  `);

  document.getElementById("logoutBtn").addEventListener("click", () => {
    Session.clear();
    window.location.href = "index.html";
  });
}

function pageBody() {
  return document.getElementById("pageBody");
}
