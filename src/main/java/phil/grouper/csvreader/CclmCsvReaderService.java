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
import phil.grouper.csvbinder.cclm;

@Service
public class CclmCsvReaderService {

    @Cacheable(value = "cclmIndexedCache", key = "#fileName")
    public Map<String, List<cclm>> getIndexedCclmData(String csvDirectoryPath, String fileName) {
        System.out.println("--> Building Multi-Index for CCLM CSV from DISK...");
        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return Collections.emptyMap();
        }
        try (Reader reader = new FileReader(fullPath.toFile())) {
            List<cclm> list = new CsvToBeanBuilder<cclm>(reader)
                    .withType(cclm.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
            Map<String, List<cclm>> indexMap = new HashMap<>();
            for (cclm item : list) {
                String ccrowVal = (item.getCcrow() != null) ? item.getCcrow().trim().toUpperCase() : null;
                String cclVal = (item.getCcl() != null) ? item.getCcl().trim().toUpperCase() : null;
                // Index by 'ccrow'
                if (ccrowVal != null && !ccrowVal.isEmpty()) {
                    indexMap.computeIfAbsent("CCROW:" + ccrowVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'ccl'
                if (cclVal != null && !cclVal.isEmpty()) {
                    indexMap.computeIfAbsent("CCL:" + cclVal, k -> new ArrayList<>()).add(item);
                }
                // Index by both 'ccrow' and 'ccl'
                if (ccrowVal != null && !ccrowVal.isEmpty() && cclVal != null && !cclVal.isEmpty()) {
                    indexMap.computeIfAbsent("BOTH:" + ccrowVal + "_" + cclVal, k -> new ArrayList<>()).add(item);
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
    public List<cclm> readAndFilter(String csvDirectoryPath, String fileName, String ccrowParam, String cclParam) {
        Map<String, List<cclm>> indexMap = getIndexedCclmData(csvDirectoryPath, fileName);
        boolean hasCcrow = ccrowParam != null && !ccrowParam.trim().isEmpty();
        boolean hasCcl = cclParam != null && !cclParam.trim().isEmpty();
        @SuppressWarnings("null")
        String cleanCcrow = hasCcrow ? ccrowParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanCcl = hasCcl ? cclParam.trim().toUpperCase() : "";
        // Case 1: Search by both CCROW and CCL
        if (hasCcrow && hasCcl) {
            return indexMap.getOrDefault("BOTH:" + cleanCcrow + "_" + cleanCcl,
                    Collections.emptyList());
        }
        // Case 2: Search by CCROW only
        if (hasCcrow) {
            return indexMap.getOrDefault("CCROW:" + cleanCcrow, Collections.emptyList());
        }
        // Case 3: Search by CCL only
        if (hasCcl) {
            return indexMap.getOrDefault("CCL:" + cleanCcl, Collections.emptyList());
        }
        // Case 5: No params -> Return all records
        return indexMap.getOrDefault("ALL", Collections.emptyList());
    }

    public long getRecordCount(String csvDirectoryPath, String fileName) {
        return getIndexedCclmData(csvDirectoryPath, fileName).getOrDefault("ALL", Collections.emptyList()).size();
    }

}
