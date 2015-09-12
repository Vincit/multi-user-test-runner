Multi User Test Runner
======================

This custom JUnit test runner is for testing Spring web-apps with multiple roles, users and their combinations. 
The library makes it easy to test many authorization scenarios with minimal configuration.

Originally the library was created to test the security of Spring service-layer methods. The library also works
with any plain Java classes and has been successfully used with REST-assured based API testing.

# Requirements

 * Java 7+
 * JUnit 4.12+
 
# Optional Requirements

## For SpringMultiUserTestClassRunner (multi-user-test-runner-spring module)
 * Spring Framework 3.2.x, 4.0.x, 4.1.x, 4.2.x (tested)
 * Spring Security 3.2.x (tested)
 
The library may work with other versions but has not been tested with versions other than the ones mentioned above.

# Getting

## Maven

```xml
<dependency>
    <groupId>fi.vincit</groupId>
    <artifactId>multi-user-test-runner</artifactId>
        <version>0.3.0-beta3</version>
    <scope>test</scope>
</dependency>

<!-- Spring support (optional) -->
<dependency>
    <groupId>fi.vincit</groupId>
    <artifactId>multi-user-test-runner-spring</artifactId>
        <version>0.3.0-beta3</version>
    <scope>test</scope>
</dependency>
```

## Gradle

```groovy
dependencies {
    test 'fi.vincit:multi-user-test-runner:0.3.0-beta3'
    // Spring support (optional)
    test 'fi.vincit:multi-user-test-runner-spring:0.3.0-beta3'
}
```

# Usage

Usage is simple:

Configure the test class:

1. Create a configured abstract class by extending `AbstractUserRoleIT` class and implement methods. 
   This will be the base class for your tests.
1. Configure the runner and set the default exception to expect on failure with the annotation `@MultiUserTestConfig`
1. Create a test class which is extended from your configured class.
1. Add `@TestUsers` annotation for your test class and define roles/users to use in the tests

Write the tests:

1. Write the test methods
1. Add an assertion. For example, `authentication().expect(toFail(ifAnyOf("user:user")));` before the method under test 
   to define which roles/users are expected to fail


# Test Configuration

## Runner Classes

The default runner is the `BlockMultiUserTestClassRunner`. When using with Spring `SpringMultiUserTestClassRunner` should be used
runner loads the Spring context before the tests. The used runner can be configured via `MultiUserTestConfig` 
annotation's `runner` parameter.

### Custom Runner Classes

It is also possible to create your own custom runner. The custom runner has to 
implement JUnit's `ParentTestRunner` class and has to have a constructor with following signature:
`CustomRunner(Class<?> clazz, UserIdentifier creatorIdentifier, UserIdentifier userIdentifier)`.

When creating runner class it is important to note that `AbstractUserRoleIT.logInAs(LoginRole)` method 
has to be called *before* calling the test method and *after* `@Before` methods. This will
enable creating users in `@Before` methods so that they can be used as creators.

The `RunnerDelegate` class contains helper methods for creating custom runner class. Most of time
the `RunnerDelegate` class methods can be just call-throughs from the custom runner to the
delegate.

## Default Exception

By default `IllegalStateException` is expected as the exception that is thrown on failure. Other
exceptions are ignored by the runner and will be handled normally by the test method. 
`@MultiUserTestConfig` annotation can be used to change the default exception class for a test 
class. The annotation is inherited so it can be added to a configured base class to reduce 
boilerplate code.

The other options to change the expected class are to do it in `@Before` method or in the
test method itself. This can be achieved by calling `authentication().setExpectedException()`
method. The default exception is reset before each test.

## Creating Custom Users

In order to use custom users with `@TestUsers` annotation it is possible to create users
in JUnit's `@Before` methods. Other method is to use a library like DBUnit to create the users to 
database before the test method.


# Defining Users

`@TestUsers` annotation defines which users are used to run the tests. Users can be defined by role (`role`),
by existing user (`user`), use the creator (`TestUsers.CREATOR`) user or use a user with the same role as the creator 
(`TestUsers.NEW_USER`). All these definition types can be mixed. The possible definitions are shown in the table below.

  Type   | Format | Example | Description
---------|--------|---------|------------
  user   | `user:<user name>` | `@TestUsers(creators="user:admin-user", users="user:test-user")` | Use existing user
  role   | `role:<role name>` | `@TestUsers(creators="role:ROLE_ADMIN", users="role:ROLE_USER")` | Create new user with given role
 creator | `TestUsers.CREATOR` | `@TestUsers(creators="role:ROLE_ADMIN", users={TestUsers.CREATOR, "user:test-user"})` | Use the creator as the user
new user | `TestUsers.NEW_USER` | `@TestUsers(creators="role:ROLE_ADMIN", users={TestUsers.NEW_USER, "user:test-user"})` | Create new user, uses same role as the creator has

Each role and `NEW_USER` definition will create new users for each test method separately. They are created by calling 
`AbstractUserRoleIT#createUser(String, String, String, ROLE, LoginRole)` method.

## The Special Roles

`TestUsers.CREATOR` can be used to use the current creator user as the user. A new user is not created
but the same user is fetched with `AbstractUserRoleIT#getUserByUsername(String)` method. This can't
be used as a creator user definition.

`TestUsers.NEW_USER` can be used to create a new user with the same role as the current creator user has. 
This can't be used as a creator definitions or if the creator roles have one or more creators defined with 
existing user definition.

# Assertions

## Simple authorization assertion

The most simplest is way to assert is to use one of the following:

```java
// In version 0.1
authorization().expect(toFail(ifAnyOf("role:ROLE_USER", "role:ROLE_ADMIN", "user:User1")));

// In version 0.2 also possible
authorization().expect(toFail(ifAnyOf(roles("ROLE_USER", "ROLE_ADMIN"), users("User1"))));
```

This will simply fail/pass test depending if the following call throws/doesn't throw an exception.

## Advanced assertions

From version 0.2 onwards there are also advanced assertions which work best with Java 8 lambdas.

Assert that call fails/doesn't fail:

```java
authorization().expect(call(() -> service.doSomething(value)).toFail(ifAnyOf("role:ROLE_ADMIN")));
authorization().expect(call(() -> service.doSomething(value)).notToFail(ifAnyOf("role:ROLE_ADMIN")));
authorization().expect(call(() -> service.doSomething(value)).toFailWithException(IllegalStateException.class, ifAnyOf("role:ROLE_ADMIN")));
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
@MultiUserTestConfig(
        runner = SpringMultiUserTestClassRunner.class, 
        defaultException = AccessDeninedException.class)
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

