$(document).ready(function () {
  const table = $("#prpdcTable").DataTable({
    dom: "rtip", // Hides standard DataTables search bar
    pageLength: 10,
    responsive: true,
    ordering: true,
    ajax: {
      url: "/api/csv/prpdc/display",
      type: "GET",
      dataSrc: "", // Flat JSON list
      error: function (xhr) {
        console.error("Failed to load PRPDC data:", xhr.responseText);
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
        data: "mdc",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "pdc",
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
