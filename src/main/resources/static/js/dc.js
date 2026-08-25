$(document).ready(function () {
  const table = $("#dcTable").DataTable({
    dom: "rtip", // Hides standard DataTables search bar
    pageLength: 10,
    responsive: true,
    ordering: true,
    ajax: {
      url: "/api/csv/dc/display",
      type: "GET",
      dataSrc: "", // Flat JSON list
      error: function (xhr) {
        console.error("Failed to load DC data:", xhr.responseText);
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
        data: "dccol",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },

      {
        data: "drgx",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },

      {
        data: "cname",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },

      {
        data: "drgx2",
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
