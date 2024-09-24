package shade.dev.local.security.type.ratelimiting;

public interface RateLimitingCacheService {

    void putCounter(String key, RateLimitCounter counter);

    RateLimitCounter getCounter(String key);

}
