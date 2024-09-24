package shade.dev.local.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import shade.dev.local.security.aspect.RateLimitingAspect;
import shade.dev.local.security.type.ratelimiting.service.RateLimitingCacheService;
import shade.dev.local.security.type.ratelimiting.service.impl.DefaultRateLimitingCache;

@Configuration
@ConditionalOnMissingBean(RateLimitingCacheService.class)
public class RateLimitAutoConfiguration {

    @Bean
    public RateLimitingCacheService rateLimitingCache(CacheManager cacheManager) {
        return new DefaultRateLimitingCache(cacheManager);
    }

    @Bean
    public RateLimitingAspect rateLimitingAspect(RateLimitingCacheService cache) {
        return new RateLimitingAspect(cache);
    }

}
