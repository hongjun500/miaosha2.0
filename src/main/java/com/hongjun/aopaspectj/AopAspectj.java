package com.hongjun.aopaspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @author hongjun500
 * @date 2020/6/14 12:24
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:切面类，实现请求消息通知
 */

@Aspect
@Component
public class AopAspectj {

    public static final Logger logger = LoggerFactory.getLogger(AopAspectj.class);

    LocalDateTime localDateTime = LocalDateTime.now();
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 切入点
     */
    @Pointcut(value = "execution(* com.hongjun.controller.*.*(..))")
    public void webLog(){

    }

    /**
     * 前置通知
     */
    @Before("webLog()")
    public void before(JoinPoint joinPoint){
        // 开始计时
        startTime.set(System.currentTimeMillis());

        // 接受请求，并记录
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("来路地址: {}", request.getRemoteAddr());
        logger.info("访问地址: {}", request.getRequestURI());
        logger.info("请求方式: {}", request.getMethod());
        logger.info("请求参数: {}", Arrays.toString(joinPoint.getArgs()));
        logger.info("执行方法: {}", joinPoint.getSignature().getDeclaringType() + "--->" + joinPoint.getSignature().getName());
    }

    /**
     * 最终通知
     * @param ret
     * @throws Throwable
     */
    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        logger.info("返回: {}", ret);
        logger.info("执行时间: {}秒" ,(System.currentTimeMillis() - startTime.get())/1000);
        startTime.remove();//用完之后记得清除，不然可能导致内存泄露;
    }
}
