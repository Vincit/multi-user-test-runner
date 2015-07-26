Multi User Test Runner
======================

Custom JUnit test runner for testing Spring webapps with multiple roles and users and their combinations.

# Requirements

 * Java 7 or Java 8
 * JUnit 4.12 (version tested)
 
# Optional Requirements

## For SpringMultiUserTestClassRunner
 * Spring Framework 3.2.x or 4.0.x (versions tested)
 * Spring Security 3.2.x (version tested)
 
Library may work with other versions, but it has not been tested other than the versions mentioned.

# Usage

Usage is simple:

1. Create a configured abstract class by extending `AbstractUserRole` class and implement methods
2. Create a test class which is extended from your configured class.
3. Add `@TestUsers` annotation for your test class
4. Write test methods
5. Add `authentication().expect(toFail().ifAnyOf("user:user"));` before method to test 
   to mark which roles/users are expected to fail

# Runner Classes

By default there are two runner classes `SpringMultiUserTestClassRunner` and `BlockMultiUserTestClassRunner`. Spring
runner loads the Spring context before the tests and the Block runner will works as a plain JUnit test runner.
Used runner can be configured via `TestUsers` annotation's `runner` argument. Spring runner is used by default.
It is also possible to define a custom runner. The custom runner has to implement JUnit's `ParentTestRunner` and
has to have a constructor with following format: 
`CustomRunner(Class<?> clazz, UserIdentifier creatorIdentifier, UserIdentifier userIdentifier)`.

# Assertions

## Simple authorization assertion

Most simple way to add an multi user test assertion is to use:
```java
authorization().expect(toFail().ifAnyOf("role:ROLE_USER"));
```
This will simply fail/pass test depending if the following call throws/doesn't throw an exception.

## Advanced assertions

From version 0.2 onwards there are also advanced assertions which work best with Java 8 lambdas.

Assert that call fails/doesn't fail:
```java
authorization().expect(call(() -> service.doSomething(value)).toFail(ifAnyOf("role:ROLE_ADMIN")));
authorization().expect(call(() -> service.doSomething(value)).notToFail(ifAnyOf("role:ROLE_ADMIN")));
```

Compare method call return value:
```java
authorization().expect(valueOf(() -> service.getAllUsers(value))
                .toEqual(10, ifAnyOf("role:ROLE_ADMIN"))
                .toEqual(2, ifAnyOf("role:ROLE_USER"));
```

Use a custom assertion (e.g. JUnit `assertEquals` or `assertThat`:
```java
authorization().expect(valueOf(() -> service.getAllUsers(value))
                .toAssert((value) -> assertThat(value, is(10)), ifAnyOf("role:ROLE_ADMIN"))
                .toAssert((value) -> assertThat(value, is(2)), ifAnyOf("role:ROLE_USER"));
```

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
    public void createAndUpdateTodo() {
        // Create data with "creator" user
        // Logged in as "creator" user by default
        Todo todo = todoService.create(new TodoDto("Write documentation"));
        
        logInAs(LoginRole.USER);
        
        // Create/update/read data with "user" user
        TodoDto updateDto = new TodoDto(todo);
        updateDto.setName("Write better documentation");
        
        authorization().expect(toFail().ifAnyOf("role:ROLE_USER", "user:existing-user-name"));
        todoService.update(updateDto);
    }
}

```

This will run tests:

* ServiceIT
    * createAndUpdateTodo creator = role:ROLE_ADMIN; user = creator;
    * createAndUpdateTodo creator = role:ROLE_ADMIN; user = role:ROLE_ADMIN;
    * createAndUpdateTodo creator = role:ROLE_ADMIN; user = role:ROLE_USER;
    * createAndUpdateTodo creator = role:ROLE_ADMIN; user = user:existing-user-name;
    * createAndUpdateTodo creator = role:ROLE_USER; user = creator;
    * createAndUpdateTodo creator = role:ROLE_USER; user = role:ROLE_ADMIN;
    * createAndUpdateTodo creator = role:ROLE_USER; user = role:ROLE_USER;
    * createAndUpdateTodo creator = role:ROLE_USER; user = user:existing-user-name;
    
And if something fails due to authorization error, you will see error like:
```
java.lang.AssertionError: Not expected to fail with user role role:ROLE_ADMIN
<stack trace...>
Caused by: org.springframework.security.access.AccessDeniedException: Permission denied
<stack trace...>
```

or if tested method doesn't fail when expected:
```
java.lang.AssertionError: Expected to fail with user role role:ROLE_USER
<stack trace...>
```

It is also possible to run certain test methods with only certain roles by adding `TestUsers` annotation to the method.

```java
@TestUsers(creators = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        users = {TestUsers.CREATOR, "role:ROLE_ADMIN", "role:ROLE_USER", "user:existing-user-name"})
public class ServiceIT extends AbstractConfiguredUserIT {
    @TestUsers(creators = {"role:ROLE_ADMIN"}, users = {"role:ROLE_USER", "user:existing-user-name"})
    @Test
    public void onlyForAdminCreatorAndUserUser() {
        // Will be run only if creator is ROLE_ADMIN and user is either ROLE_USER or existing-user-name
    }
    
    @TestUsers(creators = {"role:ROLE_ADMIN"})
    @Test
    public void onlyForAdminAndAnyUser() {
        // Will be run only if creator is ROLE_ADMIN. User can be any of the ones defined for class.
    }
    
}
```

