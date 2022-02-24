package com.example.demo.Curatos框架实现分布式锁;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author 温黎明
 * @version 1.0
 * @date 2022/2/20 9:39
 */
public class CuratorTest {

    public static void main(String[] args) {
        //创建分布式锁1
        InterProcessMutex interProcessMutex = new InterProcessMutex(getCuratorFramework(), "/locks");
        //创建分布式锁2
        InterProcessMutex interProcessMutex2 = new InterProcessMutex(getCuratorFramework(), "/locks");
        more(interProcessMutex,"线程1").start();
        more(interProcessMutex2,"线程2").start();
    }

    public static Thread more(InterProcessMutex i,String p){
        return new Thread(()->{
            try {
                i.acquire();
                System.out.println(p+" 获取到锁");
                i.acquire();
                System.out.println(p+" 再次获取到锁");
                Thread.sleep(5000);
                i.release();
                System.out.println(p+" 释放到锁");
                i.release();
                System.out.println(p+" 再次释放到锁");
            } catch (Exception e) {
                e.printStackTrace();
            }});
    }

    private static CuratorFramework getCuratorFramework() {
        ExponentialBackoffRetry b = new ExponentialBackoffRetry(3000, 3);
        CuratorFramework build = CuratorFrameworkFactory.builder()
                .connectString("192.168.118.137:2181,192.168.118.130:2181,192.168.118.138:2181")
                .connectionTimeoutMs(100000)
                .sessionTimeoutMs(100000)
                .retryPolicy(b).build();
        //客户端启动
        build.start();
        System.out.println("Zookeeper已启动");
        return build;
    }
}
