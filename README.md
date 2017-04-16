Multi User Test Runner
======================

This custom JUnit test runner is for testing Java applications with multiple roles, users and their combinations.
The library makes it easy to test many authorization scenarios with minimal configuration. It is
easy to verify that your system denies and permits the access for correct users even in more complex 
scenarios where the authorization depends on multiple users and their roles.

Originally the library was created to test the security of Spring service-layer methods. Now the core 
library also with any plain Java classes and has been successfully used with REST-assured based API testing.
From 0.5.0 onwards `multi-user-test-runner-spring` dependency is deprecated. Spring support is achieved by using
rules.

# Requirements version 1.0 and newer

 * Java 8 or newer
 * JUnit 4.12 or newer (supports JUnit5 with JUnit vintage 4.12)

 
 
# Optional Requirements

## multi-user-test-runner-spring module (MUTR 0.4 and earlier)
 * Spring Framework 3.2.x, 4.0.x, 4.1.x, 4.2.x (tested)
 * Spring Security 3.2.x, 4.0.x (tested)
 
The library may work with other versions but has not been tested with versions other than the ones mentioned above.

# Getting

## Maven

```xml
<dependency>
    <groupId>fi.vincit</groupId>
    <artifactId>multi-user-test-runner</artifactId>
        <version>1.0.0-beta1</version>
    <scope>test</scope>
</dependency>
```

## Gradle

```groovy
dependencies {
    test 'fi.vincit:multi-user-test-runner:1.0.0-beta1'
}
```

# Usage

Usage is simple:

## Configuring the Test Class (The New Way)

Configure the test class:

1. Create a configuration class that implements `MultiUserConfig<USER, ROLE>` interface (where USER and ROLE
   are your user and role types)
1. Configure the test runner by adding `@RunWith(MultiUserTestRunner.class)` for the test class
1. Configure users to run with by adding `@RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = "role:ROLE_ADMIN")`
   for the test class
1. Create your test class and add an `AuthroizationRule` and your config class to your test class:
```java
@MultiUserConfigClass
private MultiUserConfig<User, User.Role> multiUserConfig = new MyMultiUserConfig();

@Rule
public AuthorizationRule authorizationRule = new AuthorizationRule();
```

Write the tests:

1. Write the test methods
1. Add an assertion. For example, `authenticationRule.expect(toFail(ifAnyOf("user:user")));` before the method under test
   to define which roles/users are expected to fail

### Additional Configuration for Spring

To make the test work with Spring two more rules are needed: `SpringClassRule` and `SpringMethodRule`.
By adding these rules the bean under test and the config annotated by `@MultiUserConfigClass` can be autowired.
The `MultiUserConfig` will also be able to use Spring's dependency injection. The test class configuration
will look like the following:
```java
@Autowired
@MultiUserConfigClass
private MultiUserConfig<User, User.Role> multiUserConfig;

@Rule
public AuthorizationRule authorizationRule = new AuthorizationRule();

@ClassRule
public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

@Rule
public final SpringMethodRule springMethodRule = new SpringMethodRule();
```

### Configuring the Test Class (The Old Way)

Configure the base test class:

1. Create a configured abstract class by extending `AbstractUserRoleIT` class and implement methods. 
   This will be the base class for your tests.
1. Configure the runner and set the default exception to expect on failure with the annotation `@MultiUserTestConfig`

Write the tests:

1. Create a test class which is extended from your configured class.
1. Add `@RunWithUsers` annotation for your test class and define roles/users to use in the tests
1. Write the test methods
1. Add an assertion. For example, `authentication().expect(toFail(ifAnyOf("user:user")));` before the method under test 
   to define which roles/users are expected to fail

## Assertion Error Messages

If the method under test fails when not expected:

```
java.lang.AssertionError: Not expected to fail with user role role:ROLE_ADMIN
<stack trace...>
Caused by: org.springframework.security.access.AccessDeniedException: Permission denied
<stack trace...>
```

If a method under test doesn't fail when expected:

```
java.lang.AssertionError: Expected to fail with user role role:ROLE_USER
<stack trace...>
```

# Test Configuration

## Test Class Runners

The default runner is the `BlockMultiUserTestClassRunner`. When using with Spring service-layer methods
`SpringMultiUserTestClassRunner` from `multi-user-test-runner-spring` module should be used.
The Spring runner loads the Spring context before the tests. This mean you can use Spring's dependency
injection, `@ContextConfiguration` etc. with your test classes. The test class runner can be configured using 
`@MultiUserTestConfig` annotation's `runner` parameter.

