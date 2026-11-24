$(document).ready(function () {
  const table = $("#procTable").DataTable({
    dom: "rtip", // Hides standard DataTables search bar
    pageLength: 10,
    responsive: true,
    ordering: true,
    ajax: {
      url: "/api/csv/proc/display",
      type: "GET",
      dataSrc: "", // Flat JSON list
      error: function (xhr) {
        console.error("Failed to load PROC data:", xhr.responseText);
      },
    },
    columns: [
      {
        data: "code",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "orp",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "orptype",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "sex",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "site",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "procgr",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "pcpart",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "extlev",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "desc",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "drguse",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "mayun",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
    ],
  });

  // Connect global search bar
  $("#globalSearch").on("keyup", function () {
    table.search(this.value).draw();
  });
});
