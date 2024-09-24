package shade.dev.local.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import shade.dev.local.security.type.permissionevaluator.MainPermissionEvaluator;
import shade.dev.local.security.type.permissionevaluator.PermissionEvaluatorManager;

@Configuration
@ConditionalOnMissingBean(MethodSecurityExpressionHandler.class)
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true, proxyTargetClass = true)
public class MethodSecurityAutoConfiguration {

    private final PermissionEvaluatorManager manager;

    @Autowired
    public MethodSecurityAutoConfiguration(PermissionEvaluatorManager manager) {
        this.manager = manager;
    }

    @Bean
    public MethodSecurityExpressionHandler handler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(new MainPermissionEvaluator(manager));
        return handler;
    }

}
