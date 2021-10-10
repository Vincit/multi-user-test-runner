package fi.vincit.mutrproject.config;

import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.annotation.PostConstruct;

@Configuration
public class AuthenticationConfig extends GlobalAuthenticationConfigurerAdapter {

    private final UserService userService;
    private final UserDetailsService userDetailsService;

    public AuthenticationConfig(UserService userService, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @PostConstruct
    public void initUsers() {
        userService.createUser("admin", "admin", Role.ROLE_ADMIN);
        userService.createUser("user", "user", Role.ROLE_USER);
    }
}
