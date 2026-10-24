package phil.grouper.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import phil.grouper.csvbinder.ax;
import phil.grouper.csvbinder.bmdc;
import phil.grouper.csvbinder.ccex;
import phil.grouper.csvbinder.cclm;
import phil.grouper.csvbinder.da;
import phil.grouper.csvbinder.dcv;
import phil.grouper.csvbinder.drg;
import phil.grouper.csvbinder.dc;
import phil.grouper.csvbinder.i10;
import phil.grouper.csvbinder.i10vx;
import phil.grouper.csvbinder.prpdc;
import phil.grouper.csvreader.AxCsvReaderService;
import phil.grouper.csvreader.BmdcCsvReaderService;
import phil.grouper.csvreader.CcexCsvReaderService;
import phil.grouper.csvreader.CclmCsvReaderService;
import phil.grouper.csvreader.DaCsvReaderService;
import phil.grouper.csvreader.DcCsvReaderService;
import phil.grouper.csvreader.DcvCsvReaderService;
import phil.grouper.csvreader.DrgCsvReaderService;
import phil.grouper.csvreader.I10CsvReaderService;
import phil.grouper.csvreader.I10vxCsvReaderService;
import phil.grouper.csvreader.PrpdcCsvReaderService;

@RestController
@RequestMapping("/api/csv")
public class CsvDisplayController {

    @Value("${grouper.csv.dir}")
    private String csvDirectoryPath;

    private final AxCsvReaderService csvReaderService;
    private final I10vxCsvReaderService i10vxCsvReaderService;
    private final DaCsvReaderService daCsvReaderService;
    private final CcexCsvReaderService ccexCsvReaderService;
    private final BmdcCsvReaderService bmdcCsvReaderService;
    private final I10CsvReaderService i10CsvReaderService;
    private final DrgCsvReaderService drgCsvReaderService;
    private final DcvCsvReaderService dcvCsvReaderService;
    private final DcCsvReaderService dcCsvReaderService;
    private final PrpdcCsvReaderService prpdcCsvReaderService;
    private final CclmCsvReaderService cclmCsvReaderService;

    public CsvDisplayController(
            AxCsvReaderService csvReaderService,
            I10vxCsvReaderService i10vxCsvReaderService,
            DaCsvReaderService daCsvReaderService,
            CcexCsvReaderService ccexCsvReaderService,
            BmdcCsvReaderService bmdcCsvReaderService,
            I10CsvReaderService i10CsvReaderService,
            DrgCsvReaderService drgCsvReaderService,
            DcvCsvReaderService dcvCsvReaderService,
            DcCsvReaderService dcCsvReaderService,
            PrpdcCsvReaderService prpdcCsvReaderService,
            CclmCsvReaderService cclmCsvReaderService) {
        this.csvReaderService = csvReaderService;
        this.i10vxCsvReaderService = i10vxCsvReaderService;
        this.daCsvReaderService = daCsvReaderService;
        this.ccexCsvReaderService = ccexCsvReaderService;
        this.bmdcCsvReaderService = bmdcCsvReaderService;
        this.i10CsvReaderService = i10CsvReaderService;
        this.drgCsvReaderService = drgCsvReaderService;
        this.dcvCsvReaderService = dcvCsvReaderService;
        this.dcCsvReaderService = dcCsvReaderService;
        this.prpdcCsvReaderService = prpdcCsvReaderService;
        this.cclmCsvReaderService = cclmCsvReaderService;
    }

