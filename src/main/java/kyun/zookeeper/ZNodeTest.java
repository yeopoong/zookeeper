package kyun.zookeeper;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZNodeTest {

    public static void main(String[] args) {
        int sessionTimeout = 10 * 100;

        // 1. ZooKeeper 서버(클러스터)에 연결
        ZooKeeper zk = null;

        try {
            zk = new ZooKeeper("127.0.0.1:2181", sessionTimeout, null);

            // 2. Test01 znode가 존재하지 않으면 /test01, /test02 생성
            if (zk.exists("/test01", false) == null) {
                zk.create("/test01", "test01_data".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                zk.create("/test02", "test02_data".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            // 3 /test01 노드의 자식 노드로 sub01, sub02 생성
            zk.create("/test01/sub01", null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            zk.create("/test01/sub02", null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

            // 4. /test01 노드의 데이터 가져오기
            byte[] test01Data = zk.getData("/test01", false, null);
            System.out.println("getData [/test01]: " + new String(test01Data));

            // 5. /test01/sub01 노드의 데이터를 새로운 값으로 설정
            zk.setData("/test01/sub01", "this new Data".getBytes(), -1);
            byte[] subData = zk.getData("/test01/sub01", false, null);
            System.out.println("getData after setData [/test01/sub01]: " + new String(subData));

            // 6. 노드가 존재하는지 확인
            System.out.println("exist [/test01/sub01]: " + (zk.exists("/test01/sub01", false) != null));
            System.out.println("exist [/test01/sub03]: " + (zk.exists("/test01/sub03", false) != null));

            // 7. /test01의 자식노드 목록 가져오기
            List<String> children = zk.getChildren("/test01", false);
            for (String eachChildren : children) {
                System.out.println("getChildren [/test01]: " + eachChildren);
            }
        } catch (IOException | KeeperException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 8. zk 죵료
            try {
                zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
