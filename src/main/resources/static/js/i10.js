$(document).ready(function () {
  const table = $("#i10Table").DataTable({
    dom: "rtip", // Hides standard DataTables search bar
    pageLength: 10,
    responsive: true,
    ordering: true,
    ajax: {
      url: "/api/csv/i10/display",
      type: "GET",
      dataSrc: "", // Flat JSON list
      error: function (xhr) {
        console.error("Failed to load I10 data:", xhr.responseText);
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

      {
        data: "cc",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },

      {
        data: "maincc",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },

      {
        data: "ccrow",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },

      {
        data: "hivax",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "trauma",
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
        data: "accpdx",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "ageduse",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "agemin",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },

      {
        data: "agemax",
        render: function (data) {
          return `<span class="font-mono">${data ? data : ""}</span>`;
        },
      },
      {
        data: "agedmin",
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
