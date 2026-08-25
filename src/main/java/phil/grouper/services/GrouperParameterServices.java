package phil.grouper.services;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import phil.grouper.entity.GrouperParameter;
import phil.grouper.utility.Utility;
import java.io.File;
import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

@Service
public class GrouperParameterServices {

    // private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.upload.dir}") // This pulls "D:/REPORTS" from application.properties
    private String reportPath;

    @Autowired
    private Utility utility;

    @Cacheable(value = "parameters")
    public List<GrouperParameter> readGrouperParameters() {
        System.out.println(">>>> FETCHING FROM D:/REPORTS DRIVE...");

        // 1. Construct the Path
        Path path = Paths.get(reportPath, "grouperparameter.json");
        File file = path.toFile();

        // 2. VALIDATION: Check if file exists
        if (!file.exists()) {
            System.err.println("CRITICAL: File missing at " + file.getAbsolutePath());
            // Return an empty list or throw a custom exception
            return Collections.emptyList();
        }

        // 3. VALIDATION: Check if it is a file and not a directory
        if (!file.isFile()) {
            throw new RuntimeException("Target path is not a valid file: " + path);
        }

        try {

            // Maps the JSON array to a List of your entities
            return utility.objectMapper().readValue(file, new TypeReference<List<GrouperParameter>>() {
            });

        } catch (IOException e) {
            // Log the error and handle it (e.g., return empty list or throw custom
            // exception)
            System.err.println("Error reading JSON from " + reportPath + ": " + e.getMessage());
            throw new RuntimeException("Data loading failed", e);
        }
    }

    @CacheEvict(value = "parameters", allEntries = true)
    public void refreshCache() {
        System.out.println("--- Cache Cleared ---");
    }
}
