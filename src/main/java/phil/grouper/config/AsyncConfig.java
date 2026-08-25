package phil.grouper.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // By default, Spring will use a SimpleAsyncTaskExecutor.
    // This is enough for basic bulk uploads.
}