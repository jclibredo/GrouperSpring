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
import phil.grouper.csvbinder.prpdc;

@Service
public class PrpdcCsvReaderService {

    @Cacheable(value = "prpdcIndexedCache", key = "#fileName")
    public Map<String, List<prpdc>> getIndexedPrpdcData(String csvDirectoryPath, String fileName) {
        System.out.println("--> Building Multi-Index for PRPDC CSV from DISK...");
        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return Collections.emptyMap();
        }
        try (Reader reader = new FileReader(fullPath.toFile())) {
            List<prpdc> list = new CsvToBeanBuilder<prpdc>(reader)
                    .withType(prpdc.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
            Map<String, List<prpdc>> indexMap = new HashMap<>();
            for (prpdc item : list) {
                String codeVal = (item.getCode() != null) ? item.getCode().trim().toUpperCase() : null;
                String mdcVal = (item.getMdc() != null) ? item.getMdc().trim().toUpperCase() : null;
                String pdcVal = (item.getPdc() != null) ? item.getPdc().trim().toUpperCase() : null;
                // Index by 'code'
                if (codeVal != null && !codeVal.isEmpty()) {
                    indexMap.computeIfAbsent("CODE:" + codeVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'mdc'
                if (mdcVal != null && !mdcVal.isEmpty()) {
                    indexMap.computeIfAbsent("MDC:" + mdcVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'pdc'
                if (pdcVal != null && !pdcVal.isEmpty()) {
                    indexMap.computeIfAbsent("PDC:" + pdcVal, k -> new ArrayList<>()).add(item);
                }
                // Index by both 'mdc' and 'code'
                if (mdcVal != null && !mdcVal.isEmpty() && codeVal != null && !codeVal.isEmpty()) {
                    indexMap.computeIfAbsent("BOTH:" + mdcVal + "_" + codeVal, k -> new ArrayList<>()).add(item);
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
    public List<prpdc> readAndFilter(String csvDirectoryPath, String fileName, String codeParam, String mdcParam,
            String pdcParam) {
        Map<String, List<prpdc>> indexMap = getIndexedPrpdcData(csvDirectoryPath, fileName);
        boolean hasCode = codeParam != null && !codeParam.trim().isEmpty();
        boolean hasMdc = mdcParam != null && !mdcParam.trim().isEmpty();
        boolean hasPdc = pdcParam != null && !pdcParam.trim().isEmpty();
        @SuppressWarnings("null")
        String cleanCode = hasCode ? codeParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanMdc = hasMdc ? mdcParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanPdc = hasPdc ? pdcParam.trim().toUpperCase() : "";

        // Case 1: Search by both MDC and PDC
        if (hasCode && hasMdc && hasPdc) {
            return indexMap.getOrDefault("BOTH:" + cleanCode + "_" + cleanMdc + "_" + cleanPdc,
                    Collections.emptyList());
        }

        // Case 2: Search by MDC only
        if (hasCode) {
            return indexMap.getOrDefault("CODE:" + cleanCode, Collections.emptyList());
        }

        // Case 3: Search by MDC only
        if (hasMdc) {
            return indexMap.getOrDefault("MDC:" + cleanMdc, Collections.emptyList());
        }

        // Case 4: Search by PDC only
        if (hasPdc) {
            return indexMap.getOrDefault("PDC:" + cleanPdc, Collections.emptyList());
        }

        // Case 5: No params -> Return all records
        return indexMap.getOrDefault("ALL", Collections.emptyList());
    }

    public long getPrpdcRecordCount(String csvDirectoryPath, String fileName) {
        return getIndexedPrpdcData(csvDirectoryPath, fileName).getOrDefault("ALL", Collections.emptyList()).size();
    }
}
