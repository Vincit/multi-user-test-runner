package fi.vincit.mutrproject.feature.user.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ROLE_USER,
    ROLE_ADMIN,
    ROLE_SYSTEM_ADMIN,
    ROLE_MODERATOR;

    @Override
    public String getAuthority() {
        return name();
    }
}
