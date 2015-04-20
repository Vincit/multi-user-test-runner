Multi User Test Runner
======================

Custom JUnit test runner for testing Spring webapps with multiple roles and users and their combinations.

# Usage

1. Create a configured abstract class by extending `AbstractUserRole` class and implement methods
2. Create a test class which is extended from your configured class.
3. Add `@TestUsers` annotation for your test class
4. Write test methods
5. User `authentication().expectToFailIfUserAnyOf()` method to mark which roles/user are expected to fail

# Example

```java

// Webapp specific implementation of test class
@RunWith(MultiUserTestRunner.class)
public class AbstractConfiguredUserIT extends AbstractUserRoleIT<User, Id<User>, User.Role> {
    
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserSecurityService userSecurityService;

    @Override
    protected void loginWithUser(User user) {
        authenticateUser(user);
    }

    @Override
    protected void authenticateUser(User user) {
        final Set<SimpleGrantedAuthority> authorities = Collections
                .singleton(new SimpleGrantedAuthority(user.getRole().toString()));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(UserInfo.createFromUser(user), user.getUsername(), authorities));
    }

    @Override
    protected User createUser(String username, String firstName, String lastName, User.Role userRole, LoginRole loginRole) {
        User user = userService.createUser(username, firstName, lastName, userRole);
        userSecurityService.setUserPassword(user, loginRole.toString());
        return user;
    }

    @Override
    protected User.Role stringToRole(String role) {
        return User.Role.valueOf(role);
    }

    @Override
    protected User getUserByUsername(String username) {
        return userService.findByUsername(username);
    }
}

```

```java

// Test implementation
@TestUsers(creators = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        users = {TestUsers.CREATOR, "role:ROLE_ADMIN", "role:ROLE_USER", "user:existing-user-name"})
public class ServiceIT extends AbstractConfiguredUserIT {

    @Test
    public void testX() {
        // Create data with "creator" user
        // Logged in as "creator" user by default
        Todo todo = todoService.create(new TodoDto("Write documentation"));
        
        logInAs(LoginRole.USER);
        authorization().expectToFailIfUserAnyOf("role:ROLE_USER", "user:existing-user-name");
        
        // Create/update/read data with "user" user
        TodoDto updateDto = new TodoDto(todo);
        updateDto.setName("Write documentation for project");
        todoService.update(updateDto);
    }
}

```
