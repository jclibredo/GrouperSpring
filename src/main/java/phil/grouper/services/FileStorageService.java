package phil.grouper.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    // Your existing method
    public void printPath() {
        System.out.println("Files will be saved to: " + uploadDir);
    }

    // New method for the API to use
    public String getUploadPath() {
        return uploadDir;
    }
}
