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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.opencsv.bean.CsvToBeanBuilder;

import phil.grouper.csvbinder.ax;
import phil.grouper.entity.DRGWSResult;
import phil.grouper.utility.Utility;

@Service
public class AxCsvReaderService {

    /**
     * Builds and caches multi-field indexes for AX data:
     * - Key "AX:{axVal}" -> List of items with that 'ax'
     * - Key "CODE:{codeVal}" -> List of items with that 'code'
     * - Key "BOTH:{ax}_{code}" -> List of items matching both
     */
    @Value("${grouper.csv.dir}")
    private String csvDirectoryPath;


    private final Utility utility;

    public AxCsvReaderService(
            Utility utility) {
        this.utility = utility;
    }

    String fileName = "ax.csv";

    @Cacheable(value = "axIndexedCache", key = "#fileName")
    public Map<String, List<ax>> getIndexedAxData() {
        System.out.println("--> Building Multi-Index for AX CSV from DISK...");
        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return Collections.emptyMap();
        }
        try (Reader reader = new FileReader(fullPath.toFile())) {
            List<ax> list = new CsvToBeanBuilder<ax>(reader)
                    .withType(ax.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
            Map<String, List<ax>> indexMap = new HashMap<>();
            for (ax item : list) {
                String axVal = (item.getAx() != null) ? item.getAx().trim().toUpperCase() : null;
                String codeVal = (item.getCode() != null) ? item.getCode().trim().toUpperCase() : null;
                // Index by 'ax'
                if (axVal != null && !axVal.isEmpty()) {
                    indexMap.computeIfAbsent("AX:" + axVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'code'
                if (codeVal != null && !codeVal.isEmpty()) {
                    indexMap.computeIfAbsent("CODE:" + codeVal, k -> new ArrayList<>()).add(item);
                }
                // Index by both 'ax' and 'code'
                if (axVal != null && !axVal.isEmpty() && codeVal != null && !codeVal.isEmpty()) {
                    indexMap.computeIfAbsent("BOTH:" + axVal + "_" + codeVal, k -> new ArrayList<>()).add(item);
                }
                // Keep a 'ALL' key for unfiltered queries
                indexMap.computeIfAbsent("ALL", k -> new ArrayList<>()).add(item);
            }
            System.out.println(utility.objectMapper().writeValueAsString(indexMap));
            return indexMap;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV file at " + fullPath, e);
        }
    }

    /**
     * O(1) Instant Lookup using cached multi-indexes
     */
    public List<ax> readAndFilter(String axParam, String codeParam) {
        Map<String, List<ax>> indexMap = getIndexedAxData();
        boolean hasAx = axParam != null && !axParam.trim().isEmpty();
        boolean hasCode = codeParam != null && !codeParam.trim().isEmpty();
        @SuppressWarnings("null")
        String cleanAx = hasAx ? axParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanCode = hasCode ? codeParam.trim().toUpperCase() : "";
        // Case 1: Search by both AX and CODE
        if (hasAx && hasCode) {
            return indexMap.getOrDefault("BOTH:" + cleanAx + "_" + cleanCode, Collections.emptyList());
        }
        // Case 2: Search by AX only
        if (hasAx) {
            return indexMap.getOrDefault("AX:" + cleanAx, Collections.emptyList());
        }
        // Case 3: Search by CODE only
        if (hasCode) {
            return indexMap.getOrDefault("CODE:" + cleanCode, Collections.emptyList());
        }
        // Case 4: No params -> Return all records
        return indexMap.getOrDefault("ALL", Collections.emptyList());
    }

    public long getAxRecordCount() {
        return getIndexedAxData().getOrDefault("ALL", Collections.emptyList()).size();
    }

    public DRGWSResult AX(
            final AxCsvReaderService axCsvReaderService,
            final String axcodes,
            final String requestcode) {
        DRGWSResult result = utility.DRGWSResult();
        result.setMessage("");
        result.setResult("");
        result.setSuccess(false);

        try {
            String cleanAx = axcodes != null ? axcodes.trim() : "";
            String cleanCode = requestcode != null ? requestcode.trim() : "";

            List<ax> matches = axCsvReaderService.readAndFilter(
                    cleanAx,
                    cleanCode);

            if (matches != null && !matches.isEmpty()) {
                // THIS LINE IS FOR OLD LIBRARY
                result.setResult(cleanCode);
                result.setSuccess(true);
            }
        } catch (Exception ex) {
            result.setMessage("Something went wrong AX");
            Logger.getLogger(AxCsvReaderService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }
}