package hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class AuthControllerTest {

    private MockMvc mvc;

    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp(){
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(userService,authenticationManager)).build();
    }

    /*
    假如下面有两个测试

    @Test
    void test1(){

    }

    @Test
    void test2(){

    }
    在 JVM 的某个地方
    测试前一定现有本类的实例对象
    {
        AuthControllerTest testInstance = new AuthControllerTest();
        testInstance.test1();
        // 这个过程是 junit帮我们做的
    }

    // 问题来了

    调用的时候是怎样的？

    这样？
    {
        AuthControllerTest testInstance = new AuthControllerTest();
        testInstance.test1();
        testInstance.test2();
    }

    还是这样？
    {
        AuthControllerTest testInstance1 = new AuthControllerTest();
        testInstance1.test1();

        AuthControllerTest testInstance2 = new AuthControllerTest();
        testInstance2.test2();
    }
    // 答案是第二种

    如果你有 BeforeEach
    {
        AuthControllerTest testInstance1 = new AuthControllerTest();
        testInstance1.setUp();
        testInstance1.test1();

        AuthControllerTest testInstance2 = new AuthControllerTest();
        testInstance2.setUp();
        testInstance2.test2();
    }
    那么就是这样 ,保证了每个测试的环境的独立
    */

    @Test
    void returnNotLoginByDefault() throws Exception {
        // 发起 get 请求 并期待返回 status 是 ok
        mvc.perform(get("/auth")).andExpect(status().isOk()).andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("用户未登录")));
    }

    @Test
    void testLogin() throws Exception {
        /*
        检查 /auth 的返回值，处于 登录状态
        */
        mvc.perform(get("/auth")).andExpect(status().isOk()).andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("用户未登录")));

        // 使用 /auth/login 登录
        Map<String,String> usernameAndPassword = new HashMap<>();
        usernameAndPassword.put("username","my_user");
        usernameAndPassword.put("password","my_pwd");

        new ObjectMapper().writeValueAsString(usernameAndPassword);

        // mock userDetail
        Mockito.when(userService.loadUserByUsername("my_user")).thenReturn(new User("my_user",bCryptPasswordEncoder.encode("my_pwd"),Collections.emptyList()));

        // 发起登录请求
        MvcResult response = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(usernameAndPassword)))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("登录成功")))
                .andReturn();

        // 因为前面的鉴权不再 我们这个测试用例的范围内 所以拿不到 cookie 的
        // System.out.println(Arrays.toString(response.getResponse().getCookies())); // 返回的 是 []

        // 带着 session 去登录
        HttpSession session = response.getRequest().getSession();
        // 此时还是失败 因为 getUserByUsername 没有mock
        Mockito.when(userService.getUserByUsername("my_user")).thenReturn(new hello.entity.User(123,"my_user",bCryptPasswordEncoder.encode("my_pwd")));

        mvc.perform(get("/auth").session((MockHttpSession) session)).andExpect(status().isOk()).andExpect(result->{
            System.out.println(result.getResponse().getContentAsString());
            Assertions.assertTrue(result.getResponse().getContentAsString().contains("my_user"));
        });

    }
}