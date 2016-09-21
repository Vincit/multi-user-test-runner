package fi.vincit.mutrproject.feature.user;

import java.util.Collection;
import java.util.Optional;

import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;

public interface UserService {
    Optional<User> getLoggedInUser();

    User createUser(String username, String password, Role role);

    User createUser(String username, String password, Collection<Role> roles);

    void clearUsers();

    void loginUser(User user);

    void logout();

    User loadUserByUsername(String username);
}
