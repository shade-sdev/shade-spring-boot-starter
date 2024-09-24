package shade.dev.local.security.type.permissionevaluator;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.PermissionEvaluator;


public class PermissionEvaluatorManager {

    private final ApplicationContext applicationContext;
    private static final TargetedPermissionEvaluator denyAll = new DenyAllPermissionEvaluator();

    public PermissionEvaluatorManager(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    public PermissionEvaluator targetedPermissionEvaluator(String className)
    {
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
