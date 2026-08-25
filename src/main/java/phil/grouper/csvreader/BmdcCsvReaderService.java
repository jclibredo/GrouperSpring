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

import phil.grouper.csvbinder.bmdc;

@Service
public class BmdcCsvReaderService {

    /**
     * Builds and caches multi-field indexes for Bmdc data:
     * - Key "CODE:{codeVal}" -> List of items with that 'code'
     * - Key "DESC:{descVal}" -> List of items with that 'desc'
     * - Key "BOTH:{code}_{desc}" -> List of items matching both
     */
    @Cacheable(value = "bmdcIndexedCache", key = "#fileName")
    public Map<String, List<bmdc>> getIndexedBmdcData(String csvDirectoryPath, String fileName) {
        System.out.println("--> Building Multi-Index for Bmdc CSV from DISK...");

        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return Collections.emptyMap();
        }

        try (Reader reader = new FileReader(fullPath.toFile())) {
            List<bmdc> list = new CsvToBeanBuilder<bmdc>(reader)
                    .withType(bmdc.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            Map<String, List<bmdc>> indexMap = new HashMap<>();
            for (bmdc item : list) {
                String codeVal = (item.getCode() != null) ? item.getCode().trim().toUpperCase() : null;
                String mdcmaleVal = (item.getMdcmale() != null) ? item.getMdcmale().trim().toUpperCase() : null;
                String pdcmaleVal = (item.getPdcmale() != null) ? item.getPdcmale().trim().toUpperCase() : null;
                String mdcfemVal = (item.getMdcfem() != null) ? item.getMdcfem().trim().toUpperCase() : null;
                String pdcfemVal = (item.getPdcfem() != null) ? item.getPdcfem().trim().toUpperCase() : null;
                String descVal = (item.getDesc() != null) ? item.getDesc().trim().toUpperCase() : null;

                // Index by 'code'
                if (codeVal != null && !codeVal.isEmpty()) {
                    indexMap.computeIfAbsent("CODE:" + codeVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'mdcmale'
                if (mdcmaleVal != null && !mdcmaleVal.isEmpty()) {
                    indexMap.computeIfAbsent("MDCMALE:" + mdcmaleVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'pdcmale'
                if (pdcmaleVal != null && !pdcmaleVal.isEmpty()) {
                    indexMap.computeIfAbsent("PDCMALE:" + pdcmaleVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'mdcfem'
                if (mdcfemVal != null && !mdcfemVal.isEmpty()) {
                    indexMap.computeIfAbsent("MDCFEM:" + mdcfemVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'pdcfem'
                if (pdcfemVal != null && !pdcfemVal.isEmpty()) {
                    indexMap.computeIfAbsent("PDCFEM:" + pdcfemVal, k -> new ArrayList<>()).add(item);
                }

                // Index by 'desc'
                if (descVal != null && !descVal.isEmpty()) {
                    indexMap.computeIfAbsent("DESC:" + descVal, k -> new ArrayList<>()).add(item);
                }

                // Index by both 'code','mdcmale','pdcmale', 'mdcfem','pdcfem','desc' if
                // applicable
                if (codeVal != null && !codeVal.isEmpty() &&
                        mdcmaleVal != null && !mdcmaleVal.isEmpty() &&
                        pdcmaleVal != null && !pdcmaleVal.isEmpty() &&
                        mdcfemVal != null && !mdcfemVal.isEmpty() &&
                        pdcfemVal != null && !pdcfemVal.isEmpty() &&
                        descVal != null && !descVal.isEmpty()) {
                    indexMap.computeIfAbsent("BOTH:" + codeVal + "_" + mdcmaleVal + "_" + pdcmaleVal + "_" + mdcfemVal
                            + "_" + pdcfemVal + "_" + descVal, k -> new ArrayList<>()).add(item);
                }

                // Keep a 'ALL' key for unfiltered queries
                indexMap.computeIfAbsent("ALL", k -> new ArrayList<>()).add(item);
            }

            return indexMap;

        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV file at " + fullPath, e);
        }
    }

    public List<bmdc> readAndFilter(
            String csvDirectoryPath,
            String fileName,
            String codeParam,
            String mdcmaleParam,
            String pdcmaleParam,
            String mdcfemParam,
            String pdcfemParam,
            String descParam) {
        Map<String, List<bmdc>> indexMap = getIndexedBmdcData(csvDirectoryPath, fileName);
        boolean hasCode = codeParam != null && !codeParam.trim().isEmpty();
        boolean hasMdcmale = mdcmaleParam != null && !mdcmaleParam.trim().isEmpty();
        boolean hasPdcmale = pdcmaleParam != null && !pdcmaleParam.trim().isEmpty();
        boolean hasMdcfem = mdcfemParam != null && !mdcfemParam.trim().isEmpty();
        boolean hasPdcfem = pdcfemParam != null && !pdcfemParam.trim().isEmpty();
        boolean hasDesc = descParam != null && !descParam.trim().isEmpty();

        @SuppressWarnings("null")
        String cleanCode = hasCode ? codeParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanMdcmale = hasMdcmale ? mdcmaleParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanPdcmale = hasPdcmale ? pdcmaleParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanMdcfem = hasMdcfem ? mdcfemParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanPdcfem = hasPdcfem ? pdcfemParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanDesc = hasDesc ? descParam.trim().toUpperCase() : "";

        // Case 1: Search by all parameters
        if (hasCode && hasMdcmale && hasPdcmale && hasMdcfem && hasPdcfem && hasDesc) {
            return indexMap.getOrDefault("BOTH:" + cleanCode + "_" + cleanMdcmale + "_" + cleanPdcmale + "_"
                    + cleanMdcfem + "_" + cleanPdcfem + "_" + cleanDesc, Collections.emptyList());
        }
        // Case 2: Search by Code only
        if (hasCode) {
            return indexMap.getOrDefault("CODE:" + cleanCode, Collections.emptyList());
        }
        // Case 3: Search by Mdcmale only
        if (hasMdcmale) {
            return indexMap.getOrDefault("MDCMALE:" + cleanMdcmale, Collections.emptyList());
        }
        // Case 4: Search by Pdcmale only
        if (hasPdcmale) {
            return indexMap.getOrDefault("PDCMALE:" + cleanPdcmale, Collections.emptyList());
        }
        // Case 5: Search by Mdcfem only
        if (hasMdcfem) {
            return indexMap.getOrDefault("MDCFEM:" + cleanMdcfem, Collections.emptyList());
        }
        // Case 6: Search by Pdcfem only
        if (hasPdcfem) {
            return indexMap.getOrDefault("PDCFEM:" + cleanPdcfem, Collections.emptyList());
        }
        // Case 7: Search by Desc only
        if (hasDesc) {
            return indexMap.getOrDefault("DESC:" + cleanDesc, Collections.emptyList());
        }
        // Case 8: No params -> Return all records
        return indexMap.getOrDefault("ALL", Collections.emptyList());
    }

    public long getBmdcRecordCount(String csvDirectoryPath, String fileName) {
        return getIndexedBmdcData(csvDirectoryPath, fileName).getOrDefault("ALL", Collections.emptyList()).size();
    }

}
