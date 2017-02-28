package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.runner.junit5.Authorization;

public interface MultiUserConfig<USER, ROLE> extends UserFactory<USER, ROLE>, RoleConverter<ROLE>, UserRoleIT<USER> {
    void setAuthorizationRule(Authorization authorizationRule, Object testClassInstance);
    void initialize();
}
