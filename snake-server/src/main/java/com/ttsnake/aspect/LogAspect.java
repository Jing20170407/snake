package com.ttsnake.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LogAspect {
    @Pointcut("execution(* com.ttsnake.netty.handler.SnakeHandler.*(..))")
    private void pointCut() {
    }

    @Around(value = "pointCut()")
    public void doLog(ProceedingJoinPoint point) {
        Long start = System.currentTimeMillis();

        String clazzNama = point.getTarget().getClass().getSimpleName();
        String methodName = point.getSignature().getName();
        String name = clazzNama + "." + methodName;

        try {
            point.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.error(name, throwable);
        }
        Long end = System.currentTimeMillis();

        log.debug(name + "耗时：" + (end - start) + "毫秒");

    }
}
