import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cloud/On-Premise 환경에서 요구되는 핵심 알고리즘 구현
 * 
 * 데이터 파티셔닝 알고리즘 (Hash-based Partitioning)
 */
public class DataPartitioningSample {

    // ===== 데이터 파티셔닝 알고리즘 =====
    
    /**
     * 해시 기반 파티셔닝 클래스
     */
    static class HashPartitioner {
        private int partitionCount;
        private List<String> partitions;
        
        public HashPartitioner(int partitionCount) {
            this.partitionCount = partitionCount;
            this.partitions = new ArrayList<>();
            for (int i = 0; i < partitionCount; i++) {
                partitions.add("partition_" + i);
            }
        }
        
        /**
         * 키를 기반으로 파티션을 결정하는 메소드
         */
        public String getPartition(String key) {
            int hash = Math.abs(key.hashCode());
            int partitionIndex = hash % partitionCount;
            return partitions.get(partitionIndex);
        }
        
        /**
         * 데이터를 파티션별로 분배하는 메소드
         */
        public Map<String, List<String>> distributeData(List<String> data) {
            Map<String, List<String>> partitionedData = new HashMap<>();
            
            // 파티션 초기화
            for (String partition : partitions) {
                partitionedData.put(partition, new ArrayList<>());
            }
            
            // 데이터를 각 파티션에 분배
            for (String item : data) {
                String partition = getPartition(item);
                partitionedData.get(partition).add(item);
            }
            
            return partitionedData;
        }
        
        /**
         * 파티션 통계를 출력하는 메소드
         */
        public void printPartitionStats(Map<String, List<String>> partitionedData) {
            System.out.println("=== Partition Statistics ===");
            for (Map.Entry<String, List<String>> entry : partitionedData.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue().size() + " items");
            }
        }
    }
    
    /**
     * 데이터 파티셔닝 테스트
     */
    public static void testDataPartitioning() {
        System.out.println("========== Data Partitioning Test ==========");
        
        HashPartitioner partitioner = new HashPartitioner(4);
        
        // 테스트 데이터 생성
        List<String> data = Arrays.asList(
            "user1", "user2", "user3", "user4", "user5",
            "order1", "order2", "order3", "order4", "order5",
            "product1", "product2", "product3", "product4", "product5"
        );
        
        // 데이터 분배
        Map<String, List<String>> partitionedData = partitioner.distributeData(data);
        
        // 결과 출력
        partitioner.printPartitionStats(partitionedData);
        
        System.out.println("--- Detailed Distribution ---");
        for (Map.Entry<String, List<String>> entry : partitionedData.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        System.out.println();
    }    
    
    /**
     * 메인 메소드 - 테스트 실행
     */
    public static void main(String[] args) {
        System.out.println("Cloud/On-Premise 데이터 파티셔닝 테스트 시작");
        System.out.println("=".repeat(40));
        
        try {
            // 데이터 파티셔닝 테스트
            testDataPartitioning();
        } catch (Exception e) {
            System.err.println("테스트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("데이터 파티셔닝 테스트 완료");
    }    
    
}
