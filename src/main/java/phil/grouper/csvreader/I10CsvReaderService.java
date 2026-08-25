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

import phil.grouper.csvbinder.i10;

@Service
public class I10CsvReaderService {

    @Cacheable(value = "i10IndexedCache", key = "#fileName")
    public Map<String, List<i10>> getIndexedI10Data(String csvDirectoryPath, String fileName) {
        System.out.println("--> Building Code Index for I10 CSV from DISK...");

        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return Collections.emptyMap();
        }

        try (Reader reader = new FileReader(fullPath.toFile())) {
            List<i10> list = new CsvToBeanBuilder<i10>(reader)
                    .withType(i10.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            Map<String, List<i10>> indexMap = new HashMap<>();
            for (i10 item : list) {
                String codeVal = (item.getCode() != null) ? item.getCode().trim().toUpperCase() : null;
                String mdcVal = (item.getMdc() != null) ? item.getMdc().trim().toUpperCase() : null;
                String pdcVal = (item.getPdc() != null) ? item.getPdc().trim().toUpperCase() : null;
                String ccVal = (item.getCc() != null) ? item.getCc().trim().toUpperCase() : null;
                String mainccVal = (item.getMaincc() != null) ? item.getMaincc().trim().toUpperCase() : null;
                String ccrowVal = (item.getCcrow() != null) ? item.getCcrow().trim().toUpperCase() : null;
                String hivaxVal = (item.getHiv_ax() != null) ? item.getHiv_ax().trim().toUpperCase() : null;
                String traumaVal = (item.getTrauma() != null) ? item.getTrauma().trim().toUpperCase() : null;
                String sexVal = (item.getSex() != null) ? item.getSex().trim().toUpperCase() : null;
                String accpdxVal = (item.getAccpdx() != null) ? item.getAccpdx().trim().toUpperCase() : null;
                String ageduseVal = (item.getAgeduse() != null) ? item.getAgeduse().trim().toUpperCase() : null;
                String ageminVal = (item.getAgemin() != null) ? item.getAgemin().trim().toUpperCase() : null;
                String agemaxVal = (item.getAgemax() != null) ? item.getAgemax().trim().toUpperCase() : null;
                String agedminVal = (item.getAgedmin() != null) ? item.getAgedmin().trim().toUpperCase() : null;

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
                // Index by 'cc'
                if (ccVal != null && !ccVal.isEmpty()) {
                    indexMap.computeIfAbsent("CC:" + ccVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'maincc'
                if (mainccVal != null && !mainccVal.isEmpty()) {
                    indexMap.computeIfAbsent("MAINCC:" + mainccVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'ccrow'
                if (ccrowVal != null && !ccrowVal.isEmpty()) {
                    indexMap.computeIfAbsent("CCROW:" + ccrowVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'hiv_ax'
                if (hivaxVal != null && !hivaxVal.isEmpty()) {
                    indexMap.computeIfAbsent("HIV_AX:" + hivaxVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'trauma'
                if (traumaVal != null && !traumaVal.isEmpty()) {
                    indexMap.computeIfAbsent("TRAUMA:" + traumaVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'sex'
                if (sexVal != null && !sexVal.isEmpty()) {
                    indexMap.computeIfAbsent("SEX:" + sexVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'accpdx'
                if (accpdxVal != null && !accpdxVal.isEmpty()) {
                    indexMap.computeIfAbsent("ACCPDX:" + accpdxVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'ageduse'
                if (ageduseVal != null && !ageduseVal.isEmpty()) {
                    indexMap.computeIfAbsent("AGEDUSE:" + ageduseVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'agemin'
                if (ageminVal != null && !ageminVal.isEmpty()) {
                    indexMap.computeIfAbsent("AGEMIN:" + ageminVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'agemax'
                if (agemaxVal != null && !agemaxVal.isEmpty()) {
                    indexMap.computeIfAbsent("AGEMAX:" + agemaxVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'agedmin'
                if (agedminVal != null && !agedminVal.isEmpty()) {
                    indexMap.computeIfAbsent("AGEDMIN:" + agedminVal, k -> new ArrayList<>()).add(item);
                }
                // Index by both
                // 'code','mdc','pdc','cc','maincc','ccrow','hiv_ax','trauma','sex','accpdx','ageduse','agemin','agemax','agedmin'
                // if
                // applicable
                if (codeVal != null && !codeVal.isEmpty() &&
                        mdcVal != null && !mdcVal.isEmpty() &&
                        pdcVal != null && !pdcVal.isEmpty() &&
                        ccVal != null && !ccVal.isEmpty() &&
                        mainccVal != null && !mainccVal.isEmpty() &&
                        ccrowVal != null && !ccrowVal.isEmpty() &&
                        hivaxVal != null && !hivaxVal.isEmpty() &&
                        traumaVal != null && !traumaVal.isEmpty() &&
                        sexVal != null && !sexVal.isEmpty() &&
                        accpdxVal != null && !accpdxVal.isEmpty() &&
                        ageduseVal != null && !ageduseVal.isEmpty() &&
                        ageminVal != null && !ageminVal.isEmpty() &&
                        agemaxVal != null && !agemaxVal.isEmpty() &&
                        agedminVal != null && !agedminVal.isEmpty()) {
                    indexMap.computeIfAbsent("BOTH:"
                            + codeVal + "_"
                            + mdcVal + "_"
                            + pdcVal + "_"
                            + ccVal + "_"
                            + mainccVal + "_"
                            + ccrowVal + "_"
                            + hivaxVal + "_"
                            + traumaVal + "_"
                            + sexVal + "_"
                            + accpdxVal + "_"
                            + ageduseVal + "_"
                            + ageminVal + "_"
                            + agemaxVal + "_"
                            + agedminVal, k -> new ArrayList<>()).add(item);
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
     * Fast index retrieval by 'code', with fallback stream filter for 'desc' or
     * 'validcode'
     */
    public List<i10> readAndFilter(
            String csvDirectoryPath,
            String fileName,
            String codeParam,
            String mdcParam,
            String pdcParam,
            String ccParam,
            String mainccParam,
            String ccrowParam,
            String hivaxParam,
            String sexParam,
            String accpdxParam,
            String ageduseParam,
            String ageminParam,
            String agemaxParam,
            String agedminParam) {
        Map<String, List<i10>> indexMap = getIndexedI10Data(csvDirectoryPath, fileName);
        // Step 2: Apply secondary filter for desc and validcode if provided
        boolean hasCode = codeParam != null && !codeParam.trim().isEmpty();
        boolean hasMdc = mdcParam != null && !mdcParam.trim().isEmpty();
        boolean hasPdc = pdcParam != null && !pdcParam.trim().isEmpty();
        boolean hasCc = ccParam != null && !ccParam.trim().isEmpty();
        boolean hasMaincc = mainccParam != null && !mainccParam.trim().isEmpty();
        boolean hasCcrow = ccrowParam != null && !ccrowParam.trim().isEmpty();
        boolean hasHivax = hivaxParam != null && !hivaxParam.trim().isEmpty();
        boolean hasSex = sexParam != null && !sexParam.trim().isEmpty();
        boolean hasAccpdx = accpdxParam != null && !accpdxParam.trim().isEmpty();
        boolean hasAgeduse = ageduseParam != null && !ageduseParam.trim().isEmpty();
        boolean hasAgemin = ageminParam != null && !ageminParam.trim().isEmpty();
        boolean hasAgemax = agemaxParam != null && !agemaxParam.trim().isEmpty();
        boolean hasAgedmin = agedminParam != null && !agedminParam.trim().isEmpty();

        @SuppressWarnings("null")
        String cleanCode = hasCode ? codeParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanMdc = hasMdc ? mdcParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanPdc = hasPdc ? pdcParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanCc = hasCc ? ccParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanMaincc = hasMaincc ? mainccParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanCcrow = hasCcrow ? ccrowParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanHivax = hasHivax ? hivaxParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanSex = hasSex ? sexParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanAccpdx = hasAccpdx ? accpdxParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanAgeduse = hasAgeduse ? ageduseParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanAgemin = hasAgemin ? ageminParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanAgemax = hasAgemax ? agemaxParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanAgedmin = hasAgedmin ? agedminParam.trim().toUpperCase() : "";

        // Case 1: Search by all parameters
        if (hasCode && hasMdc && hasPdc && hasCc && hasMaincc && hasCcrow && hasHivax && hasSex && hasAccpdx
                && hasAgeduse && hasAgemin && hasAgemax && hasAgedmin) {
            return indexMap.getOrDefault("BOTH:"
                    + cleanCode + "_"
                    + cleanMdc + "_"
                    + cleanPdc + "_"
                    + cleanCc + "_"
                    + cleanMaincc + "_"
                    + cleanCcrow + "_"
                    + cleanHivax + "_"
                    + cleanSex + "_"
                    + cleanAccpdx + "_"
                    + cleanAgeduse + "_"
                    + cleanAgemin + "_"
                    + cleanAgemax + "_"
                    + cleanAgedmin,
                    Collections.emptyList());
        }
        // Case 2: Search by Code only
        if (hasCode) {
            return indexMap.getOrDefault("CODE:" + cleanCode, Collections.emptyList());
        }
        // Case 3: Search by Mdc only
        if (hasMdc) {
            return indexMap.getOrDefault("MDC:" + cleanMdc, Collections.emptyList());
        }
        // Case 4: Search by Pdc only
        if (hasPdc) {
            return indexMap.getOrDefault("PDC:" + cleanPdc, Collections.emptyList());
        }
        // Case 5: Search byCc only
        if (hasCc) {
            return indexMap.getOrDefault("CC:" + cleanCc, Collections.emptyList());
        }
        // Case 6: Search by Maincc only
        if (hasMaincc) {
            return indexMap.getOrDefault("MAINCC:" + cleanMaincc, Collections.emptyList());
        }
        // Case 7: Search by Ccrow only
        if (hasCcrow) {
            return indexMap.getOrDefault("CCROW:" + cleanCcrow, Collections.emptyList());
        }
        // Case 8: Search by Hiv_ax only
        if (hasHivax) {
            return indexMap.getOrDefault("HIV_AX:" + cleanHivax, Collections.emptyList());
        }
        // Case 9: Search by Sex only
        if (hasSex) {
            return indexMap.getOrDefault("SEX:" + cleanSex, Collections.emptyList());
        }
        // Case 10: Search by Accpdx only
        if (hasAccpdx) {
            return indexMap.getOrDefault("ACCPDX:" + cleanAccpdx, Collections.emptyList());
        }
        // Case 11: Search by Ageduse only
        if (hasAgeduse) {
            return indexMap.getOrDefault("AGEDUSE:" + cleanAgeduse, Collections.emptyList());
        }
        // Case 12: Search by Agemin only
        if (hasAgemin) {
            return indexMap.getOrDefault("AGEMIN:" + cleanAgemin, Collections.emptyList());
        }
        // Case 13: Search by Agemax only
        if (hasAgemax) {
            return indexMap.getOrDefault("AGEMAX:" + cleanAgemax, Collections.emptyList());
        }
        // Case 14: Search by Agedmin only
        if (hasAgedmin) {
            return indexMap.getOrDefault("AGEDMIN:" + cleanAgedmin, Collections.emptyList());
        }
        // Case 15: No params -> Return all records
        return indexMap.getOrDefault("ALL", Collections.emptyList());
    }

    public long getI10RecordCount(String csvDirectoryPath, String fileName) {
        return getIndexedI10Data(csvDirectoryPath, fileName).getOrDefault("ALL", Collections.emptyList()).size();
    }
}
