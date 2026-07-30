package com.tk.eventmanager.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.tk.eventmanager.service..*(..))")
    //        │              │                    │
    //        │              │                    └─ любые аргументы
    //        │              └─ любой метод в пакете service и подпакетах
    //        └─ перехватываем "вокруг" (до и после)
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {

        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        System.out.println("▶ " + method + " | args: " + Arrays.toString(args));
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();  // ← вызываем оригинальный метод

        long ms = System.currentTimeMillis() - start;
        System.out.println("◀ " + method + " | " + ms + " ms | result: " + result);

        return result;
    }
}