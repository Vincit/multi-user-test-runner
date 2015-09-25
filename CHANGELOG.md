Multi User Test Runner
======================

# 0.3.0

## Changes

* First public release
* Better configuration support
* Support for creating users in `@Before` methods
* Improved documentation

## Breaking Changes

* `SpringMultiUserTestClassRunner` is in its own artefact: spring-test-class-runner
* Default TestUsers runner is changed from `SpringMultiUserTestClassRunner` to `BlockMultiUserTestClassRunner`
* Runner configuration is done via `MultiUserTestConfig` annotation. Annotation can be added to a base class to reduce boilerplage code.
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
