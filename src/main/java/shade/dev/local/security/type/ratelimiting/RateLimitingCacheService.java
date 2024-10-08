package shade.dev.local.security.type.ratelimiting;

import shade.dev.local.security.type.ratelimiting.model.RateLimitCounter;

public interface RateLimitingCacheService {

    void putCounter(String key, RateLimitCounter counter);

    RateLimitCounter getCounter(String key);

}
