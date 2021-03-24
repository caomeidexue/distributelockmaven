package com.jiang.distributelock.aop;

import com.jiang.distributelock.annotation.DistributeLock;
import com.jiang.distributelock.service.DistributeLockService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * 〈功能概述〉<br>
 *
 * @author: yiche
 * @date: 2021/3/9 11:13 上午
 */
@Aspect
@Component
public class DistributeLockAspect {

    //private static final Logger logger = LoggerFactory.getLogger(DistributeLockAspect.class);


    @Autowired
    private DistributeLockService distributeLockService;

    @Pointcut("@annotation(com.jiang.distributelock.annotation.DistributeLock)")
    public void annotationPointCut() {}


    @Around("annotationPointCut()")
    public Object beforeBusExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        Object result;
        Class<?> targetCls=proceedingJoinPoint.getTarget().getClass();
        //获取方法签名(通过此签名获取目标方法信息)
        MethodSignature ms=(MethodSignature)proceedingJoinPoint.getSignature();
        //获取目标方法上的注解指定的操作名称
        Method targetMethod= targetCls.getDeclaredMethod(ms.getName(),ms.getParameterTypes());
        DistributeLock distributeLock= targetMethod.getAnnotation(DistributeLock.class);
        String lockKey = distributeLock.lockKey();

        //得到el表达式
        String el = distributeLock.lockKey();
        //解析el表达式，将#id等替换为参数值
        ExpressionParser expressionParser = new SpelExpressionParser();
        Expression expression = expressionParser.parseExpression(el);
        EvaluationContext context = new StandardEvaluationContext();
        String[] parameterNames = ms.getParameterNames();
        Object[] args = proceedingJoinPoint.getArgs();
        for (int i = 0; i <parameterNames.length ; i++) {
            context.setVariable(parameterNames[i],args[i]);
        }
        String key = expression.getValue(context).toString();
        System.out.println("springEl表达式解析后的key:"+key);

        try {
            boolean dlFlag = distributeLockService.tryLock(distributeLock.lockKey(),distributeLock.expireTime(),distributeLock.timeUnit());
            if (!dlFlag) {
                //throw new DlException("# 业务繁忙，请稍后重试");
                System.out.println("分布式锁上锁有问题:{}"+distributeLock.lockKey());
                return null;
            }
            // 添加续期任务到JOB中
            //lockKeyList.add(new RedisLockInfo(lockKey, distributeLock.expire(), distributeLock.tryCount(), Thread.currentThread()));
            result = proceedingJoinPoint.proceed();
            distributeLockService.releaseLock(lockKey);
            System.out.println("# [END]分布式锁,删除key:" + distributeLock.lockKey());
            return result;
        } /*catch (DlException de) {
            log.warn("# 分布式锁，{}", de.getMsg());
        } catch (InternalException ie) {
            log.error("# 分布式锁,请求超时", ie.getMessage());
            throw new Exception(ie.getMessage());*/
         catch (Exception e) {
             System.out.println("# 分布式锁,创建失败:"+e.getMessage());
             distributeLockService.releaseLock(lockKey);
        }
        return false;
    }
}
