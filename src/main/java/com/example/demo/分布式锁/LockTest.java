package com.example.demo.分布式锁;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * @author 温黎明
 * @version 1.0
 * @date 2022/2/19 12:13
 */
public class LockTest {

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        final DistributedLock distributedLock = new DistributedLock();
        final  DistributedLock distributedLock2 = new DistributedLock();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    distributedLock.zklock();
                    System.out.println("线程1启动,获取到锁");
                    Thread.sleep(2000);
                    distributedLock.unzklock();
                    System.out.println("线程1启动,释放锁");
                } catch (InterruptedException | KeeperException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    distributedLock2.zklock();
                    System.out.println("线程2启动,获取到锁");
                    Thread.sleep(2000);
                    distributedLock2.unzklock();
                    System.out.println("线程2启动,释放锁");
                } catch (InterruptedException | KeeperException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
