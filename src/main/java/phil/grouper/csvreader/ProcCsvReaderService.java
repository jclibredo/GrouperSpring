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

import com.opencsv.bean.CsvToBeanBuilder;

import phil.grouper.csvbinder.i10;
import phil.grouper.csvbinder.proc;

public class ProcCsvReaderService {

    @Cacheable(value = "procIndexedCache", key = "#fileName")
    public Map<String, List<proc>> getIndexedProcData(String csvDirectoryPath, String fileName) {
        System.out.println("--> Building Code Index for PROC CSV from DISK...");

        Path fullPath = Paths.get(csvDirectoryPath, fileName);
        if (!Files.exists(fullPath)) {
            return Collections.emptyMap();
        }

        try (Reader reader = new FileReader(fullPath.toFile())) {
            List<proc> list = new CsvToBeanBuilder<proc>(reader)
                    .withType(proc.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            Map<String, List<proc>> indexMap = new HashMap<>();
            for (proc item : list) {
                String codeVal = (item.getCode() != null) ? item.getCode().trim().toUpperCase() : null;
                String orpVal = (item.getOrp() != null) ? item.getOrp().trim().toUpperCase() : null;
                String orptypeVal = (item.getOrptype() != null) ? item.getOrptype().trim().toUpperCase() : null;
                String sexVal = (item.getSex() != null) ? item.getSex().trim().toUpperCase() : null;
                String siteVal = (item.getSite() != null) ? item.getSite().trim().toUpperCase() : null;
                String procgrVal = (item.getProcgr() != null) ? item.getProcgr().trim().toUpperCase() : null;
                String pcpartVal = (item.getPcpart() != null) ? item.getPcpart().trim().toUpperCase() : null;
                String extlevVal = (item.getExtlev() != null) ? item.getExtlev().trim().toUpperCase() : null;
                String descVal = (item.getDesc() != null) ? item.getDesc().trim().toUpperCase() : null;
                String drguseVal = (item.getDrguse() != null) ? item.getDrguse().trim().toUpperCase() : null;
                String mayunVal = (item.getMayun() != null) ? item.getMayun().trim().toUpperCase() : null;

                // Index by 'code'
                if (codeVal != null && !codeVal.isEmpty()) {
                    indexMap.computeIfAbsent("CODE:" + codeVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'orp'
                if (orpVal != null && !orpVal.isEmpty()) {
                    indexMap.computeIfAbsent("ORP:" + orpVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'orptype'
                if (orptypeVal != null && !orptypeVal.isEmpty()) {
                    indexMap.computeIfAbsent("ORPTYPE:" + orptypeVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'cc'
                if (sexVal != null && !sexVal.isEmpty()) {
                    indexMap.computeIfAbsent("SEX:" + sexVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'maincc'
                if (siteVal != null && !siteVal.isEmpty()) {
                    indexMap.computeIfAbsent("SITE:" + siteVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'ccrow'
                if (procgrVal != null && !procgrVal.isEmpty()) {
                    indexMap.computeIfAbsent("PROCGR:" + procgrVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'hiv_ax'
                if (pcpartVal != null && !pcpartVal.isEmpty()) {
                    indexMap.computeIfAbsent("PCPART:" + pcpartVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'trauma'
                if (extlevVal != null && !extlevVal.isEmpty()) {
                    indexMap.computeIfAbsent("EXTLEV:" + extlevVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'sex'
                if (sexVal != null && !sexVal.isEmpty()) {
                    indexMap.computeIfAbsent("SEX:" + sexVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'accpdx'
                if (descVal != null && !descVal.isEmpty()) {
                    indexMap.computeIfAbsent("DESC:" + descVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'ageduse'
                if (drguseVal != null && !drguseVal.isEmpty()) {
                    indexMap.computeIfAbsent("DRGUSE:" + drguseVal, k -> new ArrayList<>()).add(item);
                }
                // Index by 'mayun'
                if (mayunVal != null && !mayunVal.isEmpty()) {
                    indexMap.computeIfAbsent("MAYUN:" + mayunVal, k -> new ArrayList<>()).add(item);
                }
                // Index by both
                // 'code','orp','orptype','sex','site','procgr','pcpart','extlev','desc','drguse','mayun'
                // if applicable
                if (codeVal != null && !codeVal.isEmpty() &&
                        orpVal != null && !orpVal.isEmpty() &&
                        orptypeVal != null && !orptypeVal.isEmpty() &&
                        sexVal != null && !sexVal.isEmpty() &&
                        siteVal != null && !siteVal.isEmpty() &&
                        procgrVal != null && !procgrVal.isEmpty() &&
                        pcpartVal != null && !pcpartVal.isEmpty() &&
                        extlevVal != null && !extlevVal.isEmpty() &&
                        descVal != null && !descVal.isEmpty() &&
                        drguseVal != null && !drguseVal.isEmpty() &&
                        mayunVal != null && !mayunVal.isEmpty()) {
                    indexMap.computeIfAbsent("BOTH:"
                            + codeVal + "_"
                            + orpVal + "_"
                            + orptypeVal + "_"
                            + sexVal + "_"
                            + siteVal + "_"
                            + procgrVal + "_"
                            + pcpartVal + "_"
                            + extlevVal + "_"
                            + descVal + "_"
                            + drguseVal + "_"
                            + mayunVal + "_"
                            + sexVal, k -> new ArrayList<>()).add(item);
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
    public List<proc> readAndFilter(
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
        Map<String, List<proc>> indexMap = getIndexedProcData(csvDirectoryPath, fileName);
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
