package phil.grouper.csvreader;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.opencsv.bean.CsvToBeanBuilder;
import phil.grouper.csvbinder.i10vx;

@Service
public class I10vxCsvReaderService {

    @Cacheable(value = "i10vxIndexedCache", key = "#fileName")
    public Map<String, List<i10vx>> getIndexedI10vxData(String csvDirectoryPath, String fileName) {
        System.out.println("--> Building Code Index for I10VX CSV from DISK...");

        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return Collections.emptyMap();
        }

        try (Reader reader = new FileReader(fullPath.toFile())) {
            List<i10vx> list = new CsvToBeanBuilder<i10vx>(reader)
                    .withType(i10vx.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            // Groups items by uppercase 'code'
            Map<String, List<i10vx>> indexMap = list.stream()
                    .filter(item -> item.getCode() != null)
                    .collect(Collectors.groupingBy(
                            item -> item.getCode().trim().toUpperCase(),
                            LinkedHashMap::new,
                            Collectors.toList()));

            // Store 'ALL' key for retrieving complete list
            indexMap.put("ALL", list);

            return indexMap;

        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV file at " + fullPath, e);
        }
    }

    /**
     * Fast index retrieval by 'code', with fallback stream filter for 'desc' or
     * 'validcode'
     */
    public List<i10vx> readAndFilter(String csvDirectoryPath, String fileName, String codeParam, String descParam,
            String validCodeParam) {
        Map<String, List<i10vx>> indexMap = getIndexedI10vxData(csvDirectoryPath, fileName);

        List<i10vx> candidates;

        // Step 1: Use O(1) Index lookup if 'code' param is present
        if (codeParam != null && !codeParam.trim().isEmpty()) {
            candidates = indexMap.getOrDefault(codeParam.trim().toUpperCase(), Collections.emptyList());
        } else {
            candidates = indexMap.getOrDefault("ALL", Collections.emptyList());
        }

        // Step 2: Apply secondary filter for desc and validcode if provided
        boolean hasDesc = descParam != null && !descParam.trim().isEmpty();
        boolean hasValidCode = validCodeParam != null && !validCodeParam.trim().isEmpty();

        if (!hasDesc && !hasValidCode) {
            return candidates; // Return index result directly
        }

        return candidates.stream()
                .filter(item -> {
                    boolean matchesDesc = !hasDesc || (item.getDesc() != null
                            && item.getDesc().toLowerCase().contains(descParam.trim().toLowerCase()));

                    boolean matchesValid = !hasValidCode || (item.getValidCode() != null
                            && item.getValidCode().trim().equalsIgnoreCase(validCodeParam.trim()));

                    return matchesDesc && matchesValid;
                })
                .toList();
    }

    public long getI10vxRecordCount(String csvDirectoryPath, String fileName) {
        return getIndexedI10vxData(csvDirectoryPath, fileName).getOrDefault("ALL", Collections.emptyList()).size();
    }

}