package hello.configuration;

import hello.service.OrderService;
import hello.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JavaConfiguration {

    @Bean
    public UserService someDummyBean1() {
        return new UserService();
    }

    @Bean
    public OrderService someDummyBean2(UserService userService) {
        return new OrderService(userService);
    }

}