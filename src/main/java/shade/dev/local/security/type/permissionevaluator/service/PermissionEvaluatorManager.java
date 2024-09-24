package shade.dev.local.security.type.permissionevaluator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.stereotype.Component;

import shade.dev.local.security.type.permissionevaluator.DenyAllPermissionEvaluator;
import shade.dev.local.security.type.permissionevaluator.TargetedPermissionEvaluator;

@Component
public class PermissionEvaluatorManager {

    private final ApplicationContext applicationContext;
    private static final TargetedPermissionEvaluator denyAll = new DenyAllPermissionEvaluator();

    @Autowired
    public PermissionEvaluatorManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public PermissionEvaluator targetedPermissionEvaluator(String className) {
        if (className == null) {
            return denyAll;
        }

        return applicationContext.getBeansOfType(TargetedPermissionEvaluator.class)
                                 .values()
                                 .stream()
                                 .filter(it -> it.getTargetType().equals(className))
                                 .findFirst()
                                 .orElse(denyAll);
    }

}
