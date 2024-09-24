package shade.dev.local.security.type.ratelimiting.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import shade.dev.local.security.type.ratelimiting.model.RateLimitCounter;
import shade.dev.local.security.type.ratelimiting.service.RateLimitingCacheService;

import java.util.Optional;

@Component
public class DefaultRateLimitingCache implements RateLimitingCacheService {

    private static final String RATE_CACHE = "rateLimit";

    private final CacheManager cacheManager;

    @Autowired
    public DefaultRateLimitingCache(CacheManager cacheManager)
    {
        this.cacheManager = cacheManager;
    }

    @Override
    public void putCounter(String key, RateLimitCounter counter)
    {
        Optional.ofNullable(cacheManager.getCache(RATE_CACHE)).ifPresent(c -> c.put(key, counter));
    }

    @Override
    public RateLimitCounter getCounter(String key)
    {
        return Optional.ofNullable(cacheManager.getCache(RATE_CACHE))
                       .map(cache -> cache.get(key))
                       .map(Cache.ValueWrapper::get)
                       .map(RateLimitCounter.class::cast)
                       .orElse(null);
    }
}
