package fi.vincit.multiusertest.context;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import fi.vincit.multiusertest.util.User;

@Service
public class UserService {

    private HashMap<String, User> users = new HashMap<>();

    public void clear() {
        users.clear();
    }

    public User addUser(User user) {
        users.put(user.getUsername(), user);
        return user;
    }

    public User findByUsername(String username) {
        return users.get(username);
    }

}
