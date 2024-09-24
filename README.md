# Shade Starter Security

**Shade Starter Security** is a Spring Boot starter that provides rate limiting with caching features, allowing you to control access to your application’s methods based on user roles and request limits.

## Dependency

To use Shade Starter Security in your Spring Boot project, add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.shade-sdev</groupId>
    <artifactId>shade-starter-security</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```

## Features

- **Rate Limiting**: Annotate methods to enforce request limits.

  You can use the `@RateLimit` annotation to specify the maximum number of requests allowed and the time frame for that limit. Here’s an example:

    ```java
    @RateLimit(maxRequests = 5, time = 1, timeUnit = TimeUnit.HOURS)
    public void someRateLimitedMethod() {
        // Method implementation
    }
    ```

- **Role-Based Access**: Define which user roles can access rate-limited methods.

  The `@RateLimit` annotation allows you to specify roles that are permitted to access the method. For example:

    ```java
    @RateLimit(maxRequests = 3, time = 10, timeUnit = TimeUnit.MINUTES, roles = {"ROLE_ADMIN", "ROLE_USER"})
    public String accessRestrictedResource() {
        return "Access granted to restricted resource.";
    }
    ```

- **Caching Support**: Utilizes caching to manage rate limit counters efficiently.

  The implementation uses a caching mechanism to store and retrieve rate limit counters. Here’s an example of how to implement a simple caching service:

    ```java
    @Component
    public class CacheService {

        private static final String RATE_CACHE = "rateLimit";

        private final CacheManager cacheManager;

        @Autowired
        public CacheService(CacheManager cacheManager) {
            this.cacheManager = cacheManager;
        }

        public void putCounter(String key, RateLimitCounter value) {
            Optional.ofNullable(cacheManager.getCache(RATE_CACHE)).ifPresent(c -> c.put(key, value));
        }

        public RateLimitCounter getCounter(String key) {
            return Optional.ofNullable(cacheManager.getCache(RATE_CACHE))
                           .map(cache -> cache.get(key))
                           .map(Cache.ValueWrapper::get)
                           .map(RateLimitCounter.class::cast)
                           .orElse(null);
        }
    }
    ```

## RateLimitException

The `RateLimitException` is thrown when a user exceeds the allowed number of requests within a specified time frame. This exception is used to manage rate limit violations and provide appropriate feedback to the user.

### Usage

When a method annotated with `@RateLimit` is accessed, and the rate limit has been exceeded, the `RateLimitException` will be triggered. You can handle this exception globally to return a user-friendly message or status code.

### Example

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public String handleRateLimitException(RateLimitException e) {
        return e.getMessage(); // Return the exception message to the user
    }
}
```
