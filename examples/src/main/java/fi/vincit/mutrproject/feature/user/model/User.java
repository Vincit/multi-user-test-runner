package fi.vincit.mutrproject.feature.user.model;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Entity(name = "user")
public class User implements UserDetails, CredentialsContainer {

    @Column(name = "name")
    private String name;

    @Id
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "username", joinColumns = @JoinColumn(table = "user_role"))
    public Collection<Role> authorities;

    public static User anonymous() {
        return new User(null, null, null, Collections.emptySet());
    }

    public User() {
    }

    public User(String name, String username, String password, Collection<Role> authorities) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public boolean isLoggedIn() {
        return !authorities.isEmpty();
    }

    @Override
    public void eraseCredentials() {
        password = null;
    }
}
