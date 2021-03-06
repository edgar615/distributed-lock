package com.github.edgar615.distributedlock.spring;

import com.github.edgar615.distributedlock.DistributeLock;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  @Override
  @DistributeLock(lockKey = "'order:' + #p0", expire = 1000L, lockValue = "#applicationId")
  public boolean pay(String orderNo, int sleepSecond) {
    if (sleepSecond > 0) {
      try {
        TimeUnit.SECONDS.sleep(sleepSecond);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return true;
  }
}
