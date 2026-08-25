package phil.grouper.csvreader;

import java.util.Objects;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final CacheManager cacheManager;

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Clear a specific cache by name (e.g., "axCsvCache", "i10vxCsvCache")
     */
    public void clearCacheByName(String cacheName) {
        if (cacheManager.getCache(cacheName) != null) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
            System.out.println("--> Cache cleared: " + cacheName);
        }
    }

    /**
     * Clear ALL caches in the application at once
     */
    public void clearAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        System.out.println("--> All caches cleared successfully!");
    }
}
