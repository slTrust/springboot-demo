package hello.service;

import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class OrderService {
    private UserService userService;

    @Inject
    public OrderService(UserService userService) {
        this.userService = userService;
    }

    public User placeOrder(Integer userId, String item){
        return userService.getUserById((userId));
    }
}
