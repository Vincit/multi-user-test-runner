Multi User Test Runner
======================


# 1.0.0

## Changes

* Removed old assertion methods

## Breaking Changes

Version 1.0 removes deprecated APIs and feature in order to make the library
cleaner and easier to use. Expectation API2 is from now onwards known as 
Expectation API.

* Old assertion methods are removed. Only available way is to use 
  Expectation API v2 introduced in MUTR 0.5.
* `expectation2` package has been renamed to `expectation`
* Clean up Expectations API. Changes make the API easier to understand and use when there is less methods
  with the same name but different arguments.
  * Removed `whenCalledWith(anyOf(...))` methods calls in favour of `whenCalledWithAnyOf(...)`
  * Removed String argument based `whenCalledWithAnyOf` methods in favour of using only `roles(...)` and `users(...)` methods

# 0.6.0

## Changes

Version 0.6 improves error messages and adds UserDefinitionClass

* UserDefinitionClass allows definition of general user/role groups that can 
  be shared across tests. Instead of typing the same array of users/role for
  each test separately it is now possible to define a class that can be given
  to the RunWithUsers annotation.

# 0.5.0

## Changes

Version 0.5 aim is to clean APIs and code base from legacy code. This will
break backwards compatibility.

* New expectations API
* Support for multiple roles in a role identifier
* Added `IgnoreForUsers`
* Added shorthand methods for `whenCalledWith` and `anyOf` combined method calls

## Breaking Changes

* Requires Java 8 or later
* Removed old inheritance based configuration
* Removed support for old Spring 3.x, 4.0 and 4.1 versions that don't support
  new rule based test configuration.

# 0.4.0

## Changes

* `AbstractUserRoleIT` implements interfaces `UserRoleIT<USER>`, `RoleConverter<ROLE>` and `UserFactory<USER, ROLE>`.
* Internal implementation is refactored
* `ExpectCall#toFailWithException(Class<? extends Throwable> exception, UserIdentifiers identifiers, ExceptionAssertionCall exceptionAssertionCall)`
for asserting that exception contains correct values.
* Default `AbstractUserRoleIT#getRandomUsername()` returns username always in format `testuser-<number>`.
  Previously this was either `testuser-<number>` or `testuser<number>`.
* Added new `@RunWithUsers` annotation which replaces now deprecated `@TestUsers` annotation

## Breaking Changes

* Changed term `creator` to `producer` and `user` to `consumer`. These are still generally referred as `users`
* Introduction of new interfaces requires some protected `AbstractUserRoleIT` methods to become public. 
   * `USER createUser(String username, String firstName, String lastName, ROLE userRole, LoginRole loginRole)`
   * `String getRandomUsername()`
   * `USER getUserByUsername(String username)`
   * `ROLE stringToRole(String role)`
   * `void logInAs(LoginRole role)`
   * `void loginWithUser(USER user)`
   * `Class<? extends Throwable> getDefaultException()`
   * `USER getCreator()`
   * `USER getUser()`
   * `void setUsers(UserIdentifier creatorIdentifier, UserIdentifier userIdentifier)`
* Change test class run and test method names so that IDEs can link test method name to actual test method implementation.
  Also removes duplication in the test class run and test names.

# 0.3.0

## Changes

* Better configuration support
* Support for creating users in JUnit's `@Before` methods
* `TestUsers.ANONYMOUS` definition to use for unauthenticated users
* Make `AbstractUserRoleIT#getRandomUsername` protected (was package local)

## Breaking Changes

* `SpringMultiUserTestClassRunner` is in its own artefact: spring-test-class-runner
* Default TestUsers runner is changed from `SpringMultiUserTestClassRunner` to `BlockMultiUserTestClassRunner`
* Runner configuration is done via `MultiUserTestConfig` annotation. Annotation can be added to a base class to reduce boilerplate code.
* Creator is logged after `@Before` methods but just before calling the test method. Previously 
  it was called in `AbstractUserRoleIT`class's `@Before` method which made impossible to create custom users
  which could be used as creator user.
* ExpectCall only allows to use `toFail(UserIdentifiers)` and `toFailWithException(UserIdentifier, Throwable` or
  `notToFail(UserIdentifier)` methods together. I.e. `toFail(ifAnyOf("role:ROLE_USER")).notToFail(ifAnyOf("role:ROLE_ADMIN"))`
  call chain is not possible anymore.

## 0.3.0-RC2

### Changes

* Fixed `TestUsers.NEW_USER` handling in assertions
* Make `AbstractUserRoleIT#getRandomUsername` protected (was package local)

## 0.3.0-RC1

### Changes

* First public release
* Better configuration support
* Support for creating users in `@Before` methods
* Improved documentation
* `TestUsers.ANONYMOUS` definition to use for unauthenticated users

### Breaking Changes

* `SpringMultiUserTestClassRunner` is in its own artefact: spring-test-class-runner
* Default TestUsers runner is changed from `SpringMultiUserTestClassRunner` to `BlockMultiUserTestClassRunner`
* Runner configuration is done via `MultiUserTestConfig` annotation. Annotation can be added to a base class to reduce boilerplate code.
* Creator is logged after `@Before` methods but just before calling the test method. Previously 
  it was called in `AbstractUserRoleIT`class's `@Before` method which made impossible to create custom users
  which could be used as creator user.
* ExpectCall only allows to use `toFail(UserIdentifiers)` and `toFailWithException(UserIdentifier, Throwable` or
  `notToFail(UserIdentifier)` methods together. I.e. `toFail(ifAnyOf("role:ROLE_USER")).notToFail(ifAnyOf("role:ROLE_ADMIN"))`
  call chain is not possible anymore.

# 0.2 (Internal Release)

## Changes

* Add new advanced assertions. Java 8 syntactic sugar.

## Breaking Changes

* AbstractUserRoleIT doesn't take `USER_ID` generic parameter. Now the class signature is `AbstractUserRoleIT<USER, ROLE>`

# 0.1 (Internal Release)

## Changes/Features

* First release for internal use
* Support for one role per user and existing users
* Simple assertions
