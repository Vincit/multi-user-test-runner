package fi.vincit.mutrproject.feature.user;

import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;

import java.util.Collection;

public interface UserService {
    User getLoggedInUser();

    User createUser(String username, String password, Role role);

    User createUser(String username, String password, Collection<Role> roles);

    void clearUsers();

    void loginUser(String userId);

    void logout();

    User loadUserByUsername(String username);
}
