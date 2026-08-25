package phil.grouper.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Value; // IMPORTANT: Must be @Controller
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import phil.grouper.csvreader.AxCsvReaderService;
import phil.grouper.csvreader.I10CsvReaderService;
import phil.grouper.csvreader.I10vxCsvReaderService;
import phil.grouper.csvreader.DrgCsvReaderService;
import phil.grouper.csvreader.DcCsvReaderService;
import phil.grouper.csvreader.DcvCsvReaderService;
import phil.grouper.csvreader.DaCsvReaderService;
import phil.grouper.csvreader.CcexCsvReaderService;

@Controller
public class DashboardViewController {

    @Value("${grouper.csv.dir}")
    private String csvDirectoryPath;

    private final AxCsvReaderService csvReaderService;

    private final I10vxCsvReaderService i10vxCsvReaderService;

    private final I10CsvReaderService i10CsvReaderService;

    private final DrgCsvReaderService drgCsvReaderService;

    private final DcCsvReaderService dcCsvReaderService;

    private final DcvCsvReaderService dcvCsvReaderService;

    private final DaCsvReaderService daCsvReaderService;

    private final CcexCsvReaderService ccexCsvReaderService;

    public DashboardViewController(AxCsvReaderService csvReaderService, I10vxCsvReaderService i10vxCsvReaderService,
            I10CsvReaderService i10CsvReaderService, DrgCsvReaderService drgCsvReaderService,
            DcCsvReaderService dcCsvReaderService,
            DcvCsvReaderService dcvCsvReaderService,
            DaCsvReaderService daCsvReaderService,
            CcexCsvReaderService ccexCsvReaderService) {
        this.csvReaderService = csvReaderService;
        this.i10vxCsvReaderService = i10vxCsvReaderService;
        this.i10CsvReaderService = i10CsvReaderService;
        this.drgCsvReaderService = drgCsvReaderService;
        this.dcCsvReaderService = dcCsvReaderService;
        this.dcvCsvReaderService = dcvCsvReaderService;
        this.daCsvReaderService = daCsvReaderService;
        this.ccexCsvReaderService = ccexCsvReaderService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        // Fetch row count from CSV
        long axCount = csvReaderService.getAxRecordCount(csvDirectoryPath, "ax.csv");
        long i10vxCount = i10vxCsvReaderService.getI10vxRecordCount(csvDirectoryPath, "i10vx.csv");
        long i10Count = i10CsvReaderService.getI10RecordCount(csvDirectoryPath, "i10.csv");
        long drgCount = drgCsvReaderService.getDrgRecordCount(csvDirectoryPath, "drg.csv");
        long dcCount = dcCsvReaderService.getDcRecordCount(csvDirectoryPath, "drg.csv");
        long dcvCount = dcvCsvReaderService.getDcvRecordCount(csvDirectoryPath, "drg.csv");
        long daCount = daCsvReaderService.getDaRecordCount(csvDirectoryPath, "da.csv");
        long ccexCount = ccexCsvReaderService.getCcexRecordCount(csvDirectoryPath, "ccex.csv");
        model.addAttribute("axCount", axCount);
        model.addAttribute("i10vxCount", i10vxCount);
        model.addAttribute("i10Count", i10Count);
        model.addAttribute("drgCount", drgCount);
        model.addAttribute("dcCount", dcCount);
        model.addAttribute("dcvCount", dcvCount);
        model.addAttribute("daCount", daCount);
        model.addAttribute("ccexCount", ccexCount);

        // Resolves src/main/resources/templates/dashboard.html
        return "dashboard";
    }

    // ADD THIS METHOD: Renders the I10vx HTML page
    @GetMapping("/i10vx")
    public String showI10vxPage() {
        return "i10vx"; // Renders templates/i10vx.html
    }

    @GetMapping("/ax")
    public String showAxPage() {
        return "ax"; // Renders templates/ax.html
    }

    @GetMapping("/da")
    public String showDaPage() {
        return "da"; // Renders templates/da.html
    }

    @GetMapping("/ccex")
    public String showCcexPage() {
        return "ccex"; // Renders templates/ccex.html
    }

    @GetMapping("/bmdc")
    public String showBmdcPage() {
        return "bmdc"; // Renders templates/bmdc.html
    }

    @GetMapping("/i10")
    public String showI10Page() {
        return "i10"; // Renders templates/i10.html
    }

    @GetMapping("/drg")
    public String showDrgPage() {
        return "drg"; // Renders templates/drg.html
    }

    @GetMapping("/dcv")
    public String showDcvPage() {
        return "dcv"; // Renders templates/dcv.html
    }

    @GetMapping("/dc")
    public String showDcPage() {
        return "dc"; // Renders templates/dcv.html
    }

}