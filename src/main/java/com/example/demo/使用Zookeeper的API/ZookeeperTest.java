package com.example.demo.使用Zookeeper的API;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author 温黎明
 * @version 1.0
 * @date 2022/2/18 10:14
 */
public class ZookeeperTest {

    //注意:字符串中逗号左右不能有空格
    //connectString:所需要连接到的服务端的地址及其zookeeper的端口号
    private String connectString = "192.168.118.137:2181,192.168.118.130:2181,192.168.118.138:2181";
    //sessionTimeout:为超时时间,单位为毫秒
    private int sessionTimeout=100000;
    private ZooKeeper zooKeeper;

    public ZooKeeper zinit() throws IOException {
         return zooKeeper = new ZooKeeper(connectString, sessionTimeout,e ->{});
    }

    //查看节点是否存在
    @Test
    public void exist() throws IOException, KeeperException, InterruptedException {
        Stat exists = zinit().exists("/shanghai", false);
        System.out.println(exists==null?"No exist":"exist");
    }

//    @Before
    @Test
    public void zd() throws IOException {
        zooKeeper = new ZooKeeper(connectString,sessionTimeout,e ->{
            List<String> children = null;
            try {
                children = zooKeeper.getChildren("/", true);
                System.out.println("------------------");
                for (String c:children){
                    System.out.println(c);
                }
                System.out.println("------------------");
            } catch (KeeperException keeperException) {
                keeperException.printStackTrace();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });
    }

    //监听节点变化
    @Test
    public  void watchCNode() throws IOException, KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren("/", true);
        for (String c:children){
            System.out.println(c);
        }

        //延时
        Thread.sleep(Long.MAX_VALUE);
    }

    //创建子节点
    @Test
    public void createCNode() throws KeeperException, InterruptedException, IOException {
       zinit().create("/jiangxi/jiujiang","庐山".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

}
