package hello.controller;

import hello.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.Map;

@Controller
public class AuthController {
    private UserDetailsService userDetailsService;
    private AuthenticationManager authenticationManager;

    @Inject
    public AuthController(UserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }



    @GetMapping("/auth")
    @ResponseBody
    public Object auth(){
        return new Result("ok","用户未登录",false);
    }


    /*
    @PostMapping("/auth/login")
    public void login(@RequestBody String usernameAndPwd){
        System.out.println(usernameAndPwd);
        // 这里返回的 json字符串
    }
    */

    // 还可以直接拿到 map格式的 自动将请求的json 转换为 map
    @PostMapping("auth/login")
    @ResponseBody
    public Result login(@RequestBody Map<String,Object> usernameAndPassword){
        String username = usernameAndPassword.get("username").toString();
        String password = usernameAndPassword.get("password").toString();

        UserDetails userDetails = null;

        try{
            userDetails = userDetailsService.loadUserByUsername(username);
        }catch (UsernameNotFoundException e) {
            return new Result("fail","用户不存在",false);
        }

        // 比对账号密码
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails,password,userDetails.getAuthorities());

        // 鉴权
        try{
            authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(token);

            User loginedInUser = new User(1,"张三");
            return new Result("ok","登录成功",true,loginedInUser);
        }catch (BadCredentialsException e){
            // 鉴权识别就会抛出这个异常
            return new Result("fail","密码不正确",false);
        }

    }

    private static class Result{
        String status;
        String msg;
        boolean isLogin;
        Object data;

        public Result(String status, String msg, boolean isLogin) {
           this(status,msg,isLogin,null);
        }

        public Result(String status, String msg, boolean isLogin, Object data) {
            this.status = status;
            this.msg = msg;
            this.isLogin = isLogin;
            this.data = data;
        }

        public String getStatus() {
            return status;
        }

        public String getMsg() {
            return msg;
        }

        public boolean isLogin() {
            return isLogin;
        }

        public Object getData() {
            return data;
        }
    }

}
