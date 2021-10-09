package fi.vincit.mutrproject.feature.user;

import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;
import fi.vincit.mutrproject.feature.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;

@Service
public class DefaultUserService implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public User getLoggedInUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof User){
                return (User) principal;
            }
        }
        return User.anonymous();
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
        User user = new User(username, username, passwordEncoder.encode(password), roles);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void clearUsers() {
        userRepository.deleteAll();
    }

    @Transactional
    @Override
    public void loginUser(String userId) {
        if (userId != null) {
            User user = userRepository.getOne(userId);
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
