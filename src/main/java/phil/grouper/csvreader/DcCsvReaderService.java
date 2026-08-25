package phil.grouper.csvreader;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import com.opencsv.bean.CsvToBeanBuilder;

import phil.grouper.csvbinder.dc;

@Service
public class DcCsvReaderService {

    /**
     * Builds and caches multi-field indexes for Drg data:
     * - Key "CODE:{codeVal}" -> List of items with that 'code'
     * - Key "DESC:{descVal}" -> List of items with that 'desc'
     * - Key "BOTH:{code}_{desc}" -> List of items matching both
     */
    @Cacheable(value = "dcIndexedCache", key = "#fileName")
    public Map<String, List<dc>> getIndexedDcData(String csvDirectoryPath, String fileName) {
        System.out.println("--> Building Multi-Index for  DC from DISK...");

        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return Collections.emptyMap();
        }

        try (Reader reader = new FileReader(fullPath.toFile())) {
            List<dc> list = new CsvToBeanBuilder<dc>(reader)
                    .withType(dc.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            Map<String, List<dc>> indexMap = new HashMap<>();
            for (dc item : list) {
                String mdcVal = (item.getMdc() != null) ? item.getMdc().trim().toUpperCase() : null;
                String dcVal = (item.getDc() != null) ? item.getDc().trim().toUpperCase() : null;
                String dccolVal = (item.getDccol() != null) ? item.getDccol().trim().toUpperCase() : null;
                String drgxVal = (item.getDrgx() != null) ? item.getDrgx().trim().toUpperCase() : null;
                String cnameVal = (item.getCname() != null) ? item.getCname().trim().toUpperCase() : null;
                String drgx2Val = (item.getDrgx2() != null) ? item.getDrgx().trim().toUpperCase() : null;

                // Index by 'code'
                if (mdcVal != null && !mdcVal.isEmpty()) {
                    indexMap.computeIfAbsent("MDC:" + mdcVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'dc'
                if (dcVal != null && !dcVal.isEmpty()) {
                    indexMap.computeIfAbsent("DC:" + dcVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'dccol'
                if (dccolVal != null && !dccolVal.isEmpty()) {
                    indexMap.computeIfAbsent("DCCOL:" + dccolVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'drgx'
                if (drgxVal != null && !drgxVal.isEmpty()) {
                    indexMap.computeIfAbsent("DRGX:" + drgxVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'cname'
                if (cnameVal != null && !cnameVal.isEmpty()) {
                    indexMap.computeIfAbsent("CNAME:" + cnameVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'drgx2'
                if (drgx2Val != null && !drgx2Val.isEmpty()) {
                    indexMap.computeIfAbsent("DRGX2:" + drgx2Val, k -> new ArrayList<>()).add(item);
                }
                // Index by both 'mdc','mdcmale','pdcmale', 'mdcfem','pdcfem','desc' if
                // applicable
                if (mdcVal != null && !mdcVal.isEmpty() &&
                        dcVal != null && !dcVal.isEmpty() &&
                        dccolVal != null && !dccolVal.isEmpty() &&
                        drgxVal != null && !drgxVal.isEmpty() &&
                        cnameVal != null && !cnameVal.isEmpty() &&
                        drgx2Val != null && !drgx2Val.isEmpty()) {
                    indexMap.computeIfAbsent("BOTH:"
                            + mdcVal + "_"
                            + dcVal + "_"
                            + dccolVal + "_"
                            + drgxVal + "_"
                            + cnameVal + "_"
                            + drgx2Val, k -> new ArrayList<>())
                            .add(item);
                }

                // Keep a 'ALL' key for unfiltered queries
                indexMap.computeIfAbsent("ALL", k -> new ArrayList<>()).add(item);
            }

            return indexMap;

        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV file at " + fullPath, e);
        }
    }

    public List<dc> readAndFilter(
            String csvDirectoryPath,
            String fileName,
            String mdcParam,
            String dcParam,
            String dccolParam,
            String drgxParam,
            String cnameParam,
            String drgx2Param) {
        Map<String, List<dc>> indexMap = getIndexedDcData(csvDirectoryPath, fileName);
        boolean hasMdc = mdcParam != null && !mdcParam.trim().isEmpty();
        boolean hasDc = dcParam != null && !dcParam.trim().isEmpty();
        boolean hasDccol = dccolParam != null && !dccolParam.trim().isEmpty();
        boolean hasDrgx = drgxParam != null && !drgxParam.trim().isEmpty();
        boolean hasCname = cnameParam != null && !cnameParam.trim().isEmpty();
        boolean hasDrgx2 = drgx2Param != null && !drgx2Param.trim().isEmpty();

        @SuppressWarnings("null")
        String cleanMdc = hasMdc ? mdcParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanDc = hasDc ? dcParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanDccol = hasDccol ? dccolParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanDrgx = hasDrgx ? drgxParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanCname = hasCname ? cnameParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanDrgx2 = hasDrgx2 ? drgx2Param.trim().toUpperCase() : "";

        // Case 1: Search by all parameters
        if (hasMdc && hasDc && hasDccol && hasDrgx && hasCname && hasDrgx2) {
            return indexMap.getOrDefault("BOTH:"
                    + cleanMdc + "_"
                    + cleanDc + "_"
                    + cleanDccol + "_"
                    + cleanDrgx + "_"
                    + cleanCname + "_"
                    + cleanDrgx2, Collections.emptyList());
        }
        // Case 2: Search by MDC only
        if (hasMdc) {
            return indexMap.getOrDefault("MDC:" + hasMdc, Collections.emptyList());
        }
        // Case 3: Search by DC only
        if (hasDc) {
            return indexMap.getOrDefault("DC:" + hasDc, Collections.emptyList());
        }
        // Case 4: Search by DRG only
        if (hasDccol) {
            return indexMap.getOrDefault("DCCOL:" + hasDccol, Collections.emptyList());
        }
        // Case 5: Search by RW only
        if (hasDrgx) {
            return indexMap.getOrDefault("DRGX:" + hasDrgx, Collections.emptyList());
        }
        // Case 6: Search by WTLOS only
        if (hasCname) {
            return indexMap.getOrDefault("CNAME:" + hasCname, Collections.emptyList());
        }
        // Case 7: Search by OT only
        if (hasDrgx2) {
            return indexMap.getOrDefault("DRGX2:" + hasDrgx2, Collections.emptyList());
        }
        // Case 10: No params -> Return all records
        return indexMap.getOrDefault("ALL", Collections.emptyList());
    }

    public long getDcRecordCount(String csvDirectoryPath, String fileName) {
        return getIndexedDcData(csvDirectoryPath, fileName).getOrDefault("ALL", Collections.emptyList()).size();
    }

}
