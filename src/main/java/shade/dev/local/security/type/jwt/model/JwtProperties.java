package shade.dev.local.security.type.jwt.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "shade.jwt")
public class JwtProperties {

    private String issuer;

    private Long tokenExpiration;

    private String secretKey;

    public String getSecretKey()
    {
        return secretKey;
    }

    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }

    public Long getTokenExpiration()
    {
        return tokenExpiration;
    }

    public void setTokenExpiration(Long tokenExpiration)
    {
        this.tokenExpiration = tokenExpiration;
    }

    public String getIssuer()
    {
        return issuer;
    }

    public void setIssuer(String issuer)
    {
        this.issuer = issuer;
    }

}
