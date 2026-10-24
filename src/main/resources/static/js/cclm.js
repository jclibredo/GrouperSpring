$(document).ready(function () {
  const table = $("#cclmTable").DataTable({
    dom: "rtip", // Hides standard DataTables search bar
    pageLength: 10,
    responsive: true,
    ordering: true,
    ajax: {
      url: "/api/csv/cclm/display",
      type: "GET",
      dataSrc: "", // Flat JSON list
      error: function (xhr) {
        console.error("Failed to load CCLM data:", xhr.responseText);
      },
    },
    columns: [
      {
        data: "ccrow",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "ccl",
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
