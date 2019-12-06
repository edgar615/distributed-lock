package com.github.edgar615.distributedlock.spring;

public interface OrderService {

  boolean pay(String orderNo, int sleepSecond);
}
