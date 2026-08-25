$(document).ready(function () {
  // Initialize DataTables with AJAX integration
  const table = $("#axTable").DataTable({
    dom: "rtip", // Hide default DataTables search box to use #globalSearch
    pageLength: 10,
    responsive: true,
    ordering: true,
    ajax: {
      url: "/api/csv/display", // Your endpoint mapping
      type: "GET",
      dataSrc: "", // Since your endpoint returns a flat JSON list [ {...}, {...} ]
      error: function (xhr, error, thrown) {
        console.error(
          "Failed to fetch data from /api/csv/display:",
          xhr.responseText,
        );
      },
    },
    columns: [
      {
        data: "ax",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        // Field name returned by your Java model object
        data: "code",
        defaultContent: "",
      },
    ],
  });

  // Client-side quick filter using custom search bar
  $("#globalSearch").on("keyup", function () {
    table.search(this.value).draw();
  });
});