    // Supported endpoint patterns:
    // GET /api/csv/display --> Returns all records
    // GET /api/csv/display?ax=X --> Filters by 'ax' only
    // GET /api/csv/display?code=A000 --> Filters by 'codes' only
    // GET /api/csv/display?ax=X&code=A000 --> Filters by both 'ax' and 'codes'
    @GetMapping("/display")
    public ResponseEntity<?> displayCsvContent(
            @RequestParam(required = false) String ax,
            @RequestParam(required = false) String code) {

        final String fileName = "ax.csv";
        Path fullPath = Paths.get(csvDirectoryPath, fileName);

        if (!Files.exists(fullPath)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: File '" + fileName + "' does not exist at path: " + fullPath.toAbsolutePath());
        }
        // Returns filtered records or full list depending on query params provided
        List<ax> records = csvReaderService.readAndFilter(csvDirectoryPath, fileName, ax, code);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/i10vx/display")
    public ResponseEntity<?> displayI10vxCsvContent(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String desc,
            @RequestParam(required = false) String validcode) {
        final String fileName = "i10vx.csv";
        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: File '" + fileName + "' does not exist at path: " + fullPath.toAbsolutePath());
        }
        // Returns filtered records or full list based on provided query parameters
        List<i10vx> records = i10vxCsvReaderService.readAndFilter(csvDirectoryPath, fileName, code, desc, validcode);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/da/display")
    public ResponseEntity<?> displayDaCsvContent(
            @RequestParam(required = false) String dagger,
            @RequestParam(required = false) String asterisk,
            @RequestParam(required = false) String pdx,
            @RequestParam(required = false) String sdx) {
        final String fileName = "da.csv";
        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: File '" + fileName + "' does not exist at path: " + fullPath.toAbsolutePath());
        }
        // Returns filtered records or full list based on provided query parameters
        List<da> records = daCsvReaderService.readAndFilter(csvDirectoryPath, fileName, dagger, asterisk, pdx, sdx);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/ccex/display")
    public ResponseEntity<?> displayCcexCsvContent(
            @RequestParam(required = false) String sdx,
            @RequestParam(required = false) String pdx) {
        final String fileName = "ccex.csv";
        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: File '" + fileName + "' does not exist at path: " + fullPath.toAbsolutePath());
        }
        // Returns filtered records or full list based on provided query parameters
        List<ccex> records = ccexCsvReaderService.readAndFilter(csvDirectoryPath, fileName, sdx, pdx);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/bmdc/display")
    public ResponseEntity<?> displayBmdcCsvContent(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String mdcmale,
            @RequestParam(required = false) String pdcmale,
            @RequestParam(required = false) String mdcfem,
            @RequestParam(required = false) String pdcfem,
            @RequestParam(required = false) String desc) {
        final String fileName = "bmdc.csv";
        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: File '" + fileName + "' does not exist at path: " + fullPath.toAbsolutePath());
        }
        // Returns filtered records or full list based on provided query parameters
        List<bmdc> records = bmdcCsvReaderService.readAndFilter(csvDirectoryPath, fileName, code, mdcmale, pdcmale,
                mdcfem, pdcfem, desc);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/i10/display")
    public ResponseEntity<?> displayI10CsvContent(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String mdc,
            @RequestParam(required = false) String pdc,
            @RequestParam(required = false) String cc,
            @RequestParam(required = false) String maincc,
            @RequestParam(required = false) String ccrow,
            @RequestParam(required = false) String hivax,
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) String accpdx,
            @RequestParam(required = false) String ageduse,
            @RequestParam(required = false) String agemin,
            @RequestParam(required = false) String agemax,
            @RequestParam(required = false) String agedmin) {
        final String fileName = "i10.csv";
        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: File '" + fileName + "' does not exist at path: " + fullPath.toAbsolutePath());
        }
        // Returns filtered records or full list based on provided query parameters
        List<i10> records = i10CsvReaderService.readAndFilter(csvDirectoryPath, fileName, code, mdc, pdc, cc, maincc,
                ccrow, hivax, sex, accpdx, ageduse, agemin, agemax, agedmin);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/dc/display")
    public ResponseEntity<?> displayDcCsvContent(
            @RequestParam(required = false) String mdc,
            @RequestParam(required = false) String dc,
            @RequestParam(required = false) String dccol,
            @RequestParam(required = false) String drgx,
            @RequestParam(required = false) String cname,
            @RequestParam(required = false) String drgx2) {
        final String fileName = "dc.csv";
        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: File '" + fileName + "' does not exist at path: " + fullPath.toAbsolutePath());
        }
        // Returns filtered records or full list based on provided query parameters
        List<dc> records = dcCsvReaderService.readAndFilter(csvDirectoryPath, fileName, mdc, dc, dccol, drgx, cname,
                drgx2);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/dcv/display")
    public ResponseEntity<?> displayDcvCsvContent(
            @RequestParam(required = false) String pdrg,
            @RequestParam(required = false) String fdrg) {
        final String fileName = "dcv.csv";
        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: File '" + fileName + "' does not exist at path: " + fullPath.toAbsolutePath());
        }
        // Returns filtered records or full list based on provided query parameters
        List<dcv> records = dcvCsvReaderService.readAndFilter(csvDirectoryPath, fileName, pdrg, fdrg);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/drg/display")
    public ResponseEntity<?> displayDrgCsvContent(
            @RequestParam(required = false) String mdc,
            @RequestParam(required = false) String dc,
            @RequestParam(required = false) String drg,
            @RequestParam(required = false) String rw,
            @RequestParam(required = false) String wtlos,
            @RequestParam(required = false) String ot,
            @RequestParam(required = false) String mdf,
            @RequestParam(required = false) String drgname) {
        final String fileName = "drg.csv";
        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: File '" + fileName + "' does not exist at path: " + fullPath.toAbsolutePath());
        }
        // Returns filtered records or full list based on provided query parameters
        List<drg> records = drgCsvReaderService.readAndFilter(csvDirectoryPath, fileName, mdc, dc, drg, rw, wtlos,
                ot, mdf, drgname);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/prpdc/display")
    public ResponseEntity<?> displayPrpdcCsvContent(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String mdc,
            @RequestParam(required = false) String pdc) {
        final String fileName = "prpdc.csv";
        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: File '" + fileName + "' does not exist at path: " + fullPath.toAbsolutePath());
        }
        // Returns filtered records or full list based on provided query parameters
        List<prpdc> records = prpdcCsvReaderService.readAndFilter(csvDirectoryPath, fileName,
                code, mdc, pdc);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/cclm/display")
    public ResponseEntity<?> displayCclmCsvContent(
            @RequestParam(required = false) String ccrow,
            @RequestParam(required = false) String ccl) {
        final String fileName = "cclm.csv";
        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: File '" + fileName + "' does not exist at path: " + fullPath.toAbsolutePath());
        }
        // Returns filtered records or full list based on provided query parameters
        List<cclm> records = cclmCsvReaderService.readAndFilter(csvDirectoryPath, fileName,
                ccrow, ccl);
        return ResponseEntity.ok(records);
    }
}
