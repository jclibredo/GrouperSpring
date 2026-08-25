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

import phil.grouper.csvbinder.dcv;

@Service
public class DcvCsvReaderService {
    /**
     * Builds and caches multi-field indexes for AX data:
     * - Key "AX:{axVal}" -> List of items with that 'ax'
     * - Key "CODE:{codeVal}" -> List of items with that 'code'
     * - Key "BOTH:{ax}_{code}" -> List of items matching both
     */
    @Cacheable(value = "dcvIndexedCache", key = "#fileName")
    public Map<String, List<dcv>> getIndexedDcvData(String csvDirectoryPath, String fileName) {
        System.out.println("--> Building Multi-Index for DCV CSV from DISK...");

        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return Collections.emptyMap();
        }

        try (Reader reader = new FileReader(fullPath.toFile())) {
            List<dcv> list = new CsvToBeanBuilder<dcv>(reader)
                    .withType(dcv.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            Map<String, List<dcv>> indexMap = new HashMap<>();

            for (dcv item : list) {
                String pdrgVal = (item.getPdrg() != null) ? item.getPdrg().trim().toUpperCase() : null;
                String fdrgVal = (item.getFdrg() != null) ? item.getFdrg().trim().toUpperCase() : null;

                // Index by 'PDRG'
                if (pdrgVal != null && !pdrgVal.isEmpty()) {
                    indexMap.computeIfAbsent("PDRG:" + pdrgVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'FDRG'
                if (fdrgVal != null && !fdrgVal.isEmpty()) {
                    indexMap.computeIfAbsent("FDRG:" + fdrgVal, k -> new ArrayList<>()).add(item);
                }

                // Index by both 'ax' and 'code'
                if (pdrgVal != null && !pdrgVal.isEmpty() && fdrgVal != null && !fdrgVal.isEmpty()) {
                    indexMap.computeIfAbsent("BOTH:" + pdrgVal + "_" + fdrgVal, k -> new ArrayList<>()).add(item);
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
    public List<dcv> readAndFilter(
            String csvDirectoryPath,
            String fileName,
            String pdrgParam,
            String fdrgParam) {
        Map<String, List<dcv>> indexMap = getIndexedDcvData(csvDirectoryPath, fileName);

        boolean hasPdrg = pdrgParam != null && !pdrgParam.trim().isEmpty();
        boolean hasFdrg = fdrgParam != null && !fdrgParam.trim().isEmpty();

        @SuppressWarnings("null")
        String cleanPdrg = hasPdrg ? pdrgParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanFdrg = hasFdrg ? fdrgParam.trim().toUpperCase() : "";

        // Case 1: Search by both AX and CODE
        if (hasPdrg && hasFdrg) {
            return indexMap.getOrDefault("BOTH:" + cleanPdrg + "_" + cleanFdrg, Collections.emptyList());
        }

        // Case 2: Search by PDRG only
        if (hasPdrg) {
            return indexMap.getOrDefault("PDRG:" + hasPdrg, Collections.emptyList());
        }

        // Case 3: Search by FDRG only
        if (hasFdrg) {
            return indexMap.getOrDefault("FDRG:" + hasFdrg, Collections.emptyList());
        }

        // Case 4: No params -> Return all records
        return indexMap.getOrDefault("ALL", Collections.emptyList());
    }

    public long getDcvRecordCount(String csvDirectoryPath, String fileName) {
        return getIndexedDcvData(csvDirectoryPath, fileName).getOrDefault("ALL", Collections.emptyList()).size();
    }
}
