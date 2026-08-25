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

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.opencsv.bean.CsvToBeanBuilder;

import phil.grouper.csvbinder.drg;

@Service
public class DrgCsvReaderService {
    /**
     * Builds and caches multi-field indexes for Drg data:
     * - Key "CODE:{codeVal}" -> List of items with that 'code'
     * - Key "DESC:{descVal}" -> List of items with that 'desc'
     * - Key "BOTH:{code}_{desc}" -> List of items matching both
     */
    @Cacheable(value = "drgIndexedCache", key = "#fileName")
    public Map<String, List<drg>> getIndexedDrgData(String csvDirectoryPath, String fileName) {
        System.out.println("--> Building Multi-Index for Drg CSV from DISK...");

        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return Collections.emptyMap();
        }

        try (Reader reader = new FileReader(fullPath.toFile())) {
            List<drg> list = new CsvToBeanBuilder<drg>(reader)
                    .withType(drg.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            Map<String, List<drg>> indexMap = new HashMap<>();
            for (drg item : list) {
                String mdcVal       = (item.getMdc()        != null) ? item.getMdc().trim().toUpperCase() : null;
                String dcVal        = (item.getDc()         != null) ? item.getDc().trim().toUpperCase() : null;
                String drgVal       = (item.getDrg()        != null) ? item.getDrg().trim().toUpperCase() : null;
                String rwVal        = (item.getRw()         != null) ? item.getRw().trim().toUpperCase() : null;
                String wtlosVal     = (item.getWtlos()      != null) ? item.getWtlos().trim().toUpperCase() : null;
                String otVal        = (item.getOt()         != null) ? item.getOt().trim().toUpperCase() : null;
                String mdfVal       = (item.getMdf()        != null) ? item.getMdf().trim().toUpperCase() : null;
                String drgnameVal   = (item.getDrgname()    != null) ? item.getDrgname().trim().toUpperCase() : null;

                // Index by 'code'
                if (mdcVal != null && !mdcVal.isEmpty()) {
                    indexMap.computeIfAbsent("MDC:" + mdcVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'dc'
                if (dcVal != null && !dcVal.isEmpty()) {
                    indexMap.computeIfAbsent("DC:" + dcVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'drg'
                if (drgVal != null && !drgVal.isEmpty()) {
                    indexMap.computeIfAbsent("DRG:" + drgVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'RW'
                if (rwVal != null && !rwVal.isEmpty()) {
                    indexMap.computeIfAbsent("RW:" + rwVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'WTLOS'
                if (wtlosVal != null && !wtlosVal.isEmpty()) {
                    indexMap.computeIfAbsent("WTLOS:" + wtlosVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'OT'
                if (otVal != null && !otVal.isEmpty()) {
                    indexMap.computeIfAbsent("OT:" + otVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'MDF'
                if (mdfVal != null && !mdfVal.isEmpty()) {
                    indexMap.computeIfAbsent("MDF:" + mdfVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'DRGNAME'
                if (drgnameVal != null && !drgnameVal.isEmpty()) {
                    indexMap.computeIfAbsent("DRGNAME:" + drgnameVal, k -> new ArrayList<>()).add(item);
                }
                // Index by both 'mdc','mdcmale','pdcmale', 'mdcfem','pdcfem','desc' if
                // applicable
                if (    mdcVal      != null && !mdcVal.isEmpty() &&
                        dcVal       != null && !dcVal.isEmpty() &&
                        drgVal      != null && !drgVal.isEmpty() &&
                        rwVal       != null && !rwVal.isEmpty() &&
                        wtlosVal    != null && !wtlosVal.isEmpty() &&
                        otVal       != null && !otVal.isEmpty() &&
                        mdfVal      != null && !mdfVal.isEmpty() &&
                        drgnameVal  != null && !drgnameVal.isEmpty()) {
                    indexMap.computeIfAbsent("BOTH:" 
                    + mdcVal + "_" 
                    + dcVal + "_" 
                    + drgVal + "_" 
                    + rwVal+ "_" 
                    + wtlosVal + "_" 
                    + otVal + "_" 
                    + mdfVal + "_" 
                    + drgnameVal, k -> new ArrayList<>())
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

    public List<drg> readAndFilter(
            String csvDirectoryPath,
            String fileName,
            String mdcParam,
            String dcParam,
            String drgParam,
            String rwParam,
            String wtlosParam,
            String otParam,
            String mdfParam,
            String drgnameParam) {
        Map<String, List<drg>> indexMap = getIndexedDrgData(csvDirectoryPath, fileName);
        boolean hasMdc = mdcParam != null && !mdcParam.trim().isEmpty();
        boolean hasDc = dcParam != null && !dcParam.trim().isEmpty();
        boolean hasDrg = drgParam != null && !drgParam.trim().isEmpty();
        boolean hasRw = rwParam != null && !rwParam.trim().isEmpty();
        boolean hasWtlos = wtlosParam != null && !wtlosParam.trim().isEmpty();
        boolean hasOt = otParam != null && !otParam.trim().isEmpty();
        boolean hasMdf = mdfParam != null && !mdfParam.trim().isEmpty();
        boolean hasDrgname = drgnameParam != null && !drgnameParam.trim().isEmpty();

        @SuppressWarnings("null")
        String cleanMdc = hasMdc ? mdcParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanDc = hasDc ? dcParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanDrg = hasDrg ? drgParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanRw = hasRw ? rwParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanWtlos = hasWtlos ? wtlosParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanOt = hasOt ? otParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanMdf = hasMdf ? mdfParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanDrgname = hasDrgname ? drgnameParam.trim().toUpperCase() : "";

        // Case 1: Search by all parameters
        if (hasMdc && hasDc && hasDrg && hasRw && hasWtlos && hasOt && hasMdf && hasDrgname) {
            return indexMap.getOrDefault("BOTH:"
                    + cleanMdc + "_"
                    + cleanDc + "_"
                    + cleanDrg + "_"
                    + cleanRw + "_"
                    + cleanWtlos + "_"
                    + cleanOt + "_" 
                    + cleanMdf + "_" 
                    + cleanDrgname, Collections.emptyList());
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
        if (hasDrg) {
            return indexMap.getOrDefault("DRG:" + hasDrg, Collections.emptyList());
        }
        // Case 5: Search by RW only
        if (hasRw) {
            return indexMap.getOrDefault("RW:" + hasRw, Collections.emptyList());
        }
        // Case 6: Search by WTLOS only
        if (hasWtlos) {
            return indexMap.getOrDefault("WTLOS:" + hasWtlos, Collections.emptyList());
        }
        // Case 7: Search by OT only
        if (hasOt) {
            return indexMap.getOrDefault("OT:" + hasOt, Collections.emptyList());
        }
        // Case 8: Search by OT only
        if (hasMdf) {
            return indexMap.getOrDefault("MDF:" + hasMdf, Collections.emptyList());
        }
        // Case 9: Search by OT only
        if (hasDrgname) {
            return indexMap.getOrDefault("DRGNAME:" + hasDrgname, Collections.emptyList());
        }
        // Case 10: No params -> Return all records
        return indexMap.getOrDefault("ALL", Collections.emptyList());
    }

    public long getDrgRecordCount(String csvDirectoryPath, String fileName) {
        return getIndexedDrgData(csvDirectoryPath, fileName).getOrDefault("ALL", Collections.emptyList()).size();
    }
}
