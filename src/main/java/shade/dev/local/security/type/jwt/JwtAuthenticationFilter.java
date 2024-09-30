package shade.dev.local.security.type.jwt;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORITIES = "authorities";

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider)
    {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        Optional<String> jwtFromRequest = getJwtFromRequest(request);

        if (jwtFromRequest.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = jwtFromRequest.get();
            String username = jwtTokenProvider.username(token);
            DecodedJWT decodedJWT = jwtTokenProvider.decode(token);
            List<String> authorities = decodedJWT.getClaim(AUTHORITIES).asList(String.class);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username, null, authorities.stream().map(SimpleGrantedAuthority::new).toList());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> getJwtFromRequest(HttpServletRequest request)
    {
        return Optional.of(request)
                       .map(it -> request.getHeader(AUTHORIZATION_HEADER))
                       .filter(it -> it.startsWith(BEARER_PREFIX) && StringUtils.hasText(it))
                       .map(it -> it.substring(BEARER_PREFIX.length()))
                       .filter(StringUtils::hasText);
    }

}
