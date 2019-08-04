package hello.entity;

public class Result {
    String status;
    String msg;
    boolean isLogin;
    Object data;

    public static Result failure(String message){
        return new Result("fail",message,false);
    }

    // 改为 private 这样 所有 fail 的结果都是你预期的
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
