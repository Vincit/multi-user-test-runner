package fi.vincit.multiusertest.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

    @Bean
    public UserService getUserService() {
        return new UserService();
    }
}
