package hello.service;

import hello.entity.User;
import hello.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    BCryptPasswordEncoder mockEncoder;
    @Mock
    UserMapper mockMapper;

    @InjectMocks
    UserService userService;

    @Test
    public void testSave(){
        // 调用 userService
        // 验证 userService 将请求转发给了 userMapper
        // given: 给定一些模拟操作 给的密码会被加密 所以是个mock的行为  此时给一个假的 加密过程的返回结果
        when(mockEncoder.encode("原始密码")).thenReturn("加密后的密码");

        // when:
        userService.save("myUser","原始密码");

        // then:
        verify(mockMapper).save("myUser","加密后的密码");
    }

    @Test
    public void testGetUserByUsername(){
        userService.getUserByUsername("myuser");

        verify(mockMapper).findUserByUsername("myuser");
    }

    // 断言抛出异常
    @Test
    public void throwExceptionWhenUserNotFound(){
        // 此句可省略 当你不进行配置的时候 就是 返回 null
        // when(mockMapper.findUserByUsername("myUser")).thenReturn(null);

        // 保证这个断言 丢出这个异常 找不到 username的时候
        Assertions.assertThrows(UsernameNotFoundException.class,()->{
           userService.loadUserByUsername("myUser");
        });
    }

    @Test
    public void returnUserDetailsWhenUserfound(){
        when(mockMapper.findUserByUsername("myUser")).
                thenReturn(new User(123,"myUser","pwd"));
        UserDetails userDetails = userService.loadUserByUsername("myUser");
        Assertions.assertEquals("myUser",userDetails.getUsername());
        Assertions.assertEquals("pwd",userDetails.getPassword());
    }
}