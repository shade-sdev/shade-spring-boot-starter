package shade.dev.local.security.aspect;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import shade.dev.local.security.annotation.RateLimit;
import shade.dev.local.security.type.ratelimiting.RateLimitingCacheService;
import shade.dev.local.security.type.ratelimiting.exception.RateLimitException;
import shade.dev.local.security.type.ratelimiting.model.RateLimitCounter;

@Aspect
public class RateLimitingAspect {

    private final RateLimitingCacheService cacheService;

    @Autowired
    public RateLimitingAspect(RateLimitingCacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Around("@annotation(rateLimit)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());

        Set<String> authenticatedRoles = authentication.map(Authentication::getAuthorities)
                                                       .stream()
                                                       .flatMap(Collection::stream)
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

        String key = authentication.map(it -> it.getName() + ":" + joinPoint.getSignature().toShortString())
                                   .orElse(this.getIpAddress() + ":" + joinPoint.getSignature().toShortString());

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

    private String getIpAddress() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String ip = "unknown";
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            ip = request.getRemoteAddr();
        }

        return ip;
    }

}