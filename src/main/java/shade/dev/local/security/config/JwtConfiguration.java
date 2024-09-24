package shade.dev.local.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shade.dev.local.security.type.jwt.JwtTokenProvider;
import shade.dev.local.security.type.jwt.JwtAuthenticationFilter;
import shade.dev.local.security.type.jwt.model.JwtProperties;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "shade.jwt", name = {"issuer", "token-expiration", "secret-key"})
    public JwtTokenProvider jwtTokenProvider(JwtProperties jwtProperties)
    {
        return new JwtTokenProvider(jwtProperties);
    }

    @Bean
    @ConditionalOnBean(JwtTokenProvider.class)
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider)
    {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

}
