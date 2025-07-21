import java.util.ArrayList;
import java.util.List;

/**
 * Cloud/On-Premise 환경에서 요구되는 핵심 알고리즘 구현
 * 
 *  로드 밸런싱 알고리즘 (라운드 로빈, 가중치 라운드 로빈)
 */
public class LoadBalancerSample {

	// ===== 로드 밸런싱 알고리즘 =====
	
    /**
     * 서버 정보를 담는 클래스
     */
    static class Server {
        private String id;
        private String ip;
        private int port;
        private int weight;
        private boolean isHealthy;
        private int currentConnections;
        
        public Server(String id, String ip, int port, int weight) {
            this.id = id;
            this.ip = ip;
            this.port = port;
            this.weight = weight;
            this.isHealthy = true;
            this.currentConnections = 0;
        }
        
        // Getter 메소드들
        public String getId() { return id; }
        public String getIp() { return ip; }
        public int getPort() { return port; }
        public int getWeight() { return weight; }
        public boolean isHealthy() { return isHealthy; }
        public int getCurrentConnections() { return currentConnections; }
        
        // Setter 메소드들
        public void setHealthy(boolean healthy) { this.isHealthy = healthy; }
        public void incrementConnections() { this.currentConnections++; }
        public void decrementConnections() { this.currentConnections--; }
        
        @Override
        public String toString() {
            return String.format("Server[%s:%s:%d, weight=%d, healthy=%b, connections=%d]", 
                               id, ip, port, weight, isHealthy, currentConnections);
        }
    }
    
    /**
     * 로드 밸런서 클래스
     * 라운드 로빈과 가중치 라운드 로빈 알고리즘을 구현
     */
    static class LoadBalancer {
        private List<Server> servers;
        private int currentIndex;
        private int currentWeight;
        private int maxWeight;
        private int gcd; // 최대공약수
        
        public LoadBalancer() {
            this.servers = new ArrayList<>();
            this.currentIndex = 0;
            this.currentWeight = 0;
        }
        
        /**
         * 서버를 추가하는 메소드
         */
        public void addServer(Server server) {
            servers.add(server);
            calculateMaxWeightAndGCD();
        }
        
        /**
         * 최대 가중치와 최대공약수를 계산하는 메소드
         */
        private void calculateMaxWeightAndGCD() {
            if (servers.isEmpty()) return;
            
            maxWeight = servers.get(0).getWeight();
            gcd = servers.get(0).getWeight();
            
            for (int i = 1; i < servers.size(); i++) {
                int weight = servers.get(i).getWeight();
                maxWeight = Math.max(maxWeight, weight);
                gcd = calculateGCD(gcd, weight);
            }
        }
        
        /**
         * 최대공약수를 계산하는 헬퍼 메소드
         */
        private int calculateGCD(int a, int b) {
            while (b != 0) {
                int temp = b;
                b = a % b;
                a = temp;
            }
            return a;
        }
        
        /**
         * 라운드 로빈 알고리즘으로 서버를 선택
         */
        public Server selectServerRoundRobin() {
            if (servers.isEmpty()) return null;
            
            int attempts = 0;
            while (attempts < servers.size()) {
                Server server = servers.get(currentIndex);
                currentIndex = (currentIndex + 1) % servers.size();
                
                if (server.isHealthy()) {
                    return server;
                }
                attempts++;
            }
            return null; // 모든 서버가 비정상
        }
        
        /**
         * 가중치 라운드 로빈 알고리즘으로 서버를 선택
         */
        public Server selectServerWeightedRoundRobin() {
            if (servers.isEmpty()) return null;
            
            while (true) {
                currentIndex = (currentIndex + 1) % servers.size();
                
                if (currentIndex == 0) {
                    currentWeight = currentWeight - gcd;
                    if (currentWeight <= 0) {
                        currentWeight = maxWeight;
                    }
                }
                
                Server server = servers.get(currentIndex);
                if (server.getWeight() >= currentWeight && server.isHealthy()) {
                    return server;
                }
            }
        }
    }
    
    /**
     * 로드 밸런서 테스트
     */
    public static void testLoadBalancer() {
        System.out.println("========== Load Balancer Test ==========");
        
        LoadBalancer lb = new LoadBalancer();
        
        // 서버 추가
        lb.addServer(new Server("server1", "192.168.1.10", 8080, 1));
        lb.addServer(new Server("server2", "192.168.1.11", 8080, 2));
        lb.addServer(new Server("server3", "192.168.1.12", 8080, 3));
        
        // 라운드 로빈 테스트
        System.out.println("--- Round Robin Test ---");
        for (int i = 0; i < 6; i++) {
            Server server = lb.selectServerRoundRobin();
            System.out.println("Selected: " + server.getId());
        }
        
        // 가중치 라운드 로빈 테스트
        System.out.println("--- Weighted Round Robin Test ---");
        for (int i = 0; i < 12; i++) {
            Server server = lb.selectServerWeightedRoundRobin();
            System.out.println("Selected: " + server.getId());
        }
        
        System.out.println();
    }
    
    /**
     * 메인 메소드 - 테스트 실행
     */
    public static void main(String[] args) {
        System.out.println("로드 밸런서 테스트 시작");
        System.out.println("=".repeat(40));
        
        try {
            // 로드 밸런서 테스트
            testLoadBalancer();
        } catch (Exception e) {
            System.err.println("테스트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("로드 밸런서 테스트 완료");
    }    
}
