$(document).ready(function () {
  const table = $("#i10vxTable").DataTable({
    dom: "rtip", // Hides standard DataTables search bar
    pageLength: 10,
    responsive: true,
    ordering: true,
    ajax: {
      url: "/api/csv/i10vx/display",
      type: "GET",
      dataSrc: "", // Flat JSON list
      error: function (xhr) {
        console.error("Failed to load I10vx data:", xhr.responseText);
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
        data: "validCode",
        render: function (data) {
          // Convert 1/"1" to TRUE and 0/"0" to FALSE
          if (data == 1 || data === "1") {
            return "TRUE";
          } else if (data == 0 || data === "0") {
            return "FALSE";
          }
          return ""; // Fallback for null or undefined
        },
      },
      {
        data: "desc",
        defaultContent: "",
      },
    ],
  });

  // Connect global search bar
  $("#globalSearch").on("keyup", function () {
    table.search(this.value).draw();
  });
});
