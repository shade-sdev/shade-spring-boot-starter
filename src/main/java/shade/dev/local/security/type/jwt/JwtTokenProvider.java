package shade.dev.local.security.type.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import shade.dev.local.security.type.jwt.model.JwtProperties;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class JwtTokenProvider {

    private static final String AUTHORITIES = "authorities";

    private final JwtProperties jwtProperties;


    public JwtTokenProvider(JwtProperties jwtProperties)
    {
        this.jwtProperties = jwtProperties;
    }

    public String username(String token)
    {
        DecodedJWT jwt = decode(token);
        return jwt.getSubject();
    }

    public DecodedJWT decode(String token)
    {
        Algorithm algorithm = Algorithm.HMAC512(jwtProperties.getSecretKey());
        JWTVerifier verifier = JWT.require(algorithm)
                                  .withIssuer(jwtProperties.getIssuer())
                                  .build();

        return verifier.verify(token);
    }

    private String generateToken(UserDetails userDetails, Map<String, String> additionalClaims)
    {
        JWTCreator.Builder builder = JWT.create();
        builder.withIssuer(jwtProperties.getIssuer());
        builder.withSubject(userDetails.getUsername());
        builder.withClaim(AUTHORITIES, this.authoritiesToString(userDetails.getAuthorities()));
        builder.withIssuedAt(new Date(System.currentTimeMillis()));
        builder.withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getTokenExpiration()));
        additionalClaims.forEach(builder::withClaim);

        return builder.sign(Algorithm.HMAC512(jwtProperties.getSecretKey()));
    }

    private List<String> authoritiesToString(Collection<? extends GrantedAuthority> authorities)
    {
        return authorities.stream()
                          .map(GrantedAuthority::getAuthority)
                          .toList();
    }

}
