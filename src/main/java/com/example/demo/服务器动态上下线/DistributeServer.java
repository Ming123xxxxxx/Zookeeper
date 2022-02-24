package com.example.demo.服务器动态上下线;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author 温黎明
 * @version 1.0
 * @date 2022/2/19 10:07
 */
public class DistributeServer {

    private String connectString = "192.168.118.137:2181,192.168.118.130:2181,192.168.118.138:2181";
    private int timeout=100000;
    private ZooKeeper zookeeper;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        //1.获取zookeeper连接
        DistributeServer distributeServer = new DistributeServer();
        distributeServer.getConnecy();
        //2.注册服务器到zookeeper集群中
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        distributeServer.regist(s);
        //3.启动业务逻辑(延时)
        distributeServer.busin();
    }

    private void busin() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }


    private void regist(String urls) throws KeeperException, InterruptedException {
        String path="/servers/"+urls;
        zookeeper.create(path,urls.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(urls+" is online");
    }

    private void getConnecy() throws IOException {
        zookeeper = new ZooKeeper(connectString,timeout, e->{});
    }
}
