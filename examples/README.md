Multi User Test Runner Examples
===============================

Simple example project and example tests to showcase some of Multi User Test Runner features.

## Basic Examples

Basic examples show how to set up the library and test an application. Some tests use the application via Java API
and some tests show how to use the library together with REST Assured in order to test application through a REST
API.

### Spring Service Layer

These examples show one way how to test a Spring Framework based application.

* [Basic Example with Roles](src/test/java/fi/vincit/mutrproject/feature/todo/TodoServiceIT.java)
* [Basic Example with Existing Users](src/test/java/fi/vincit/mutrproject/feature/todo/TodoServiceWithUsersIT.java)
* [Basic Example with RunWithUsers.WITH_PRODUCER_ROLE](src/test/java/fi/vincit/mutrproject/feature/todo/TodoServiceProducerRoleIT.java)
* [Basic Example with UserDefinitionClass](src/test/java/fi/vincit/mutrproject/feature/todo/TodoServiceUserDefinitionsIT.java)
* [Basic Configuration](src/test/java/fi/vincit/mutrproject/configuration/TestMultiUserConfig.java)

### REST Assured

These examples show one way the test library can be used with REST Assured test library.

* [REST Assured Example](src/test/java/fi/vincit/mutrproject/feature/todo/RestAssuredIT.java)
* [REST Assured Configuration](src/test/java/fi/vincit/mutrproject/configuration/TestMultiUserRestConfig.java)

## Advanced Examples

Advanced examples show how to use some of the more advanced features of the library.

### Supplier Function

This example shows how to supply role definitions using Java supplier functions. This can reduce code
duplication and make the code more readable.

* [Defining Rules with Supplier Functions](src/test/java/fi/vincit/mutrproject/feature/todo/TodoServiceSuppliersIT.java)

### Producer Role Rules

This example shows how to define rules that also depend on the producer role.

* [Producer Role Rules](src/test/java/fi/vincit/mutrproject/feature/todo/TodoServiceProducerRoleIT.java)

### Role Aliases

These examples show how to configure and use role aliases. They can help to make tests more readable.

* [Role Alias Example](src/test/java/fi/vincit/mutrproject/feature/todo/TodoServiceRoleAliasIT.java)
* [Role Alias Configuration](src/test/java/fi/vincit/mutrproject/configuration/TestMultiUserAliasConfig.java)

### Multi Role Support

These examples show hot to configure and test applications where a user can have multiple roles or
other type of privileges.

* [Multi Role Example](src/test/java/fi/vincit/mutrproject/feature/todo/TodoServiceMultiRoleIT.java)
* [Multi Role Configuration](src/test/java/fi/vincit/mutrproject/configuration/TestMultiRoleConfig.java)
