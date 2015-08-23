Multi User Test Runner
======================

# 0.3.0

## Breaking changes

* `SpringMultiUserTestClassRunner` is in its own artifact: spring-test-class-runner
* Default TestUsers runner is changed from `SpringMultiUserTestClassRunner` to `BlockJUnit4ClassRunner`
* Runner configuration is done via `MultiUserTestConfig` annotation. Annotation can be added to a base class to reduce boilerplage code.