## Default Exception

By default `IllegalStateException` is expected as the exception that is thrown on failure. Other
exceptions are ignored by the runner and will be handled normally by the test method. This behaviour can be changed:

1. Using `expecations` by asserting with `authorizationRule#expect(Expectation expectation)`method. See section `Assertions`
for more information.
1. Adding `@MultiUserTestConfig` annotation to change the default exception for a test class.
The annotation is inherited so it can be added to a configured base class to reduce boilerplate code.
1. Do it in `@Before` method or in the test method itself by calling `authorizationRule#setExpectedException()`
method. The exception is reset to default exception before each test method.

## Creating Custom Users

In order to use custom users with `@RunWithUsers` annotation it is possible to create users
in JUnit's `@Before` methods. Another way to achieve this is to use a library like DBUnit to 
create the users to database before the test method.


# Defining Users

## Producer and consumer

There are two types of users: *producer* and *consumer*. *Producer* user is meant for creating resources (project,
task, users in the system etc.) that the *consumer* then uses. Operations done with the *producer* user
should always succeed. The assertions should be done with the *consumer*. If there is need to test if creating
a resource succeeds with a specific user or role it should be done with the *consumer* (not *producer* user).
Otherwise the tests end up testing too many things at once. This library already adds a little bit of
complexity to the test methods so the tests should be kept as simple as possible.

## Definitions

`@RunWithUsers` annotation defines which users are used to run the tests. Users can be defined by role (`role`),
by existing user (`user`), use the producer (`RunWithUsers.PRODUCER`) user, use a consumer with the same role as the producer
(`RunWithUsers.WITH_PRODUCER_ROLE`) or not log in at all (`RunWithUsers.ANONYMOUS`). All these definition types can be mixed.
The possible definitions are shown in the table below.

  Type                             | Format | Example | Description
-----------------------------------|--------|---------|------------
 user                              | `user:<user name>` | `@RunWithUsers(producers="user:admin-user", consumers="user:test-user")` | Use existing user
 role                              | `role:<role name>` | `@RunWithUsers(producers="role:ROLE_ADMIN", consumers="role:ROLE_USER")` | Create new user with given role
 producer                          | `RunWithUsers.PRODUCER` | `@RunWithUsers(producers="role:ROLE_ADMIN", consumers={RunWithUsers.producer, "user:test-user"})` | Use the producer as the user
 new consumer with producer role   | `RunWithUsers.WITH_PRODUCER_ROLE` | `@RunWithUsers(producers="role:ROLE_ADMIN", consumers={RunWithUsers.WITH_PRODUCER_ROLE, "user:test-user"})` | Create new consumer, uses same role as the producer has
 anonymous                         | `RunWithUsers.ANONYMOUS` | `@RunWithUsers(producers="role:ROLE_ADMIN", consumers={RunWithUsers.ANONYMOUS, "user:test-user"})` | Don't log in/clear log in details. `loginWithUser(User)` is called with null user

Each role definition and `WITH_PRODUCER_ROLE` definition will create new users for each test method separately. They are created by calling
`AbstractUserRoleIT#createUser(String, String, String, ROLE, LoginRole)` method. `RunWithUsers.PRODUCER` and
existing user definitions will not create new users.

## Changing the User During Test

By default the producer user is logged in by using the implemented `loginWithUser(USER user)` method. To change
the test to use the consumer (i.e. current user definition) the `logInAs(LoginRole role)` method can be called at
any point of the test method. This method takes `LoginRole.PRODUCER` or `LoginRole.CONSUMER` as parameter. Normally
after creating data with the producer user the user is changed before calling the method under test:

```java
@Test
public void fetchProduct() {
    String productId = productService.createProduct("Ice cream");

    logInAs(LoginRole.CONSUMER);

    authorization().expect(toFail(ifAnyOf("role:ROLE_ANONYMOUS")));
    productService.fetchProduct(productId);
}
```

## The Special Roles

`RunWithUsers.PRODUCER` can be used to use the current producer user as the user. A new consumer is not created
but the same producer user is fetched with `AbstractUserRoleIT#getUserByUsername(String)` method. This can't
be used as a producer user definition.

`RunWithUsers.WITH_PRODUCER_ROLE` can be used to create a new consumer user with the same role as the current producer user has.
This definition can't be used as a producer definitions or if the producer roles have one or more producers defined with
existing user definition.

