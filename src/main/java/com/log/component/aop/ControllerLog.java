package com.log.component.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Aspect
@Component("controllerLog")
@Slf4j
public class ControllerLog {

    @Pointcut("@annotation(com.log.component.aop.ControllerLogger)")
    public void logPointCut() {

    }

    @Around(value = "logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        return process(point);
    }

    public Object process(ProceedingJoinPoint point) throws Throwable {
        System.out.println("\n");
        log.info("==========================  " + getCurrentMethodName(point) + " starting =============================");
        long beginTime = System.currentTimeMillis();
        long time = System.currentTimeMillis() - beginTime;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes.getRequest() != null) {
            request = attributes.getRequest();
        }
        log.info("API ：" + request.getRequestURI());
        log.info("类方法 : " + point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName());

        log.info("请求方式：" + request.getMethod());
        log.info("请求参数 : " + Arrays.toString(getParams(point)));
        Object result = point.proceed();
        log.info("返回结果 : " + result);
        log.info("开始时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss").format(new Date()));
        log.info("结束时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss").format(new Date()));
        log.info("耗时 : " + time);
        log.info("==========================  " + getCurrentMethodName(point) + " ending =============================\n");
        return result;
    }

//    @Before(value = "logPointCut()")
//    private void before(JoinPoint point) {
//        log.info("==========================  " + getCurrentMethodName(point) + " starting =============================");
//    }

//    @After(value = "logPointCut()")
//    private void after(JoinPoint point) {
//        log.info("==========================  " + getCurrentMethodName(point) + " ending =============================");
//
//    }

    @AfterThrowing(value = "logPointCut()", throwing = "e")
    private void throwAfter(JoinPoint point, Exception e) {
        log.error(e.getMessage());
        log.info("==========================  " + getCurrentMethodName(point) + " ending =============================\n");

    }

    public String getCurrentMethodName(JoinPoint point) {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Object target = point.getTarget();
        try {
            return target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes()).getName();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Object[] getParams(JoinPoint point) {
        if (point.getArgs().length == 0) {
            return new Object[]{};
        }
        return point.getArgs();
    }
}