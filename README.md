Multi User Test Runner
======================

This custom JUnit test runner is for testing JVM applications with multiple roles and users.
The library makes it easy to test many authorization scenarios by reusing test cases. It's
easy to verify that your system returns correct data, denies and permits the access for correct users.

# Requirements for version 1.0 and newer

 * Java 8 or newer
 * JUnit 4.12 or newer (supports JUnit5 with JUnit vintage 4.12)

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

## Configuring the Test Class

Configuration class:

1. Create a configuration class that implements `MultiUserConfig<USER, ROLE>` interface (where `USER` and `ROLE`
   are your user and role types)
2. Implement required methods for that class

Test class:

1. Add the following annotations:
   1. `@RunWith(MultiUserTestRunner.class)`
   2. `@MultiUserTestConfig`
   3. `@RunWithUsers()` with appropriate users e.g. `@RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = {"role:ROLE_ADMIN", "role:ROLE_USER"})`
2. Add an `AuthroizationRule` and annotate it with JUnit's `@Rule`
3. Add you configuration class as member variable and annotate it with `@MultiUserConfigClass`: 
```java
// This can be initialized here or you can you @Resource or @Autowired if using Spring Framework
@MultiUserConfigClass
private MultiUserConfig<User, User.Role> multiUserConfig = new MyMultiUserConfig();

@Rule
public AuthorizationRule authorizationRule = new AuthorizationRule();
```
4. Write the tests using the `AuthorizationRule`.

### Additional Configuration for Spring Framework

To make the test work with Spring two more rules are needed: `SpringClassRule` and `SpringMethodRule`.
By adding these rules the bean under test and the config annotated by `@MultiUserConfigClass` can be autowired.
The `MultiUserConfig` will also be able to use Spring's dependency injection. The test class configuration
will look like the following:
```java
@Resource
@MultiUserConfigClass
private MultiUserConfig<User, User.Role> multiUserConfig;

@Rule
public AuthorizationRule authorizationRule = new AuthorizationRule();

@ClassRule
public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

@Rule
public final SpringMethodRule springMethodRule = new SpringMethodRule();
```

## Assertion Error Messages

If the method under test fails when not expected:

```
fi.vincit.multiusertest.exception.CallFailedError: Assertion failed with role <ROLE_USER>: Permission denied
<stack trace...>
Caused by: org.springframework.security.access.AccessDeniedException: Permission denied
<stack trace...>
```

If the method under test fails with a wrong exception:

```
fi.vincit.multiusertest.exception.CallFailedError: Unexpected exception thrown with role <producer=role:ROLE_SYSTEM_ADMIN, consumer=ROLE_USER>: Expected <IllegalStateException> but was <AccessDeniedException>: Permission denied
<stack trace...>
Caused by: org.springframework.security.access.AccessDeniedException: Permission denied
<stack trace...>
```

If a method under test doesn't fail when expected:

```
fi.vincit.multiusertest.exception.CallFailedError: Expected assertion to fail with role <ROLE_USER> with exception IllegalStateException. No exception was thrown.
<stack trace...>
```

# Test Configuration

## Creating Custom Users

In order to use custom users with `@RunWithUsers` annotation it is possible to create users
in JUnit's `@Before` methods. Another way to achieve this is to use a library like DBUnit to 
create the users to database before the test method.


# Defining Users

## Producer and Consumer

There are two types of users: **producer**s and **consumer**s.
- **Producer** user is meant for creating resources (project, task, users in the system etc.) that the *consumer* then uses. 
  Operations done with the *producer* user should always succeed. 
- **Consumer** user is the one all the calls and assertions should be done.

## User Identifiers

`@RunWithUsers` annotation defines which users are used to run the tests. Users can be defined by:
- Role (`role`)
- User instance (`user`)
- Use the current producer user (`RunWithUsers.PRODUCER`)
- Use a consumer with the same role as the producer but a different user instance (`RunWithUsers.WITH_PRODUCER_ROLE`)
- Anonymous user (`RunWithUsers.ANONYMOUS`)

Type                             | Format | Creates new user | Example | Description
-----------------------------------|--------|------------------|---------|------------
 user                              | `user:<user name>` | no | `@RunWithUsers(producers="user:admin-user", consumers="user:test-user")` | Use existing user
 role                              | `role:<role name>` | yes | `@RunWithUsers(producers="role:ROLE_ADMIN", consumers="role:ROLE_USER")` | Create new user with given role
 producer                          | `RunWithUsers.PRODUCER` | no | `@RunWithUsers(producers="role:ROLE_ADMIN", consumers={RunWithUsers.producer, "user:test-user"})` | Use the producer as the user
 new consumer with producer role   | `RunWithUsers.WITH_PRODUCER_ROLE` | yes | `@RunWithUsers(producers="role:ROLE_ADMIN", consumers={RunWithUsers.WITH_PRODUCER_ROLE, "user:test-user"})` | Create new consumer, uses same role as the producer has
 anonymous                         | `RunWithUsers.ANONYMOUS` | no | `@RunWithUsers(producers="role:ROLE_ADMIN", consumers={RunWithUsers.ANONYMOUS, "user:test-user"})` | Don't log in/clear log in details. `loginWithUser(User)` is called with null user

