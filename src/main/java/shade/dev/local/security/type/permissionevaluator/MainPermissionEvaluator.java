package shade.dev.local.security.type.permissionevaluator;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

public class MainPermissionEvaluator implements PermissionEvaluator {

    private static final PermissionEvaluator denyAll = new DenyAllPermissionEvaluator();

    private final PermissionEvaluatorManager permissionEvaluatorManager;

    public MainPermissionEvaluator(PermissionEvaluatorManager permissionEvaluatorManager) {
        this.permissionEvaluatorManager = permissionEvaluatorManager;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        PermissionEvaluator permissionEvaluator = permissionEvaluatorManager.targetedPermissionEvaluator(targetDomainObject.getClass().getName());
        if (permissionEvaluator == null) {
            permissionEvaluator = denyAll;
        }

        return permissionEvaluator.hasPermission(authentication, targetDomainObject, permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        PermissionEvaluator permissionEvaluator = permissionEvaluatorManager.targetedPermissionEvaluator(targetType);
        if (permissionEvaluator == null) {
            permissionEvaluator = denyAll;
        }

        return permissionEvaluator.hasPermission(authentication, targetId, targetType, permission);
    }

}
