package phil.grouper.csvreader;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.opencsv.bean.CsvToBeanBuilder;
import phil.grouper.csvbinder.ccex;

@Service
public class CcexCsvReaderService {
    /**
     * Builds and caches multi-field indexes for CCEX data:
     * - Key "SDX:{sdxVal}" -> List of items with that 'sdx'
     * - Key "PDX:{pdxVal}" -> List of items with that 'pdx'
     * - Key "BOTH:{sdx}_{pdx}" -> List of items matching both
     */
    @Cacheable(value = "ccexIndexedCache", key = "#fileName")
    public Map<String, List<ccex>> getIndexedCcexData(String csvDirectoryPath, String fileName) {
        System.out.println("--> Building Multi-Index for CCEX CSV from DISK...");

        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return Collections.emptyMap();
        }

        try (Reader reader = new FileReader(fullPath.toFile())) {
            List<ccex> list = new CsvToBeanBuilder<ccex>(reader)
                    .withType(ccex.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            Map<String, List<ccex>> indexMap = new HashMap<>();
            for (ccex item : list) {
                String sdxVal = (item.getSdx() != null) ? item.getSdx().trim().toUpperCase() : null;
                String pdxVal = (item.getPdx() != null) ? item.getPdx().trim().toUpperCase() : null;

                // Index by 'sdx'
                if (sdxVal != null && !sdxVal.isEmpty()) {
                    indexMap.computeIfAbsent("SDX:" + sdxVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'pdx'
                if (pdxVal != null && !pdxVal.isEmpty()) {
                    indexMap.computeIfAbsent("PDX:" + pdxVal, k -> new ArrayList<>()).add(item);
                }

                // Index by both 'sdx' and 'pdx'
                if (sdxVal != null && !sdxVal.isEmpty() && pdxVal != null && !pdxVal.isEmpty()) {
                    indexMap.computeIfAbsent("BOTH:" + sdxVal + "_" + pdxVal, k -> new ArrayList<>()).add(item);
                }

                // Keep a 'ALL' key for unfiltered queries
                indexMap.computeIfAbsent("ALL", k -> new ArrayList<>()).add(item);
            }

            return indexMap;

        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV file at " + fullPath, e);
        }
    }

    public List<ccex> readAndFilter(
        String csvDirectoryPath, 
        String fileName, 
        String sdxParam, 
        String pdxParam) {
        Map<String, List<ccex>> indexMap = getIndexedCcexData(csvDirectoryPath, fileName);

        boolean hasSdx = sdxParam != null && !sdxParam.trim().isEmpty();
        boolean hasPdx = pdxParam != null && !pdxParam.trim().isEmpty();

        @SuppressWarnings("null")
        String cleanSdx = hasSdx ? sdxParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanPdx = hasPdx ? pdxParam.trim().toUpperCase() : "";

        // Case 1: Search by both Sdx and Pdx
        if (hasSdx && hasPdx) {
            return indexMap.getOrDefault("BOTH:" + cleanSdx + "_" + cleanPdx, Collections.emptyList());
        }

        // Case 2: Search by Sdx only
        if (hasSdx) {
            return indexMap.getOrDefault("SDX:" + cleanSdx, Collections.emptyList());
        }
        // Case 3: Search by Pdx only
        if (hasPdx) {
            return indexMap.getOrDefault("PDX:" + cleanPdx, Collections.emptyList());
        }

        // Case 4: No params -> Return all records
        return indexMap.getOrDefault("ALL", Collections.emptyList());
    }

    public long getCcexRecordCount(String csvDirectoryPath, String fileName) {
        return getIndexedCcexData(csvDirectoryPath, fileName).getOrDefault("ALL", Collections.emptyList()).size();
    }
}
