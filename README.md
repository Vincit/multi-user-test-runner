Multi User Test Runner
======================

This custom JUnit test runner is for testing Spring web-apps with multiple roles, users and their combinations. 
The library makes it easy to test many authorization scenarios with minimal configuration. It is
easy to verify that your system denies and permits the access for correct users even in more complex 
scenarios where the authorization depends on multiple users and their roles.

Originally the library was created to test the security of Spring service-layer methods. Now the core 
library also with any plain Java classes and has been successfully used with REST-assured based API testing.
For the Spring support, use the `multi-user-test-runner-spring` module.

# Requirements

 * Java 7 or newer
 * JUnit 4.12 or newer
 
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
        <version>0.3.0-beta4</version>
    <scope>test</scope>
</dependency>

<!-- Spring support (optional) -->
<dependency>
    <groupId>fi.vincit</groupId>
    <artifactId>multi-user-test-runner-spring</artifactId>
        <version>0.3.0-beta4</version>
    <scope>test</scope>
</dependency>
```

## Gradle

```groovy
dependencies {
    test 'fi.vincit:multi-user-test-runner:0.3.0-beta4'
    // Spring support (optional)
    test 'fi.vincit:multi-user-test-runner-spring:0.3.0-beta4'
}
```

# Usage

Usage is simple:

## Configuring the Test Class

Configure the base test class:

1. Create a configured abstract class by extending `AbstractUserRoleIT` class and implement methods. 
   This will be the base class for your tests.
1. Configure the runner and set the default exception to expect on failure with the annotation `@MultiUserTestConfig`
1. Create a test class which is extended from your configured class.
1. Add `@TestUsers` annotation for your test class and define roles/users to use in the tests

Write the tests:

1. Write the test methods
1. Add an assertion. For example, `authentication().expect(toFail(ifAnyOf("user:user")));` before the method under test 
   to define which roles/users are expected to fail

If a method fails when not expected the following error is shown:

## Assertion Error Messages

If test method fails when not expected:

```
java.lang.AssertionError: Not expected to fail with user role role:ROLE_ADMIN
<stack trace...>
Caused by: org.springframework.security.access.AccessDeniedException: Permission denied
<stack trace...>
```

If tested method doesn't fail when expected:

```
java.lang.AssertionError: Expected to fail with user role role:ROLE_USER
<stack trace...>
```

# Test Configuration

## Runner Classes

The default runner is the `BlockMultiUserTestClassRunner`. When using with Spring service-layer methods
`SpringMultiUserTestClassRunner` from `multi-user-test-runner-spring` module should be used.
The Spring runner loads the Spring context before the tests. This mean you can use Spring's dependency
injection, `@ContextConfiguration` etc. with your test classes. The used runner can be configured using 
`@MultiUserTestConfig` annotation's `runner` parameter.

### Custom Runner Classes

It is also possible to create your own custom runner. The custom runner has to  extend JUnit's 
`org.junit.runners.ParentRunner` (doesn't have to be direct superclass) class and has to have 
a constructor with following signature:

`CustomRunner(Class<?> clazz, UserIdentifier creatorIdentifier, UserIdentifier userIdentifier)`.

When creating a custom runner class it is important to note that `AbstractUserRoleIT.logInAs(LoginRole)` 
method  has to be called *after* `@Before` methods and *before* calling the actual test method. This will
enable creating users in `@Before` methods so that they can be used as creators.

The `RunnerDelegate` class contains helper methods for creating custom runner classes. Most of time
the runner implementation can just call the `RunnerDelegate` class methods without any additional logic.
But for example implementing the `withBefores` method may require some logic in order to make the
`@Before` calls to work correctly (See implementation of `BlockMultiUserTestClassRunner#withBefore` method).

## Default Exception

By default `IllegalStateException` is expected as the exception that is thrown on failure. Other
exceptions are ignored by the runner and will be handled normally by the test method. 
`@MultiUserTestConfig` annotation can be used to change the default exception class for a test 
class. The annotation is inherited so it can be added to a configured base class to reduce 
boilerplate code.

The other options to change the expected class are to do it in `@Before` method or in the
test method itself. This can be achieved by calling `authentication().setExpectedException()`
method. The exception is reset to default value before each test method.

## Creating Custom Users

In order to use custom users with `@TestUsers` annotation it is possible to create users
in JUnit's `@Before` methods. Another way to achieve this is to use a library like DBUnit to 
create the users to database before the test method.


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

## Changing User During Test

