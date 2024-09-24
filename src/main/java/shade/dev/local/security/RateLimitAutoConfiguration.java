package shade.dev.local.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
