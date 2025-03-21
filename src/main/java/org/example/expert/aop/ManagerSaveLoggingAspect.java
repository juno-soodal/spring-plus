package org.example.expert.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.domain.log.service.LogService;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ManagerSaveLoggingAspect {

    private final LogService logService;

    @Around("execution(* org.example.expert.domain.manager.service.ManagerService.saveManager(..))")
    public Object logAroundSaveManager(ProceedingJoinPoint joinPoint) throws Throwable {
            String action =  joinPoint.getSignature().getName();

        try {
            Object result = joinPoint.proceed();
            logService.saveSuccessLog(action,1L);
            return result;
        } catch (Exception e) {
            //TODO 단순 메시지 저장 시 원인 파악하기 어려움 추후 디버깅이 필요 할 경우 스택 트레이스 저장 고려
            logService.saveFailLog(action,1L, e.getMessage());
            throw e;
        }

    }
}
