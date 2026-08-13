package dev.ozodn.onlineshop.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new SpringSecurityAuditorAware();
    }

    private static final class SpringSecurityAuditorAware implements AuditorAware<String> {

        public static final String SYSTEM_USER = "system";

        @Override
        public Optional<String> getCurrentAuditor() {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null
                    || !authentication.isAuthenticated()
                    || authentication instanceof AnonymousAuthenticationToken) {
                return Optional.of(SYSTEM_USER);
            }

            return Optional.ofNullable(authentication.getName())
                    .filter(name -> !name.isBlank())
                    .or(() -> Optional.of("system"));
        }
    }
}
