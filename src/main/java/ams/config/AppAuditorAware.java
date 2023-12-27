package ams.config;

import ams.security.SecurityUtil;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AppAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityUtil.getCurrentUserLogin().orElse("Anonymous"));
    }
}
