package hello.controller;

import hello.entity.User;
import hello.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private UserService userService;
    private AuthenticationManager authenticationManager;

    @Inject
    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }



    @GetMapping("/auth")
    @ResponseBody
    public Object auth(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User loginedInUser = userService.getUserByUsername(username);

        if(loginedInUser == null){
            return new Result("ok","用户未登录",false);
        }else{
            return new Result("ok","",true,loginedInUser);
        }

    }

    @PostMapping("/auth/register")
    @ResponseBody
    public Result register(@RequestBody Map<String,Object> usernameAndPassword){
        String username = usernameAndPassword.get("username").toString();
        String password = usernameAndPassword.get("password").toString();

        if( username == null || password == null){
            return new Result("fail","username/password == null",false);
        }

        if( username.length() < 1 || username.length() > 15){
            return new Result("fail","invalid username",false);
        }

        if( password.length() < 6 || password.length() > 15){
            return new Result("fail","invalid password",false);
        }

        User user = userService.getUserByUsername(username);

        if(user == null){
            userService.save(username,password);
            return new Result("ok","success!",false);
        }else{
            return new Result("fail","user already exists",false);
        }

    }

    @GetMapping("/auth/logout")
    @ResponseBody
    public Result logout(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User loginedInUser = userService.getUserByUsername(username);

        if(loginedInUser == null){
            return new Result("ok","用户未登录",false);
        }else{
            SecurityContextHolder.clearContext();
            return new Result("ok","注销成功",false);
        }


    }

    // 还可以直接拿到 map格式的 自动将请求的json 转换为 map
    @PostMapping("/auth/login")
    @ResponseBody
    public Result login(@RequestBody Map<String,Object> usernameAndPassword){
        String username = usernameAndPassword.get("username").toString();
        String password = usernameAndPassword.get("password").toString();

        UserDetails userDetails = null;

        try{
            userDetails = userService.loadUserByUsername(username);
        }catch (UsernameNotFoundException e) {
            return new Result("fail","用户不存在",false);
        }

        // 比对账号密码
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails,password,userDetails.getAuthorities());

        // 鉴权
        try{
            authenticationManager.authenticate(token);
            // 把用户信息保存在一个地方
            // Cookie
            SecurityContextHolder.getContext().setAuthentication(token);

            return new Result("ok","登录成功",true,userService.getUserByUsername(username));
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