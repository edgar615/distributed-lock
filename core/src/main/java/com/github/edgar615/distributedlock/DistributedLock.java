package com.github.edgar615.distributedlock;

/**
 * 分布式锁的定义，将锁和锁的实现分离
 */
public interface DistributedLock  {

  /**
   * 锁的存储空间，对于redis来说，这个就是redis key的前缀，对于数据库来说这个值是数据库表名
   * @return
   */
  String storeName();

  /**
   * 锁的资源。例如锁定某个订单:order:#orderno，锁定整个任务表job
   * 支持spring El表达式
   */
  String lockKey();

  /**
   * 锁的客户端标识。
   *  支持spring El表达式
   */
  String lockValue();

  /**
   * 过期时间,单位毫秒
   */
  long expireMills();

  /**
   * 重试的间隔时间，单位毫秒，通过重试间隔和重试次数可以实现锁的等待超时
   */
  long retryIntervalMills();

  /**
   * 重试次数，默认不重试
   */
  int retryTimes();

}
