package com.github.edgar615.distributedlock.spring;

import com.github.edgar615.distributedlock.AccuireLockFailedException;
import com.github.edgar615.distributedlock.DistributeLock;
import com.github.edgar615.distributedlock.DistributedLockImpl;
import com.github.edgar615.distributedlock.DistributedLockManager;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@Aspect
public class DistributedLockAspect {

  private static final Logger LOGGER = LoggerFactory.getLogger(DistributedLockAspect.class);

  private final ExpressionParser parser = new SpelExpressionParser();
  private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

  private final DistributedLockManager distributedLockManager;

  /**
   * 应用ID，暂时先随机生成，后面根据服务发现来生成
   */
  private final String applicationId;

  public DistributedLockAspect(DistributedLockManager distributedLockManager) {
    this.distributedLockManager = distributedLockManager;
    this.applicationId = UUID.randomUUID().toString().replace("-", "");
  }

  @Around("@annotation(com.github.edgar615.distributedlock.DistributeLock) && @annotation(distributeLock)")
  public Object around(ProceedingJoinPoint pjp, DistributeLock distributeLock) throws Throwable {
    Object[] args = pjp.getArgs();
    Method method = ((MethodSignature) pjp.getSignature()).getMethod();
    String[] params = discoverer.getParameterNames(method);
    EvaluationContext context = new StandardEvaluationContext();
    context.setVariable("applicationId", applicationId);
    for (int i = 0; i < params.length; i++) {
      context.setVariable(params[i], args[i]);
      context.setVariable("p" + i, args[i]);
    }
    Expression keyExpression = parser.parseExpression(distributeLock.lockKey());
    String lockKey = keyExpression.getValue(context, String.class);
    Expression valueExpression = parser.parseExpression(distributeLock.lockValue());
    String lockValue = valueExpression.getValue(context, String.class);

    DistributedLockImpl distributedLock = new DistributedLockImpl(distributeLock.storeName(),
        lockKey, lockValue, distributeLock.expireUnit().toMillis(distributeLock.expire()));
    distributedLock.setRetryIntervalMills(distributeLock.retryIntervalUnit().toMillis(distributeLock.retryInterval()));
    distributedLock.setRetryTimes(distributeLock.retryTimes());
    boolean lockAquired = distributedLockManager.acquire(distributedLock);
    if (!lockAquired) {
      LOGGER.warn("Acquire lock failed");
      throw new AccuireLockFailedException();
    } else {
      LOGGER.info("Acquired lock successfully");
    }

    Object rawResult = pjp.proceed();
    boolean lockReleased = distributedLockManager
        .release(distributedLock.storeName(), lockKey, lockValue);
    if (!lockReleased) {
      LOGGER.warn("Released lock failed");
    } else {
      LOGGER.info("Released lock successfully");
    }
    return rawResult;
  }
}
