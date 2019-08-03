package hello;

import hello.service.OrderService;
import hello.service.User;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

@RestController
public class HelloController {
    private OrderService orderService;

    @Inject
    public HelloController(OrderService orderService) {
        this.orderService = orderService;
    }

    @RequestMapping("/")
    public User index() {
        return orderService.placeOrder(1,"");
    }

}