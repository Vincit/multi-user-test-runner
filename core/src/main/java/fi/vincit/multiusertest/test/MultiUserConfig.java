package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.rule.Authorization;

public interface MultiUserConfig<USER, ROLE> extends UserFactory<USER, ROLE>, RoleConverter<ROLE>, UserRoleIT<USER> {
    void setAuthorizationRule(Authorization authorizationRule);
    void initialize();
}
