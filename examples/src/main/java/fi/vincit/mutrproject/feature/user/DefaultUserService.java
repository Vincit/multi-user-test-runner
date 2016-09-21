package fi.vincit.mutrproject.feature.user;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;
import fi.vincit.mutrproject.feature.user.repository.UserRepository;

@Service
public class DefaultUserService implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> getLoggedInUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof User){
                return Optional.of((User) principal);
            }
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    @Override
    public User loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public User createUser(String username, String password, Role role) {
        return createUser(username, password, Arrays.asList(role));
    }

    @Transactional
    @Override
    public User createUser(String username, String password, Collection<Role> roles) {
        User user = new User(username, username, password, roles);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void clearUsers() {
        userRepository.deleteAll();
    }

    @Transactional
    @Override
    public void loginUser(User user) {
        if (user != null) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
            );
        } else {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

    @Transactional
    @Override
    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

}