By default the creator user is logged in by using the implemented `loginWithUser(USER user)` method. To change
the test to use the user the `logInAs(LoginRole role)` method can be called at any point of the test method.
This method takes `LoginRole.CREATOR` or `LoginRole.USER` as parameter. Normally after creating data with
the creator user the user is changed before calling the method under test:

```java
@Test
public void fetchProduct() {
    String productId = productService.createProduct("Ice cream");

    logInAs(LoginRole.USER);

    authorization().expect(toFail(ifAnyOf("role:ROLE_ANONYMOUS")));
    productService.fetchProduct(productId);
}
```

## The Special Roles

`TestUsers.CREATOR` can be used to use the current creator user as the user. A new user is not created
but the same user is fetched with `AbstractUserRoleIT#getUserByUsername(String)` method. This can't
be used as a creator user definition.

`TestUsers.NEW_USER` can be used to create a new user with the same role as the current creator user has. 
This can't be used as a creator definitions or if the creator roles have one or more creators defined with 
existing user definition.

## Ignoring Test Method for Specified User Definitions

It is also possible to run certain test methods with only specific user definitions by adding `@TestUsers` 
annotation to the test method.

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


# Assertions

## Simple Authorization Assertion

Assertions are easy to write. They are made by calling `authorization().expect()` method. 
The simplest way to assert is to use one of the following:

```java
// In version 0.1
authorization().expect(toFail(ifAnyOf("role:ROLE_USER", "role:ROLE_ADMIN", "user:User1")));

authorization().expect(notToFail(ifAnyOf("role:ROLE_USER", "role:ROLE_ADMIN", "user:User1")));

// In version 0.2 also possible
authorization().expect(toFail(ifAnyOf(roles("ROLE_USER", "ROLE_ADMIN"), users("User1"))));

authorization().expect(notToFail(ifAnyOf(roles("ROLE_USER", "ROLE_ADMIN"), users("User1"))));
```

This will simply fail/pass test depending if the following call throws/doesn't throw an exception
with the current user.

## Stop Waiting for Exceptions

To stop waiting for a method to fail use:

```java
authorization().dontExpectToFail();
```

This call will first check if the methods between the previous assertion and this call were supposed to fail. 
If they were, the `dontExpectToFail()` call will throw an `AssertionError` to make the test fail.

## Advanced Assertions

From version 0.2 onwards there are also advanced assertions which work best with Java 8 lambdas.

Assert that call fails/doesn't fail:

```java
authorization().expect(call(() -> service.doSomething(value)).toFail(ifAnyOf("role:ROLE_ADMIN")));
authorization().expect(call(() -> service.doSomething(value)).notToFail(ifAnyOf("role:ROLE_ADMIN")));
authorization().expect(call(() -> service.doSomething(value)).toFailWithException(IllegalStateException.class, 
                                                                                  ifAnyOf("role:ROLE_ADMIN"))
                      );
```

Compare the method call's return value:

```java
authorization().expect(valueOf(() -> service.getAllUsers(value))
                .toEqual(10, ifAnyOf("role:ROLE_ADMIN"))
                .toEqual(2, ifAnyOf("role:ROLE_USER"));
```

Use any JUnit's assertion (e.g. `assertEquals` or `assertThat`):

```java
authorization().expect(valueOf(() -> service.getAllUsers(value))
                .toAssert((value) -> assertThat(value, is(10)), ifAnyOf("role:ROLE_ADMIN"))
                .toAssert((value) -> assertThat(value, is(2)), ifAnyOf("role:ROLE_USER"));
```

# Example

Configuring base class for tests:

```java

// Webapp specific implementation of test class
@MultiUserTestConfig(
        runner = SpringMultiUserTestClassRunner.class, 
        defaultException = AccessDeninedException.class)
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

Writing tests in the test class:

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
        
        authorization().expect(toFail(ifAnyOf("role:ROLE_USER", "user:existing-user-name")));
        todoService.update(updateDto);
    }
}

```

This example test class will run tests:

* ServiceIT
    * createAndUpdateTodo creator = role:ROLE_ADMIN; user = creator;
    * createAndUpdateTodo creator = role:ROLE_ADMIN; user = role:ROLE_ADMIN;
    * createAndUpdateTodo creator = role:ROLE_ADMIN; user = role:ROLE_USER;
    * createAndUpdateTodo creator = role:ROLE_ADMIN; user = user:existing-user-name;
    * createAndUpdateTodo creator = role:ROLE_USER; user = creator;
    * createAndUpdateTodo creator = role:ROLE_USER; user = role:ROLE_ADMIN;
    * createAndUpdateTodo creator = role:ROLE_USER; user = role:ROLE_USER;
    * createAndUpdateTodo creator = role:ROLE_USER; user = user:existing-user-name;

