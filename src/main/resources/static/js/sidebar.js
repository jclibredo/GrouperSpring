document.addEventListener("DOMContentLoaded", () => {
  const toggleBtn = document.getElementById("sidebarToggle");
  const sidebar = document.getElementById("sidebar");
  const mainContent = document.querySelector(".main-content");

  if (toggleBtn && sidebar) {
    toggleBtn.addEventListener("click", (e) => {
      // 1. Stop the click from reaching 'main-content' or 'body'
      e.preventDefault();
      e.stopPropagation();

      sidebar.classList.toggle("active");
    });

    // 2. Only close if the sidebar is actually open
    document.addEventListener("click", (e) => {
      if (sidebar.classList.contains("active") && !sidebar.contains(e.target)) {
        sidebar.classList.remove("active");
      }
    });
  }
});

function toggleSubmenu(element) {
  const parentGroup = element.closest(".nav-group");
  parentGroup.classList.toggle("open");
}
