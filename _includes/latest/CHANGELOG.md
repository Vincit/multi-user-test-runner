Multi User Test Runner
======================

# 0.3.0

## Breaking changes

* `SpringMultiUserTestClassRunner` is in its own artifact: spring-test-class-runner
* Default TestUsers runner is changed from `SpringMultiUserTestClassRunner` to `BlockMultiUserTestClassRunner`
* Runner configuration is done via `MultiUserTestConfig` annotation. Annotation can be added to a base class to reduce boilerplage code.
* Creator is logged after `@Before` methods but just before calling the test method. Previously 
  it was called in `AbstractUserRoleIT`class's `@Before` method which made impossible to create custom users
  which could be used as creator user.