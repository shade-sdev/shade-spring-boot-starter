package shade.dev.local.security.type.permissionevaluator.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shade.permission-evaluator")
public class PermissionEvaluatorProperties {

    private boolean enablePermissionEvaluator;

    public boolean isEnablePermissionEvaluator() {
        return enablePermissionEvaluator;
    }

    public void setEnablePermissionEvaluator(boolean enablePermissionEvaluator) {
        this.enablePermissionEvaluator = enablePermissionEvaluator;
    }

}
