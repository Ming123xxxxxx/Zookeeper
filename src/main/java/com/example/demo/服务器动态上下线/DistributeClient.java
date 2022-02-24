package com.example.demo.服务器动态上下线;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 温黎明
 * @version 1.0
 * @date 2022/2/19 10:23
 */
public class DistributeClient {

    private String connectString = "192.168.118.137:2181,192.168.118.130:2181,192.168.118.138:2181";
    private int timeout=100000;
    private ZooKeeper zookeeper;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        DistributeClient distributeClient = new DistributeClient();
        //1.获取zookeeper连接
        distributeClient.getConnect();
        //2.监听/servers下的节点变化
        distributeClient.getServerList();
        //3.业务逻辑
        distributeClient.busin();
    }

    private void busin() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void getServerList() throws KeeperException, InterruptedException {
        List<String> children = zookeeper.getChildren("/servers", true);
        ArrayList<String> objects = new ArrayList<>();
        for(String c : children){
            byte[] data = zookeeper.getData("/servers/" + c, false, null);
            objects.add(new String(data));
        }
        //打印
        System.out.println(objects);

    }

    private void getConnect() throws IOException {
        zookeeper = new ZooKeeper(connectString,timeout, e->{
            try {
                getServerList();
            } catch (KeeperException keeperException) {
                keeperException.printStackTrace();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });
    }
}
