$(document).ready(function () {
  const table = $("#bmdcTable").DataTable({
    dom: "rtip", // Hides standard DataTables search bar
    pageLength: 10,
    responsive: true,
    ordering: true,
    ajax: {
      url: "/api/csv/bmdc/display",
      type: "GET",
      dataSrc: "", // Flat JSON list
      error: function (xhr) {
        console.error("Failed to load Bmdc data:", xhr.responseText);
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
        data: "mdcmale",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },

      {
        data: "pdcmale",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },

      {
        data: "mdcfem",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },

      {
        data: "pdcfem",
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
    ],
  });

  // Connect global search bar
  $("#globalSearch").on("keyup", function () {
    table.search(this.value).draw();
  });
});
