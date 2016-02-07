package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.rule.AuthorizationRule;

public interface MultiUserConfig<USER, ROLE> extends UserFactory<USER, ROLE>, RoleConverter<ROLE>, UserRoleIT<USER> {
    void setAuthorizationRule(AuthorizationRule authorizationRule, Object testClassInstance);
}
