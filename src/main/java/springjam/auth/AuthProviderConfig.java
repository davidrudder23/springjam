package springjam.auth;

/**
 * Created by drig on 5/16/15.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
public class AuthProviderConfig {
    @Autowired
    private SpringJamPasswordEncoder passwordEncoder;

    @Autowired
    private SpringJamUserDetailsService userDetailsService;

    @Autowired
    private SpringJamPasswordSaltSource saltSource;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setSaltSource(saltSource);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
