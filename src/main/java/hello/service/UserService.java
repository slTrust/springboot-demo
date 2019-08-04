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
    private Map<String,String> userPasswords = new ConcurrentHashMap<>();

    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        save("aaa","111");
    }

    public void save(String username, String password){
        userPasswords.put(username,bCryptPasswordEncoder.encode(password));
    }

    public String getPassword(String username){
        return userPasswords.get(username);
    }

    public User getUserById(Integer id){
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!userPasswords.containsKey(username)){
            throw new UsernameNotFoundException( username + "不存在!");
        }

        // 获取的是加密后的密码
        String encodedPassword = userPasswords.get(username);

        return new org.springframework.security.core.userdetails.User(username,encodedPassword, Collections.emptyList());
    }
}
