package fi.vincit.mutrproject.configuration;

import fi.vincit.multiusertest.test.MultiUserConfig;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public MultiUserConfig<User, Role> multiUserConfig() {
        return new TestMultiUserConfig();
    }

    @Bean
    public TestMultiUserRestConfig multiUserRestConfig() {
        return new TestMultiUserRestConfig();
    }

    @Bean
    public TestMultiRoleConfig multiRoleRestConfig() {
        return new TestMultiRoleConfig();
    }

    @Bean
    public TestMultiUserAliasConfig multiUserAliasConfig() {
        return new TestMultiUserAliasConfig();
    }

}
