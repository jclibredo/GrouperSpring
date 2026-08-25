$(document).ready(function () {
  const table = $("#dcvTable").DataTable({
    dom: "rtip", // Hides standard DataTables search bar
    pageLength: 10,
    responsive: true,
    ordering: true,
    ajax: {
      url: "/api/csv/dcv/display",
      type: "GET",
      dataSrc: "", // Flat JSON list
      error: function (xhr) {
        console.error("Failed to load DCV data:", xhr.responseText);
      },
    },
    columns: [
      {
        data: "pdrg",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "fdrg",
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
