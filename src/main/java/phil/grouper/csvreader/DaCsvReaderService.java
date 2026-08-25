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

import phil.grouper.csvbinder.da;

@Service
public class DaCsvReaderService {
    /**
     * Builds and caches multi-field indexes for AX data:
     * - Key "AX:{axVal}" -> List of items with that 'ax'
     * - Key "CODE:{codeVal}" -> List of items with that 'code'
     * - Key "BOTH:{ax}_{code}" -> List of items matching both
     */
    @Cacheable(value = "daIndexedCache", key = "#fileName")
    public Map<String, List<da>> getIndexedDaData(String csvDirectoryPath, String fileName) {
        System.out.println("--> Building Multi-Index for AX CSV from DISK...");

        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return Collections.emptyMap();
        }

        try (Reader reader = new FileReader(fullPath.toFile())) {
            List<da> list = new CsvToBeanBuilder<da>(reader)
                    .withType(da.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
            Map<String, List<da>> indexMap = new HashMap<>();
            for (da item : list) {
                String daggerVal = (item.getDagger() != null) ? item.getDagger().trim().toUpperCase() : null;
                String asteriskVal = (item.getAsterisk() != null) ? item.getAsterisk().trim().toUpperCase() : null;
                String pdxVal = (item.getPdx() != null) ? item.getPdx().trim().toUpperCase() : null;
                String sdxVal = (item.getSdx() != null) ? item.getSdx().trim().toUpperCase() : null;

                // Index by 'ax'
                if (daggerVal != null && !daggerVal.isEmpty()) {
                    indexMap.computeIfAbsent("AX:" + daggerVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'code'
                if (pdxVal != null && !pdxVal.isEmpty()) {
                    indexMap.computeIfAbsent("PDX:" + pdxVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'code'
                if (asteriskVal != null && !asteriskVal.isEmpty()) {
                    indexMap.computeIfAbsent("ASTERISK:" + asteriskVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'code'
                if (sdxVal != null && !sdxVal.isEmpty()) {
                    indexMap.computeIfAbsent("SDX:" + sdxVal, k -> new ArrayList<>()).add(item);
                }

                // Index by both 'ax' and 'code'
                if (daggerVal != null && !daggerVal.isEmpty() && asteriskVal != null && !asteriskVal.isEmpty() && pdxVal != null && !pdxVal.isEmpty() && sdxVal != null && !sdxVal.isEmpty()) {
                    indexMap.computeIfAbsent("BOTH:" + daggerVal + "_" + asteriskVal + "_" + pdxVal + "_" + sdxVal, k -> new ArrayList<>()).add(item);
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
    public List<da> readAndFilter(
            String csvDirectoryPath,
            String fileName,
            String daggerParam,
            String asteriskParam,
            String pdxParam,
            String sdxParam) {
        Map<String, List<da>> indexMap = getIndexedDaData(csvDirectoryPath, fileName);

        boolean hasDagger   = daggerParam != null && !daggerParam.trim().isEmpty();
        boolean hasAsterisk = asteriskParam != null && !asteriskParam.trim().isEmpty();
        boolean hasPdx      = pdxParam != null && !pdxParam.trim().isEmpty();
        boolean hasSdx      = sdxParam != null && !sdxParam.trim().isEmpty();

        @SuppressWarnings("null")
        String cleanDagger = hasDagger ? daggerParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanAsterisk = hasAsterisk ? asteriskParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanPdx = hasPdx ? pdxParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanSdx = hasSdx ? sdxParam.trim().toUpperCase() : "";

        // Case 1: Search by both DAGGER and ASTERISK
        if (hasDagger && hasAsterisk && hasPdx && hasSdx) {
            return indexMap.getOrDefault("BOTH:" + cleanDagger + "_" + cleanAsterisk + "_" + cleanPdx + "_" + cleanSdx,
                    Collections.emptyList());
        }

        // Case 2: Search by DAGGER only
        if (hasDagger) {
            return indexMap.getOrDefault("DAGGER:" + cleanDagger, Collections.emptyList());
        }

        // Case 3: Search by ASTERISK only
        if (hasAsterisk) {
            return indexMap.getOrDefault("ASTERISK:" + cleanAsterisk, Collections.emptyList());
        }

        // Case 4: Search by PDX only
        if (hasPdx) {
            return indexMap.getOrDefault("PDX:" + cleanPdx, Collections.emptyList());
        }

        // Case 5: Search by SDX only
        if (hasSdx) {
            return indexMap.getOrDefault("SDX:" + cleanSdx, Collections.emptyList());
        }

        // Case 6: No params -> Return all records
        return indexMap.getOrDefault("ALL", Collections.emptyList());
    }

    public long getDaRecordCount(String csvDirectoryPath, String fileName) {
        return getIndexedDaData(csvDirectoryPath, fileName).getOrDefault("ALL", Collections.emptyList()).size();
    }
}
