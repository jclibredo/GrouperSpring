package phil.grouper.services;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class JobQueueService {
    // Tracks status of jobs: Key = JobID, Value = Status String
    private final Map<String, String> jobStatusMap = new ConcurrentHashMap<>();

    public String createJob() {
        String jobId = UUID.randomUUID().toString();
        jobStatusMap.put(jobId, "PROCESSING");
        return jobId;
    }

    public void updateStatus(String jobId, String status) {
        jobStatusMap.put(jobId, status);
    }

    public String getStatus(String jobId) {
        return jobStatusMap.getOrDefault(jobId, "NOT_FOUND");
    }
}