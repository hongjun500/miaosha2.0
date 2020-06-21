package com.hongjun.error;

/**
 * @author hongjun500
 * @date 2020/6/14 11:13
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
public enum EmBusinessError implements CommonError {
    // 通用错误类型00001
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKONWN_ERROR(10002,"未知错误"),
    // 20000开头为用户错误信息相关定义
    USER_NOT_EXIST(20001, "用户不存在"),
    USER_LOGIN_FAIL(20002, "用户手机号或密码不正确")
    ;

    private EmBusinessError(int errCode, String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    // 枚举可以拥有私有成员变量，因为本身就是一个面向对象的类(枚举类)
    private int errCode;
    private String errMsg;

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
