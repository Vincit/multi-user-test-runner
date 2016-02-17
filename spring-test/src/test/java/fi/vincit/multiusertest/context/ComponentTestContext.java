package fi.vincit.multiusertest.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ComponentTestContext {

    @Bean
    public TestMultiUserConfig testMultiUserConfig() {
        return new TestMultiUserConfig();
    }

}