Some user identifiers trigger a user creation (see the table). If a new user is created, they are created for each test method and user identifier 
separately. They are created by calling `AbstractUserRoleIT#createUser(String, String, String, ROLE, LoginRole)` method. `RunWithUsers.PRODUCER` and
existing user definitions will not create new users.

## Logging in User for Tests

Logging in user is done by implementing `loginWithUser(USER user)`. The type of `USER` depends on the generic value
of `AbstractMultiUserConfig<USER, ROLE>` class. How the logging in is done, depends on the used frameworks and test types. 
E.g. if creating integration tests for Spring Framework, the user can be logged in to the SecurityContext. If using something
like `RestAssured`, the method may just store the currently logged-in user. 

By default, the producer user is logged in by using the implemented `loginWithUser(USER user)` method. The consumer
is logged in automatically when the method under test is called. After successful call of the method under test,
producer is logged back in.

```java
@Test
public void fetchProduct() {
    String productId = productService.createProduct("Ice cream");

    // Here the producer is the one who is logged in

    authorization().given(() -> productService.fetchProduct(productId)) // The lambda is called using the consumer
                .whenCalledWithAnyOf(roles("ROLE_ADMIN", "ROLE_SYSTEM_ADMIN"))
                .then(expectNotToFailIgnoringValue())
                .otherwise(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .test();

    // Here the producer is the one who is logged in
}
```

## Special Roles

`RunWithUsers.PRODUCER` can be used to use the current producer user as the user. A new consumer is not created
but the same producer user is fetched with `AbstractUserRoleIT#getUserByUsername(String)` method. This can't
be used as a producer user definition.

`RunWithUsers.WITH_PRODUCER_ROLE` can be used to create a new consumer user with the same role as the current producer user has.
This definition can't be used as a producer definitions or if the producer roles have one or more producers defined with
existing user definition.

`RunWithUsers.ANONYMOUS` means that user should not be logged in and previous log in should be cleared
if necessary. `AbstractUserRoleIT#loginWithUser(USER)` will be called with null value by default. This 
behaviour can be changed by overriding `AbstractUserRoleIT#loginAnonymous()` method.

## Role Aliases

The role definitions don't have to use the exact same role as the role type has. By implementing the
`AbstractUserRoleIT#stringToRole(String)` method appropriately the role definitions can have any value
which is then mapped to the real role.

Role aliasing feature can be used to implement a simple support for multiple roles per user. Mapping the
role definitions to multiple roles can be done for example in `AbstractUserRoleIT#createUser(String, String, String, ROLE, LoginRole)`
method.

## Multi Role Support

It's possible to define multiple roles for a role identifier. The syntax is `role:ADMIN:USER`.
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
    
    @IgnoreForUsers(producers = {"role:ROLE_ADMIN"})
    @Test
    public void ignoredForAdminProducer() {
        // Will not be run if producer is ROLE_ADMIN. Consumer can be any of the ones defined for class.
    }

}
```

## UserDefinitionClasses

Let's say that there are multiple tests that use exact same user/role definitions. To reduce duplicated user definitions,
it's possible to use UserDefinitionClass. For example, if there are multiple tests that use user identifiers `{"role:ROLE_ADMIN", "role:ROLE_USER"}` as the consumer definitions,
it's possible to make a class which defines that set of user identifiers:

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

## Debugging Tests

Sometimes when writing tests, it's required to debug a single case to see why the test doesn't pass. The framework 
doesn't allow to run only single producer/consumer pair from the UI from commandline or IDE.

For this reason it's possible to focus only on certain producers/consumers by:
1. Marking them with `$` e.g. `$role:ROLE_ADMIN`
2. Setting `RunWithUsers.focusEnabled` to `true`

These both need to be done, otherwise all tests are run.

For the predefined user identifiers (e.g. `RunWithUsers.PRODUCER`), there are `$` variants (e.g. `RunWithUsers.$PRODUCER`)
for this purpose.

```java
// This will only run tests for producer ROLE_USER and consumers PRODUCER, ROLE_ADMIN
@RunWithUsers(producers = {"role:ROLE_ADMIN", "$role:ROLE_USER"},
        consumers = {RunWithUsers.$PRODUCER, "$role:ROLE_ADMIN", "role:ROLE_USER", "user:existing-user-name"},
        focusEnabled = true)
public class ServiceIT extends AbstractConfiguredUserIT {

    @Test
    public void test1() {
        // test code here
    }

    @Test
    public void test2() {
        // test code here
    }
    
    @Test
    public void test3() {
        // test code here
    }

}
```

This also works with UserDefinitionClasses:

```java
// This will only run tests for producer ROLE_USER and consumer ROLE_USER
@RunWithUsers(producers = {"role:ROLE_ADMIN", "$role:ROLE_USER"},
        consumerClass = GeneralTestUsers.class,
        consumers = {"$role:ROLE_USER"},
        focusEnabled = true)
public class ServiceIT extends AbstractConfiguredUserIT {

    @Test
    public void test1() {
        // test code here
    }

