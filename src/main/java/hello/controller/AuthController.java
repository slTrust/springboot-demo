package hello.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class AuthController {
    @GetMapping("/auth")
    @ResponseBody
    public Object auth(){
        return new Result();
    }


    /*
    @PostMapping("/auth/login")
    public void login(@RequestBody String usernameAndPwd){
        System.out.println(usernameAndPwd);
        // 这里返回的 json字符串
    }
    */

    // 还可以直接拿到 map格式的 自动将请求的json 转换为 map
    public void login(@RequestBody Map<String,Object> usernameAndPassword){
        System.out.println(usernameAndPassword);
    }

    private static class Result{
        public String getStatus(){
            return "ok";
        }

        public boolean getIsLogin(){
            return false;
        }
    }

}
