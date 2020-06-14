package com.hongjun.controller;

import com.hongjun.error.BusinessException;
import com.hongjun.error.EmBusinessError;
import com.hongjun.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hongjun500
 * @date 2020/6/14 12:12
 * Created with 2019.3.2.IntelliJ IDEA
 * Description: 公共异常处理基类
 */
public class BaseController {
    /**
     * 定义exceptionHandler解决未被controller层吸收的exception
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception e){
        Map<String, Object> responseData = new HashMap<>();
        if (e instanceof BusinessException){
            BusinessException businessException = (BusinessException) e;
            responseData.put("errCode", businessException.getErrCode());
            responseData.put("errMsg", businessException.getErrMsg());
        }else {
            responseData.put("errCode", EmBusinessError.UNKONWN_ERROR.getErrCode());
            responseData.put("errMsg", EmBusinessError.UNKONWN_ERROR.getErrMsg());

        }
        return CommonReturnType.create(responseData,"fail");
    }
}
