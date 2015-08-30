Multi User Test Runner
======================

Custom JUnit test runner for testing Spring webapps with multiple roles and users and their combinations. 
Library makes it easy to test many authorization scenarios with minimal configuration.

Originally library was created to test security of Spring service layer methods. Library also works
with any plain Java classes and has been successfully used with REST assured based API testing.
# Requirements

 * Java 7+
 * JUnit 4.12+
 
# Optional Requirements

## For SpringMultiUserTestClassRunner (multi-user-test-runner-spring module)
 * Spring Framework 3.2.x, 4.0.x, 4.1.x, 4.2.x (versions tested)
 * Spring Security 3.2.x (version tested)
 
Library may work with other versions, but it has not been tested other than the versions mentioned.

# Getting

## Maven

```xml
<dependency>
    <groupId>fi.vincit</groupId>
    <artifactId>multi-user-test-runner</artifactId>
    <version>0.3.0-beta1</version>
    <scope>test</scope>
</dependency>

<!-- Spring support (optional) -->
<dependency>
    <groupId>fi.vincit</groupId>
    <artifactId>multi-user-test-runner-spring</artifactId>
   <version>0.3.0-beta1</version>
    <scope>test</scope>
</dependency>
```

## Gradle

```groovy
dependencies {
    test 'fi.vincit:multi-user-test-runner:0.3.0-beta1'
    // Spring support (optional)
    test 'fi.vincit:multi-user-test-runner-spring:0.3.0-beta1'
}
```

# Usage

Usage is simple:

1. Create a configured abstract class by extending `AbstractUserRole` class and implement methods
1. Configure runner and default expected annotation with `MultiUserTestConfig`
1. Create a test class which is extended from your configured class.
1. Add `@TestUsers` annotation for your test class
1. Write test methods
1. Add `authentication().expect(toFail().ifAnyOf("user:user"));` before method to test 
   to mark which roles/users are expected to fail


# Test Configuration

## Runner Classes

By default `BlockMultiUserTestClassRunner` is used. When using with Spring `SpringMultiUserTestClassRunner` should be used
runner loads the Spring context before the tests and the Block runner will works as a plain JUnit test runner.
Used runner can be configured via `MultiUserTestConfig` annotation's `runner` argument.
It is also possible to create a custom runner. The custom runner has to implement JUnit's `ParentTestRunner` and
has to have a constructor with following format: 
`CustomRunner(Class<?> clazz, UserIdentifier creatorIdentifier, UserIdentifier userIdentifier)`.

### Custom Runner Classes

When creating runner class it is important to note that `AbstractUserRoleIT.loginAsUser()` method 
has to be called *before* calling the test method and *after* `@Before` methods. This will
enable creating users in `@Before` methods so that they can be used as creators.

`RunnerDelegate` class contains helper methods for creating custom runner class. Most of time
the `RunnerDelegate` class methods can be just call throughs from the custom runner to the
delegate.

## Default Exception

By default `IllegalStateException` is expected as the exception that is thrown on failure. Other
exceptions are ignored by the runner and will be handled normally by the test method. 
`MultiUserTestConfig`  annotation can be used to change the default exception class for a test 
class. The annotation is  inherited so it can be added to a configured base class to reduce 
boilerplate code.

The other options to change the expected class are to do it in `@Before` method or in the
test method itself. This can be achieved by calling `authentication().setExpectedException()`
method.

## Creating Custom Users

In order to use custom users with `@TestUsers` annotation it is possible to create users
in JUnit's `@Before` methods. Other method is to use a library like DBUnit to create the users to 
database before the test method.


# Assertions

## Simple authorization assertion

Most simple way to add an multi user test assertion is to use:
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

