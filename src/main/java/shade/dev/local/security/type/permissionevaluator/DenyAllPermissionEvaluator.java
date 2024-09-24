package shade.dev.local.security.type.permissionevaluator;

import java.io.Serializable;

import org.springframework.security.core.Authentication;

public class DenyAllPermissionEvaluator implements TargetedPermissionEvaluator {

    @Override
    public String getTargetType() {
        return Object.class.getSimpleName();
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

}