`RunWithUsers.ANONYMOUS` means that user should not be logged in and previous log in should be cleared
if necessary. `AbstractUserRoleIT#loginWithUser(USER)` will be called with null value by default. This 
behaviour can be changed by overriding `AbstractUserRoleIT#loginAnonymous()` method.

## Role Aliasing

The role definitions don't have to use the exact same role as the role enum has. By implementing the
`AbstractUserRoleIT#stringToRole(String)` method appropriately the role definitions can have any value
which is then mapped to the real role.

Role aliasing feature can be used to implement a simple support for multiple roles per user. Mapping the
role definitions to multiple roles can be done for example in `AbstractUserRoleIT#createUser(String, String, String, ROLE, LoginRole)`
method.

## Multi Role Support

From version 0.5 onwards it is possible to define multiple roles for a role identifier. The syntax is `role:ADMIN:USER`.
This requires the configuration class to be extended from `AbstractMultiUserAndRoleConfig`.


## Ignoring a Test Method for Specific User Definitions

It is possible to run certain test methods with only specific user definitions by adding `@RunWithUsers`
or `@IgnoreForUsers` annotation to the test method.

```java
@RunWithUsers(producers = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {RunWithUsers.PRODUCER, "role:ROLE_ADMIN", "role:ROLE_USER", "user:existing-user-name"})
public class ServiceIT extends AbstractConfiguredUserIT {
    @RunWithUsers(producers = {"role:ROLE_ADMIN"}, users = {"role:ROLE_USER", "user:existing-user-name"})
    @Test
    public void onlyForAdminProducerAndConsumerUser() {
        // Will be run only if producer is ROLE_ADMIN and consumer is either ROLE_USER or existing-user-name
    }

    @RunWithUsers(producers = {"role:ROLE_ADMIN"})
    @Test
    public void onlyForAdminAndAnyUser() {
        // Will be run only if producer is ROLE_ADMIN. Consumer can be any of the ones defined for class.
    }
    
    // From version 0.5 onwards
    @IgnoreForUsers(producers = {"role:ROLE_ADMIN"})
    @Test
    public void ignoredForAdminProducer() {
        // Will no be run if producer is ROLE_ADMIN. Consumer can be any of the ones defined for class.
    }

}
```

## UserDefinitionClasses

Let's say that there are multiple tests that use exact same user/role definitions. In 0.5 and older versions the only
possibility was to copy paste the definitions to each test. In 0.6 this issue has been solved with UserDefinitionClass.

For example there are multiple tests that take `{"role:ROLE_ADMIN", "role:ROLE_USER"}` as the consumer definitions. Now
it is possible to make a class:

```
public class GeneralTestUsers implements UserDefinitionClass {

    @Override
    public String[] getUsers() {
        return new String[] {"role:ROLE_ADMIN", "role:ROLE_USER"};
    }
}
```

This class can be given to the consumer in `RunWithUsers`:

```
@RunWithUsers(consumerClass = GeneralTestUsers.class)
public class ServiceXTest {
    // ...
```

Or to the producer:

```
@RunWithUsers(producerClass = GeneralTestUsers.class)
public class ServiceXTest {
    // ...
```

# Assertions

## Definitions in Assertions

The user definitions `role`, `user`, `RunWithUsers.PRODUCER` and `RunWithUsers.ANONYMOUS` must be same in the assertion and in the
`@RunWithUsers` annotation. For example if the `@RunWithUsers` has `user:admin` and that user has `ROLE_ADMIN` role
it can be only asserted with `user:admin` and not `role:ROLE_ADMIN`. Also producer users can only be
asserted with `RunWithUsers.PRODUCER` definition and not with user or role. Users specified with special
definitions `RunWithUsers.PRODUCER` and `RunWithUsers.ANONYMOUS` can only be asserted with the exact same
special definitions.

`RunWithUsers.WITH_PRODUCER_ROLE` is an exception to above. It can't be used in the assertions. Instead the corresponding
producer user definition has to be used. For example if producer is `role:ROLE_ADMIN` and consumer is `RunWithUsers.WITH_PRODUCER_ROLE`
the correct way to reference the consumer in an assertion is `role:ROLE_ADMIN`.

## Assertions

The preferred way to write assertions if the new expectation API introduced in version 0.5.
The new API uses less nested calls and is more natural and fluent than the previous APIs. 
Writing and reading the rules is easier when the `when-then` structure is on the same level 
(as opposed to nested like in 0.2).

