package com.example.demo.分布式锁;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author 温黎明
 * @version 1.0
 * @date 2022/2/19 11:17
 */
public class DistributedLock {

    private String connectString = "192.168.118.137:2181,192.168.118.130:2181,192.168.118.138:2181";
    private int sessionTimeout=100000;
    private ZooKeeper zooKeeper;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private CountDownLatch load = new CountDownLatch(1);
    private String thelast;
    private String s;

    public DistributedLock() throws IOException, KeeperException, InterruptedException {
        //获取连接
        zooKeeper = new ZooKeeper(connectString,sessionTimeout,e->{
            //countDownLatch如果连接上zk，可以释放
            if(e.getState()== Watcher.Event.KeeperState.SyncConnected){
                countDownLatch.countDown();
            }
            //load需要释放
            if(e.getType()== Watcher.Event.EventType.NodeDeleted &&e.getPath().equals(thelast)){
                countDownLatch.countDown();
            }

        });
        //等待zookeeper正常连接后,往下走程序
        countDownLatch.await();
        //判断根节点/locks是否存在
        Stat exists = zooKeeper.exists("/locks", false);
        if (exists == null) {
            zooKeeper.create("/locks", "locks".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    //对zookeeper进行加锁
    public void zklock() throws KeeperException, InterruptedException {
        //创建对应的临时带序号节点
        s = zooKeeper.create("/locks/" + "seq-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        //判断创建的节点是否是最小的序号节点,如果是获取到锁,如果不是则监听此节点的前一个节点
        List<String> children = zooKeeper.getChildren("/locks", false);
        //如何"/locks"下只有一个节点,则直接获取锁,如果有多个节点,则需要判断谁最小
        if(children.size()==1){
            return;
        }else{
            Collections.sort(children);
            //获取对应的节点名称
            String substring = s.substring("/locks/".length());
            //通过substring获取到该节点在children集合中的位置
            int i = children.indexOf(substring);
            //判断
            if(i==-1){
                System.out.println("数据异常");
            }else if(i==0){
                //说明只有一个节点可以获取
                return;
            }else{
                //上一个节点
                thelast="/locks/"+children.get(i-1);
                //需要监听此节点的前一个节点
                zooKeeper.getData(thelast,true,null);
                //等待监听
                load.await();
                return;
            }
        }
    }

    //解锁
    public void unzklock() throws KeeperException, InterruptedException {
        //删除节点
        zooKeeper.delete(s,-1);

    }

}
