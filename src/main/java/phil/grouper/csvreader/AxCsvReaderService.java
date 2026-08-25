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

import phil.grouper.csvbinder.ax;

@Service
public class AxCsvReaderService {

    /**
     * Builds and caches multi-field indexes for AX data:
     * - Key "AX:{axVal}"        -> List of items with that 'ax'
     * - Key "CODE:{codeVal}"    -> List of items with that 'code'
     * - Key "BOTH:{ax}_{code}"  -> List of items matching both
     */
    @Cacheable(value = "axIndexedCache", key = "#fileName")
    public Map<String, List<ax>> getIndexedAxData(String csvDirectoryPath, String fileName) {
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

            return indexMap;

        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV file at " + fullPath, e);
        }
    }

    /**
     * O(1) Instant Lookup using cached multi-indexes
     */
    public List<ax> readAndFilter(String csvDirectoryPath, String fileName, String axParam, String codeParam) {
        Map<String, List<ax>> indexMap = getIndexedAxData(csvDirectoryPath, fileName);

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

    public long getAxRecordCount(String csvDirectoryPath, String fileName) {
        return getIndexedAxData(csvDirectoryPath, fileName).getOrDefault("ALL", Collections.emptyList()).size();
    }
}