    @Test
    public void test2() {
        // test code here
    }
    
    @Test
    public void test3() {
        // test code here
    }

}
```

# Writing the Tests and Assertions

## Identifiers in Assertions

The user identifiers `role`, `user`, `RunWithUsers.PRODUCER` and `RunWithUsers.ANONYMOUS` must be same in the assertion and in the
`@RunWithUsers` annotation. For example if the `@RunWithUsers` has `user:admin` and that user has `ROLE_ADMIN` role
it can be only asserted with `user:admin` and not `role:ROLE_ADMIN`. Also producer users can only be
asserted with `RunWithUsers.PRODUCER` definition and not with user or role. Users specified with special
definitions `RunWithUsers.PRODUCER` and `RunWithUsers.ANONYMOUS` can only be asserted with the exact same
special definitions.

`RunWithUsers.WITH_PRODUCER_ROLE` is an exception to above. It can't be used in the assertions. Instead the corresponding
producer user definition has to be used. For example if producer is `role:ROLE_ADMIN` and consumer is `RunWithUsers.WITH_PRODUCER_ROLE`
the correct way to reference the consumer in an assertion is `role:ROLE_ADMIN`.

## Assertions

The test code is written using the MUTR DSL which follows the `given-when-then` format. Usually the test format is
the following:
1. Initialize data (using producer)
2. Use the DSL
   1. Given: Define the actual call
   2. Define assertion rules for each user identifier
   3. Initiate the test by calling `.test()` method

```java
// Initialize test data

// Use DSL
authorizationRule.given(() -> 
            // The actual call
            testService.getAllUsernames()
        )
        // Assertions
        .whenCalledWithAnyOf(roles("ROLE_ADMIN", "ROLE_USER"))
        .then(expectValue(Arrays.asList("admin", "user 1", "user 2")))

        .whenCalledWithAnyOf(roles("ROLE_SUPER_ADMIN"))
        .then(expectValue(Arrays.asList("super_admin", "admin", "user 1", "user 2", "user 3")))

        .whenCalledWithAnyOf(roles("ROLE_VISITOR"))
        .then(expectExceptionInsteadOfValue(AccessDeniedException.class,
                exception -> assertThat(exception.getMessage(), is("Access is denied"))
        ))
        // Initiate the test
        .test();
```

# Example

For all examples, please visit [multi-user-test-runner/examples](https://github.com/Vincit/multi-user-test-runner/blob/0.5.0/examples/README.md)

Configuring base class for tests (using Spring Framework):

```java

// Webapp specific implementation of test class
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@MultiUserTestConfig
@ContextConfiguration(classes = {Application.class, SecurityConfig.class})
@RunWith(MultiUserTestRunner.class)
public abstract class AbstractConfiguredMultiRoleIT {

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseUtil databaseUtil;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();


    @Autowired
    @MultiUserConfigClass
    public TestMultiUserConfig config;

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @After
    public void clear() {
        userService.logout();
        databaseUtil.clearDb();
    }

    public TestMultiUserConfig config() {
        return config;
    }

    public AuthorizationRule authorization() {
        return authorizationRule;
    }

}
```

Writing tests in the test class:

```java

// Test implementation
@RunWithUsers(
        producers = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", "role:ROLE_USER"},
        consumers = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER",
                RunWithUsers.PRODUCER, RunWithUsers.ANONYMOUS}
)
public class TodoServiceIT extends AbstractConfiguredMultiRoleIT {

    // Service under test
    @Autowired
    private TodoService todoService;

    @Before
    public void init() {
        todoService.setSecureSystemAdminTodos(false);
    }

    @Test
    public void getPrivateTodoList() throws Throwable {
        // At this point the producer has been logged in automatically
        long id = todoService.createTodoList("Test list", false);

        authorization().given(() -> todoService.getTodoList(id))
                .whenCalledWithAnyOf(roles("ROLE_USER"), UserIdentifiers.anonymous())
                .then(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .otherwise(assertValue(todoList -> {
                    assertThat(todoList, notNullValue());
                    assertThat(todoList.getId(), is(id));
                    assertThat(todoList.getName(), is("Test list"));
                    assertThat(todoList.isPublicList(), is(false));
                }))
                .test();
    }

}
```

# Customizing

## Custom Test Class Runners

It is also possible to create your own custom test class runner. The custom runner has to extend JUnit's
`org.junit.runners.ParentRunner` (doesn't have to be direct superclass) class and has to have
a constructor with following signature:

`CustomRunner(RunnerConfig runnerConfig)`.

When creating a custom test class runner it's important to note that `AbstractUserRoleIT.logInAs(LoginRole)`
method  has to be called **after** `@Before` methods and **before** calling the actual test method. This will
enable creating users in `@Before` methods so that they can be used as producers.

The `RunnerDelegate` class contains helper methods for creating custom test class runners. Most of time
the runner implementation can just call the `RunnerDelegate` class' methods without any additional logic.
But for example implementing the `withBefores` method may require some additional logic in order to make the
test class' `@Before` methods to work correctly (See implementation of `BlockMultiUserTestClassRunner#withBefore` method).
