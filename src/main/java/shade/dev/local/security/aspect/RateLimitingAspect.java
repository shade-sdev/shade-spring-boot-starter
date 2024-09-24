package shade.dev.local.security.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import shade.dev.local.security.type.ratelimiting.model.RateLimitCounter;
import shade.dev.local.security.type.ratelimiting.exception.RateLimitException;
import shade.dev.local.security.type.ratelimiting.RateLimitingCacheService;
import shade.dev.local.security.annotation.RateLimit;

@Aspect
@Component
public class RateLimitingAspect {

    private final RateLimitingCacheService cacheService;

    @Autowired
    public RateLimitingAspect(RateLimitingCacheService cacheService)
    {
        this.cacheService = cacheService;
    }

    @Around("@annotation(rateLimit)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> authenticatedRoles = authentication.getAuthorities()
                                                       .stream()
                                                       .map(GrantedAuthority::getAuthority)
                                                       .collect(Collectors.toSet());

        String roleName = Arrays.stream(rateLimit.roles())
                                .filter(authenticatedRoles::contains)
                                .findFirst()
                                .orElse(null);

        boolean isAuthorized = rateLimit.roles().length == 0 || Objects.nonNull(roleName);

        if (!isAuthorized) {
            return joinPoint.proceed();
        }

        String key = authentication.getName() + ":" + joinPoint.getSignature().toShortString();
        RateLimitCounter counter = cacheService.getCounter(key);

        if (counter == null) {
            counter = new RateLimitCounter(rateLimit.maxRequests(), rateLimit.time(), rateLimit.timeUnit());
            cacheService.putCounter(key, counter);
        }

        if (counter.isOverLimit()) {
            throw new RateLimitException();
        }

        counter.incrementCount();
        return joinPoint.proceed();
    }

}