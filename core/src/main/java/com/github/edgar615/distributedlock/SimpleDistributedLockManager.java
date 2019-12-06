package com.github.edgar615.distributedlock;

import java.util.Collection;

public class SimpleDistributedLockManager implements DistributedLockManager {

  private final DistributedLockProvider provider;

  public SimpleDistributedLockManager(
      DistributedLockProvider provider) {
    this.provider = provider;
  }

  @Override
  public Collection<String> getStoreNames() {
    return null;
  }

  @Override
  public boolean acquire(DistributedLock distributedLock) {
    return provider.acquire(distributedLock);
  }

  @Override
  public void hold(DistributedLock distributedLock) {
    provider.hold(distributedLock);
  }

  @Override
  public boolean release(String storeName, String lockKey, String lockValue) {
    return provider.release(storeName, lockKey, lockValue);
  }
}
