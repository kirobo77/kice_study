import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Cloud/On-Premise 환경에서 요구되는 핵심 알고리즘 구현
 * 
 * 분산 캐싱 알고리즘 (Consistent Hashing)
 * 해시 공간 (Hash Space)
 *   일반적으로 0 ~ 2³²-1의 정수 범위를 사용하는 원형 공간.
 * 해시 링 (Hash Ring)
 *   해시 공간을 원형 구조로 연결한 것. 맨 끝과 처음이 연결되어 순환 구조를 이룸.
 * 키-서버 매핑
 *   키와 서버 모두 해시 함수를 통해 링에 위치시킨다.
 *   키는 자신보다 해시값이 큰 첫 번째 서버에 매핑된다 (시계 방향 탐색).
 * 
 */
public class ConsistentHashingSample2 {
    
    static class AdvancedConsistentHashRing {
        private TreeMap<Long, String> ring;
        private Set<String> nodes;
        private int virtualNodeCount;
        private MessageDigest md5;
        
        public AdvancedConsistentHashRing(int virtualNodeCount) {
            this.ring = new TreeMap<>();
            this.nodes = new HashSet<>();
            this.virtualNodeCount = virtualNodeCount;
            try {
                this.md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("MD5 알고리즘을 찾을 수 없습니다", e);
            }
        }
        
        /**
         * 노드 추가 - 다양한 키 전략 사용
         */
        public void addNode(String node) {
            nodes.add(node);
            
            for (int i = 0; i < virtualNodeCount; i++) {
                String virtualNode1 = node + "#vnode#" + i;
                long hash1 = calculateMD5Hash(virtualNode1);
                ring.put(hash1, node);
            }
        }
        
        /**
         * 노드 제거
         */
        public void removeNode(String node) {
            nodes.remove(node);
            
            // 해당 노드의 모든 가상 노드 제거
            Iterator<Map.Entry<Long, String>> iterator = ring.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, String> entry = iterator.next();
                if (entry.getValue().equals(node)) {
                    iterator.remove();
                }
            }
        }
        
        /**
         * 키에 대한 노드 찾기
         */
        public String getNode(String key) {
            if (ring.isEmpty()) return null;
            
            long hash = calculateMD5Hash(key);
            Map.Entry<Long, String> entry = ring.ceilingEntry(hash);
            
            if (entry == null) {
                entry = ring.firstEntry();
            }
            
            return entry.getValue();
        }
        
        /**
         * MD5 기반 해시 함수 (더 균등한 분산)
         */
        private long calculateMD5Hash(String input) {
            md5.reset();
            md5.update(input.getBytes());
            byte[] digest = md5.digest();
            
            long hash = 0;
            for (int i = 0; i < 8; i++) {
                hash <<= 8;
                hash |= ((int) digest[i]) & 0xFF;
            }
            
            return hash;
        }
    }
    
    /**
     * 일관된 해싱 테스트
     */
    public static void testConsistentHashing() {
        System.out.println("========== Consistent Hashing Test ==========");
        
        AdvancedConsistentHashRing ring = new AdvancedConsistentHashRing(3);
        
        // 노드 추가
        ring.addNode("node1");
        ring.addNode("node2");
        ring.addNode("node3");
        
        // 키 분배 테스트
        String[] keys = {"user1", "user2", "user3", "user4", "user5", "data1", "data2", "data3"};
        
        System.out.println("--- Key Distribution ---");
        for (String key : keys) {
            String node = ring.getNode(key);
            System.out.println("Key: " + key + " -> Node: " + node);
        }
        
        // 노드 제거 후 테스트
        System.out.println("--- After removing node2 ---");
        ring.removeNode("node2");
        for (String key : keys) {
            String node = ring.getNode(key);
            System.out.println("Key: " + key + " -> Node: " + node);
        }
        
    }
    
    /**
     * 메인 메소드 - 테스트 실행
     */
    public static void main(String[] args) {
        System.out.println("일관된 해싱 테스트 테스트 시작");
        System.out.println("=".repeat(40));
        
        try {
            // 일관된 해싱 테스트
            testConsistentHashing();
            
        } catch (Exception e) {
            System.err.println("테스트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("일관된 해싱 테스트 테스트 완료");
    }   
}