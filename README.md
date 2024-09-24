# Shade Starter Security

**Shade Starter Security** is a Spring Boot starter that provides rate limiting with caching features, allowing you to control access to your application’s methods based on user roles and request
limits.

## Dependency

To use Shade Starter Security in your Spring Boot project, add the following dependency to your `pom.xml`:

```xml

<dependency>
    <groupId>com.github.shade-sdev</groupId>
    <artifactId>shade-spring-boot-starter</artifactId>
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
  Make sure to enable Caching with `@EnableCaching`
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

The `RateLimitException` is thrown when a user exceeds the allowed number of requests within a specified time frame. This exception is used to manage rate limit violations and provide appropriate
feedback to the user.

### Usage

When a method annotated with `@RateLimit` is accessed, and the rate limit has been exceeded, the `RateLimitException` will be triggered. You can handle this exception globally to return a
user-friendly message or status code.

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

- **Permission Evaluator Factory**: Create multiple Permission Evaluator for a specific class

This system allows you to manage permission evaluation in a flexible way based on the type of the target object. It leverages Spring Security's `PermissionEvaluator` interface and enhances it by
introducing a `TargetedPermissionEvaluator` interface for specific object types.

### Components

TargetedPermissionEvaluator
An interface that extends Spring Security's PermissionEvaluator:

```java
public interface TargetedPermissionEvaluator extends PermissionEvaluator {
    String getTargetType();
}
```

getTargetType(): Returns the fully qualified class name of the target type this evaluator handles.

PermissionEvaluatorManager
Manages and retrieves the appropriate TargetedPermissionEvaluator for a given class name:

```java

@Component
public class PermissionEvaluatorManager {
    // ... (constructor and fields omitted for brevity)

    public PermissionEvaluator targetedPermissionEvaluator(String className) {
        // ... (implementation details)
    }
}
```

Uses Spring's ApplicationContext to fetch all beans of type TargetedPermissionEvaluator.
Returns the appropriate evaluator based on the provided class name, or a DenyAllPermissionEvaluator if none is found.

MainPermissionEvaluator
The main entry point for permission evaluation:

```java
public class MainPermissionEvaluator implements PermissionEvaluator {
    // ... (constructor and fields omitted for brevity)

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        // ... (implementation details)
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        // ... (implementation details)
    }
}
```

Delegates to the appropriate TargetedPermissionEvaluator based on the target object's class or provided target type.
Falls back to DenyAllPermissionEvaluator if no specific evaluator is found.

DenyAllPermissionEvaluator
A fallback permission evaluator that denies all permissions:

```java
public class DenyAllPermissionEvaluator implements TargetedPermissionEvaluator {
    // ... (implementation details)
}
```

Always returns false for any permission check.
Used as a default when no specific evaluator is found.

### Usage

Create custom permission evaluators by implementing TargetedPermissionEvaluator:

```java

@Component
public class TestPermissionEvaluator implements TargetedPermissionEvaluator {
    @Override
    public String getTargetType() {
        return Person.class.getName();
    }

    // ... (implement hasPermission methods)
}
```

Use the @PreAuthorize annotation with the hasPermission expression:

```java
import org.springframework.http.ResponseEntity;

@PreAuthorize("hasPermission(null, 'com.example.person', null)")
public ResponseEntity<Void> deletePerson() {
    // ...
}
```

Applying it to your own MethodSecurityExpressionHandler

```java

@Bean
public PermissionEvaluatorManager permissionEvaluatorManager(ApplicationContext applicationContext) {
    return new PermissionEvaluatorManager(applicationContext);
}

@Bean
public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
        RoleHierarchy roleHierarchy,
        PermissionEvaluatorManager permissionEvaluatorManager
) {
    DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setRoleHierarchy(roleHierarchy);
    expressionHandler.setPermissionEvaluator(new MainPermissionEvaluator(permissionEvaluatorManager));
    return expressionHandler;
}
```

How It Works

When a method annotated with @PreAuthorize is called, Spring Security invokes the MainPermissionEvaluator.
MainPermissionEvaluator uses PermissionEvaluatorManager to find the appropriate TargetedPermissionEvaluator.
If a matching evaluator is found, it's used to check the permission.
If no matching evaluator is found, the DenyAllPermissionEvaluator is used, denying the permission.
