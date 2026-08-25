$(document).ready(function () {
  const table = $("#drgTable").DataTable({
    dom: "rtip", // Hides standard DataTables search bar
    pageLength: 10,
    responsive: true,
    ordering: true,
    ajax: {
      url: "/api/csv/drg/display",
      type: "GET",
      dataSrc: "", // Flat JSON list
      error: function (xhr) {
        console.error("Failed to load Drg data:", xhr.responseText);
      },
    },
    columns: [
      {
        data: "mdc",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "dc",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },

      {
        data: "drg",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },

      {
        data: "rw",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },

      {
        data: "wtlos",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },

      {
        data: "ot",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "mdf",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "drgname",
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
