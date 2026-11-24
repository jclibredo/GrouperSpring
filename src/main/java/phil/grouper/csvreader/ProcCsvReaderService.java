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
import phil.grouper.csvbinder.proc;

@Service
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
            String orptypeParam,
            String sexParam,
            String siteParam,
            String procgrParam,
            String pcpartParam,
            String extlevParam,
            String descParam,
            String drguseParam,
            String mayunParam) {
        Map<String, List<proc>> indexMap = getIndexedProcData(csvDirectoryPath, fileName);
        // Step 2: Apply secondary filter for desc and validcode if provided
        boolean hasCode = codeParam != null && !codeParam.trim().isEmpty();
        boolean hasOrptype = orptypeParam != null && !orptypeParam.trim().isEmpty();
        boolean hasSex = sexParam != null && !sexParam.trim().isEmpty();
        boolean hasSite = siteParam != null && !siteParam.trim().isEmpty();
        boolean hasProcgr = procgrParam != null && !procgrParam.trim().isEmpty();
        boolean hasPcpart = pcpartParam != null && !pcpartParam.trim().isEmpty();
        boolean hasExtlev = extlevParam != null && !extlevParam.trim().isEmpty();
        boolean hasDesc = descParam != null && !descParam.trim().isEmpty();
        boolean hasDrguse = drguseParam != null && !drguseParam.trim().isEmpty();
        boolean hasMayun = mayunParam != null && !mayunParam.trim().isEmpty();

        @SuppressWarnings("null")
        String cleanCode = hasCode ? codeParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanOrptype = hasOrptype ? orptypeParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanSex = hasSex ? sexParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanSite = hasSite ? siteParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanProcgr = hasProcgr ? procgrParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanPcpart = hasPcpart ? pcpartParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanExtlev = hasExtlev ? extlevParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanDesc = hasDesc ? descParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanDrguse = hasDrguse ? drguseParam.trim().toUpperCase() : "";
        @SuppressWarnings("null")
        String cleanMayun = hasMayun ? mayunParam.trim().toUpperCase() : "";

        // Case 1: Search by all parameters
        if (hasCode && hasOrptype && hasSex && hasSite && hasProcgr && hasPcpart && hasExtlev && hasDesc && hasDrguse
                && hasMayun) {
            return indexMap.getOrDefault("BOTH:"
                    + cleanCode + "_"
                    + cleanOrptype + "_"
                    + cleanSex + "_"
                    + cleanSite + "_"
                    + cleanProcgr + "_"
                    + cleanPcpart + "_"
                    + cleanExtlev + "_"
                    + cleanDesc + "_"
                    + cleanDrguse + "_"
                    + cleanMayun,
                    Collections.emptyList());
        }
        // Case 2: Search by Code only
        if (hasCode) {
            return indexMap.getOrDefault("CODE:" + cleanCode, Collections.emptyList());
        }
        // Case 3: Search by Orptype only
        if (hasOrptype) {
            return indexMap.getOrDefault("ORPTYPE:" + cleanOrptype, Collections.emptyList());
        }
        // Case 4: Search by Sex only
        if (hasSex) {
            return indexMap.getOrDefault("SEX:" + cleanSex, Collections.emptyList());
        }
        // Case 5: Search bySite only
        if (hasSite) {
            return indexMap.getOrDefault("SITE:" + cleanSite, Collections.emptyList());
        }
        // Case 6: Search by Procgr only
        if (hasProcgr) {
            return indexMap.getOrDefault("PROCGR:" + cleanProcgr, Collections.emptyList());
        }
        // Case 7: Search by Pcpart only
        if (hasPcpart) {
            return indexMap.getOrDefault("PCPART:" + cleanPcpart, Collections.emptyList());
        }
        // Case 8: Search by Extlev only
        if (hasExtlev) {
            return indexMap.getOrDefault("EXTLEV:" + cleanExtlev, Collections.emptyList());
        }
        // Case 9: Search by Desc only
        if (hasDesc) {
            return indexMap.getOrDefault("DESC:" + cleanDesc, Collections.emptyList());
        }
        // Case 10: Search by Drguse only
        if (hasDrguse) {
            return indexMap.getOrDefault("DRGUSE:" + cleanDrguse, Collections.emptyList());
        }
        // Case 11: Search by Mayun only
        if (hasMayun) {
            return indexMap.getOrDefault("MAYUN:" + cleanMayun, Collections.emptyList());
        }
        // Case 15: No params -> Return all records
        return indexMap.getOrDefault("ALL", Collections.emptyList());
    }

    public long getPROCRecordCount(String csvDirectoryPath, String fileName) {
        return getIndexedProcData(csvDirectoryPath, fileName).getOrDefault("ALL", Collections.emptyList()).size();
    }

}
