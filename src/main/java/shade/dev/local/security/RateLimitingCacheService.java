package shade.dev.local.security;

public interface RateLimitingCacheService {

    void putCounter(String key, RateLimitCounter counter);

    RateLimitCounter getCounter(String key);

}
