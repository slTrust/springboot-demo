package hello.service;

import hello.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService implements UserDetailsService {

    // 密码加密器
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // ConcurrentHashMap 是线程安全的 HashMap 线程不安全
    private Map<String,User> users = new ConcurrentHashMap<>();

    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        save("aaa","111");
    }

    public void save(String username, String password){
        users.put(username,new User(1,username,bCryptPasswordEncoder.encode(password)));
    }

//    public User getUserById(Integer id){
//        return null;
//    }

    public User getUserByUsername(String username){
        return users.get(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!users.containsKey(username)){
            throw new UsernameNotFoundException( username + "不存在!");
        }

        // 获取的是加密后的密码
        User user = users.get(username);

        return new org.springframework.security.core.userdetails.User(username,user.getEncryptedPassword(), Collections.emptyList());
    }
}