```java
authorizationRule.testCall(() -> testService.getAllUsernames())
        .whenCalledWith(anyOf(roles("ROLE_ADMIN", "ROLE_USER")))
        .then(expectValue(Arrays.asList("admin", "user 1", "user 2")))

        .whenCalledWith(anyOf(roles("ROLE_SUPER_ADMIN")))
        .then(expectValue(Arrays.asList("super_admin", "admin", "user 1", "user 2", "user 3")))
        // Shorthand version of whenCalledWith + anyOf
        .whenCalledWithAnyOf(roles("ROLE_VISITOR"))
        .then(expectExceptionInsteadOfValue(AccessDeniedException.class,
                exception -> assertThat(exception.getMessage(), is("Access is denied"))
        ))
        .test();
```

# Example

For all examples, please visit [multi-user-test-runner/examples](https://github.com/Vincit/multi-user-test-runner/blob/0.5.0/examples/README.md)

Configuring base class for tests:

```java

// Webapp specific implementation of test class
@ContextConfiguration(classes = {IntegrationTestContext.class})
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@MultiUserTestConfig(
        runner = SpringMultiUserTestClassRunner.class, 
        defaultException = AccessDeniedException.class)
@RunWith(MultiUserTestRunner.class)
public class AbstractConfiguredUserIT extends AbstractUserRoleIT<User, User.Role> {
    
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserSecurityService userSecurityService;

    @Override
    public void loginWithUser(User user) {
        final Set<SimpleGrantedAuthority> authorities = Collections
                .singleton(new SimpleGrantedAuthority(user.getRole().toString()));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(UserInfo.createFromUser(user), user.getUsername(), authorities));
    }

    @Override
    public void loginAnonymous() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Override
    public User createUser(String username, String firstName, String lastName, User.Role userRole, LoginRole loginRole) {
        User user = userService.createUser(username, firstName, lastName, userRole);
        userSecurityService.setUserPassword(user, loginRole.toString());
        return user;
    }

    @Override
    public User.Role stringToRole(String role) {
        return User.Role.valueOf(role);
    }

    @Override
    public User getUserByUsername(String username) {
        return userService.findByUsername(username);
    }
}

```

Writing tests in the test class:

```java

// Test implementation
@RunWithUsers(producers = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {RunWithUsers.PRODUCER, "role:ROLE_ADMIN", "role:ROLE_USER", "user:existing-user-name"})
public class ServiceIT extends AbstractConfiguredUserIT {

    @Test
    public void createAndUpdateTodo() {
        // Create data with "producer" user
        // Logged in as "producer" user by default
        Todo todo = todoService.create(new TodoDto("Write documentation"));
        
        logInAs(LoginRole.CONSUMER);
        
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
    * createAndUpdateTodo producer = role:ROLE_ADMIN; user = producer;
    * createAndUpdateTodo producer = role:ROLE_ADMIN; user = role:ROLE_ADMIN;
    * createAndUpdateTodo producer = role:ROLE_ADMIN; user = role:ROLE_USER;
    * createAndUpdateTodo producer = role:ROLE_ADMIN; user = user:existing-user-name;
    * createAndUpdateTodo producer = role:ROLE_USER; user = producer;
    * createAndUpdateTodo producer = role:ROLE_USER; user = role:ROLE_ADMIN;
    * createAndUpdateTodo producer = role:ROLE_USER; user = role:ROLE_USER;
    * createAndUpdateTodo producer = role:ROLE_USER; user = user:existing-user-name;


# Customizing

## Custom Test Class Runners

It is also possible to create your own custom test class runner. The custom runner has to extend JUnit's
`org.junit.runners.ParentRunner` (doesn't have to be direct superclass) class and has to have
a constructor with following signature:

`CustomRunner(Class<?> clazz, UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier)`.

When creating a custom test class runner it is important to note that `AbstractUserRoleIT.logInAs(LoginRole)`
method  has to be called **after** `@Before` methods and **before** calling the actual test method. This will
enable creating users in `@Before` methods so that they can be used as producers.

The `RunnerDelegate` class contains helper methods for creating custom test class runners. Most of time
the runner implementation can just call the `RunnerDelegate` class' methods without any additional logic.
But for example implementing the `withBefores` method may require some additional logic in order to make the
test class' `@Before` methods to work correctly (See implementation of `BlockMultiUserTestClassRunner#withBefore` method).

