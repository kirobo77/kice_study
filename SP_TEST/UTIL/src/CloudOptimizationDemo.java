import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;
import java.security.*;

/**
 * Cloud/On-Premise 환경에서 요구되는 핵심 알고리즘 구현
 * 
 * 포함된 알고리즘:
 * 1. 로드 밸런싱 알고리즘 (라운드 로빈, 가중치 라운드 로빈)
 * 2. 분산 캐싱 알고리즘 (Consistent Hashing)
 * 3. 장애 감지 알고리즘 (Heartbeat with Circuit Breaker)
 * 4. 데이터 파티셔닝 알고리즘 (Hash-based Partitioning)
 * 5. 메시지 큐 알고리즘 (Producer-Consumer Pattern)
 * 6. 리소스 모니터링 알고리즘 (Moving Average)
 */
public class CloudOptimizationDemo {
    
    // ===== 1. 로드 밸런싱 알고리즘 =====
    
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
    
    // ===== 2. 분산 캐싱 알고리즘 (Consistent Hashing) =====
    
    /**
     * 일관된 해싱을 구현하는 클래스
     */
    static class ConsistentHashRing {
        private TreeMap<Integer, String> ring;
        private Set<String> nodes;
        private int virtualNodeCount;
        
        public ConsistentHashRing(int virtualNodeCount) {
            this.ring = new TreeMap<>();
            this.nodes = new HashSet<>();
            this.virtualNodeCount = virtualNodeCount;
        }
        
        /**
         * 노드를 링에 추가하는 메소드
         */
        public void addNode(String node) {
            nodes.add(node);
            for (int i = 0; i < virtualNodeCount; i++) {
                String virtualNode = node + "#" + i;
                int hash = calculateHash(virtualNode);
                ring.put(hash, node);
            }
        }
        
        /**
         * 노드를 링에서 제거하는 메소드
         */
        public void removeNode(String node) {
            nodes.remove(node);
            for (int i = 0; i < virtualNodeCount; i++) {
                String virtualNode = node + "#" + i;
                int hash = calculateHash(virtualNode);
                ring.remove(hash);
            }
        }
        
        /**
         * 키에 대한 노드를 찾는 메소드
         */
        public String getNode(String key) {
            if (ring.isEmpty()) return null;
            
            int hash = calculateHash(key);
            
            // 해시보다 큰 첫 번째 노드를 찾음
            Map.Entry<Integer, String> entry = ring.ceilingEntry(hash);
            
            // 찾지 못하면 링의 첫 번째 노드를 반환 (순환 구조)
            if (entry == null) {
                entry = ring.firstEntry();
            }
            
            return entry.getValue();
        }
        
        /**
         * 해시 함수 (간단한 해시 구현)
         */
        private int calculateHash(String input) {
            return input.hashCode();
        }
        
        /**
         * 현재 링 상태를 출력하는 메소드
         */
        public void printRing() {
            System.out.println("=== Consistent Hash Ring ===");
            for (Map.Entry<Integer, String> entry : ring.entrySet()) {
                System.out.println("Hash: " + entry.getKey() + " -> Node: " + entry.getValue());
            }
        }
    }
    
    // ===== 3. 장애 감지 알고리즘 (Heartbeat with Circuit Breaker) =====
    
    /**
     * 서킷 브레이커 패턴을 구현하는 클래스
     */
    static class CircuitBreaker {
        private enum State {
            CLOSED, OPEN, HALF_OPEN
        }
        
        private State state;
        private int failureCount;
        private int failureThreshold;
        private long timeout;
        private long lastFailureTime;
        private int successCount;
        private int halfOpenMaxCalls;
        
        public CircuitBreaker(int failureThreshold, long timeout, int halfOpenMaxCalls) {
            this.state = State.CLOSED;
            this.failureCount = 0;
            this.failureThreshold = failureThreshold;
            this.timeout = timeout;
            this.lastFailureTime = 0;
            this.successCount = 0;
            this.halfOpenMaxCalls = halfOpenMaxCalls;
        }
        
        /**
         * 서비스 호출을 실행하는 메소드
         */
        public boolean call(Callable<Boolean> service) {
            if (state == State.OPEN) {
                // 타임아웃 시간이 지나면 HALF_OPEN으로 전환
                if (System.currentTimeMillis() - lastFailureTime > timeout) {
                    state = State.HALF_OPEN;
                    successCount = 0;
                    System.out.println("Circuit Breaker: OPEN -> HALF_OPEN");
                } else {
                    System.out.println("Circuit Breaker: Call blocked (OPEN state)");
                    return false;
                }
            }
            
            try {
                boolean result = service.call();
                onSuccess();
                return result;
            } catch (Exception e) {
                onFailure();
                return false;
            }
        }
        
        /**
         * 성공 시 호출되는 메소드
         */
        private void onSuccess() {
            failureCount = 0;
            
            if (state == State.HALF_OPEN) {
                successCount++;
                if (successCount >= halfOpenMaxCalls) {
                    state = State.CLOSED;
                    System.out.println("Circuit Breaker: HALF_OPEN -> CLOSED");
                }
            }
        }
        
        /**
         * 실패 시 호출되는 메소드
         */
        private void onFailure() {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();
            
            if (failureCount >= failureThreshold) {
                state = State.OPEN;
                System.out.println("Circuit Breaker: " + (state == State.HALF_OPEN ? "HALF_OPEN" : "CLOSED") + " -> OPEN");
            }
        }
        
        public State getState() {
            return state;
        }
    }
    
    /**
     * 하트비트 모니터링 클래스
     */
    static class HeartbeatMonitor {
        private Map<String, Long> lastHeartbeat;
        private Map<String, Boolean> nodeStatus;
        private long heartbeatInterval;
        private Timer timer;
        
        public HeartbeatMonitor(long heartbeatInterval) {
            this.lastHeartbeat = new ConcurrentHashMap<>();
            this.nodeStatus = new ConcurrentHashMap<>();
            this.heartbeatInterval = heartbeatInterval;
            this.timer = new Timer(true);
        }
        
        /**
         * 노드를 모니터링에 추가하는 메소드
         */
        public void addNode(String nodeId) {
            lastHeartbeat.put(nodeId, System.currentTimeMillis());
            nodeStatus.put(nodeId, true);
        }
        
        /**
         * 하트비트를 업데이트하는 메소드
         */
        public void updateHeartbeat(String nodeId) {
            lastHeartbeat.put(nodeId, System.currentTimeMillis());
            nodeStatus.put(nodeId, true);
        }
        
        /**
         * 주기적으로 하트비트를 체크하는 메소드
         */
        public void startMonitoring() {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    checkHeartbeats();
                }
            }, heartbeatInterval, heartbeatInterval);
        }
        
        /**
         * 하트비트를 체크하는 메소드
         */
        private void checkHeartbeats() {
            long currentTime = System.currentTimeMillis();
            
            for (Map.Entry<String, Long> entry : lastHeartbeat.entrySet()) {
                String nodeId = entry.getKey();
                long lastTime = entry.getValue();
                
                if (currentTime - lastTime > heartbeatInterval * 2) {
                    if (nodeStatus.get(nodeId)) {
                        nodeStatus.put(nodeId, false);
                        System.out.println("Node " + nodeId + " is DOWN");
                    }
                } else {
                    if (!nodeStatus.get(nodeId)) {
                        nodeStatus.put(nodeId, true);
                        System.out.println("Node " + nodeId + " is UP");
                    }
                }
            }
        }
        
        public boolean isNodeHealthy(String nodeId) {
            return nodeStatus.getOrDefault(nodeId, false);
        }
        
        public void stopMonitoring() {
            timer.cancel();
        }
    }
    
    // ===== 4. 데이터 파티셔닝 알고리즘 =====
    
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
    
    // ===== 5. 메시지 큐 알고리즘 =====
    
    /**
     * 간단한 메시지 큐 구현
     */
    static class MessageQueue<T> {
        private Queue<T> queue;
        private int maxSize;
        private final Object lock = new Object();
        
        public MessageQueue(int maxSize) {
            this.queue = new LinkedList<>();
            this.maxSize = maxSize;
        }
        
        /**
         * 메시지를 큐에 추가하는 메소드 (Producer)
         */
        public boolean produce(T message) {
            synchronized (lock) {
                if (queue.size() >= maxSize) {
                    return false; // 큐가 가득 참
                }
                queue.offer(message);
                lock.notifyAll(); // 대기 중인 컨슈머에게 알림
                return true;
            }
        }
        
        /**
         * 메시지를 큐에서 가져오는 메소드 (Consumer)
         */
        public T consume() throws InterruptedException {
            synchronized (lock) {
                while (queue.isEmpty()) {
                    lock.wait(); // 메시지가 올 때까지 대기
                }
                return queue.poll();
            }
        }
        
        /**
         * 큐의 현재 크기를 반환하는 메소드
         */
        public int size() {
            synchronized (lock) {
                return queue.size();
            }
        }
    }
    
    /**
     * 메시지 프로듀서 클래스
     */
    static class MessageProducer implements Runnable {
        private MessageQueue<String> queue;
        private String producerId;
        private int messageCount;
        
        public MessageProducer(MessageQueue<String> queue, String producerId, int messageCount) {
            this.queue = queue;
            this.producerId = producerId;
            this.messageCount = messageCount;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < messageCount; i++) {
                String message = producerId + "_message_" + i;
                boolean success = queue.produce(message);
                if (success) {
                    System.out.println(producerId + " produced: " + message);
                } else {
                    System.out.println(producerId + " failed to produce: " + message + " (queue full)");
                }
                
                try {
                    Thread.sleep(100); // 100ms 간격으로 메시지 생성
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    /**
     * 메시지 컨슈머 클래스
     */
    static class MessageConsumer implements Runnable {
        private MessageQueue<String> queue;
        private String consumerId;
        private boolean running;
        
        public MessageConsumer(MessageQueue<String> queue, String consumerId) {
            this.queue = queue;
            this.consumerId = consumerId;
            this.running = true;
        }
        
        @Override
        public void run() {
            while (running) {
                try {
                    String message = queue.consume();
                    System.out.println(consumerId + " consumed: " + message);
                    Thread.sleep(150); // 150ms 간격으로 메시지 처리
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        public void stop() {
            running = false;
        }
    }
    
    // ===== 6. 리소스 모니터링 알고리즘 =====
    
    /**
     * 이동 평균을 계산하는 클래스
     */
    static class MovingAverageCalculator {
        private Queue<Double> values;
        private int windowSize;
        private double sum;
        
        public MovingAverageCalculator(int windowSize) {
            this.values = new LinkedList<>();
            this.windowSize = windowSize;
            this.sum = 0.0;
        }
        
        /**
         * 새로운 값을 추가하고 이동 평균을 계산하는 메소드
         */
        public double addValue(double value) {
            values.offer(value);
            sum += value;
            
            // 윈도우 크기를 초과하면 가장 오래된 값을 제거
            if (values.size() > windowSize) {
                double oldValue = values.poll();
                sum -= oldValue;
            }
            
            return getAverage();
        }
        
        /**
         * 현재 이동 평균을 반환하는 메소드
         */
        public double getAverage() {
            return values.isEmpty() ? 0.0 : sum / values.size();
        }
        
        /**
         * 현재 값의 개수를 반환하는 메소드
         */
        public int getCount() {
            return values.size();
        }
    }
    
    /**
     * 리소스 모니터링 클래스
     */
    static class ResourceMonitor {
        private Map<String, MovingAverageCalculator> metrics;
        private Map<String, Double> thresholds;
        private Timer timer;
        
        public ResourceMonitor() {
            this.metrics = new HashMap<>();
            this.thresholds = new HashMap<>();
            this.timer = new Timer(true);
        }
        
        /**
         * 모니터링할 메트릭을 추가하는 메소드
         */
        public void addMetric(String metricName, int windowSize, double threshold) {
            metrics.put(metricName, new MovingAverageCalculator(windowSize));
            thresholds.put(metricName, threshold);
        }
        
        /**
         * 메트릭 값을 업데이트하는 메소드
         */
        public void updateMetric(String metricName, double value) {
            MovingAverageCalculator calculator = metrics.get(metricName);
            if (calculator != null) {
                double average = calculator.addValue(value);
                checkThreshold(metricName, average);
            }
        }
        
        /**
         * 임계값을 확인하는 메소드
         */
        private void checkThreshold(String metricName, double average) {
            Double threshold = thresholds.get(metricName);
            if (threshold != null && average > threshold) {
                System.out.println("ALERT: " + metricName + " average (" + 
                                 String.format("%.2f", average) + ") exceeds threshold (" + threshold + ")");
            }
        }
        
        /**
         * 모든 메트릭의 현재 상태를 출력하는 메소드
         */
        public void printMetrics() {
            System.out.println("=== Resource Metrics ===");
            for (Map.Entry<String, MovingAverageCalculator> entry : metrics.entrySet()) {
                String metricName = entry.getKey();
                MovingAverageCalculator calculator = entry.getValue();
                System.out.println(metricName + ": " + String.format("%.2f", calculator.getAverage()) + 
                                 " (count: " + calculator.getCount() + ")");
            }
        }
        
        /**
         * 주기적으로 랜덤 메트릭을 생성하는 메소드 (테스트용)
         */
        public void startRandomMetricGeneration() {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // CPU 사용률 시뮬레이션 (0-100%)
                    double cpuUsage = Math.random() * 100;
                    updateMetric("cpu_usage", cpuUsage);
                    
                    // 메모리 사용률 시뮬레이션 (0-100%)
                    double memoryUsage = Math.random() * 100;
                    updateMetric("memory_usage", memoryUsage);
                    
                    // 응답 시간 시뮬레이션 (0-1000ms)
                    double responseTime = Math.random() * 1000;
                    updateMetric("response_time", responseTime);
                }
            }, 0, 1000);
        }
        
        public void stopMonitoring() {
            timer.cancel();
        }
    }
    
    // ===== 테스트 메소드들 =====
    
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
     * 일관된 해싱 테스트
     */
    public static void testConsistentHashing() {
        System.out.println("========== Consistent Hashing Test ==========");
        
        ConsistentHashRing ring = new ConsistentHashRing(3);
        
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
        
        System.out.println();
    }
    
    /**
     * 서킷 브레이커 테스트
     */
    public static void testCircuitBreaker() {
        System.out.println("========== Circuit Breaker Test ==========");
        
        CircuitBreaker cb = new CircuitBreaker(3, 5000, 2);
        
        // 실패하는 서비스 시뮬레이션
        Callable<Boolean> failingService = new Callable<Boolean>() {
            private int callCount = 0;
            
            @Override
            public Boolean call() throws Exception {
                callCount++;
                if (callCount <= 4) {
                    throw new RuntimeException("Service failure");
                }
                return true; // 5번째 호출부터는 성공
            }
        };
        
        // 서킷 브레이커 테스트
        for (int i = 0; i < 10; i++) {
            System.out.println("Call " + (i + 1) + ": " + cb.call(failingService) + " (State: " + cb.getState() + ")");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println();
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
     * 메시지 큐 테스트
     */
    public static void testMessageQueue() throws InterruptedException {
        System.out.println("========== Message Queue Test ==========");
        
        MessageQueue<String> queue = new MessageQueue<>(10);
        
        // 프로듀서와 컨슈머 스레드 생성
        Thread producer1 = new Thread(new MessageProducer(queue, "Producer1", 5));
        Thread producer2 = new Thread(new MessageProducer(queue, "Producer2", 5));
        
        MessageConsumer consumer1 = new MessageConsumer(queue, "Consumer1");
        MessageConsumer consumer2 = new MessageConsumer(queue, "Consumer2");
        Thread consumerThread1 = new Thread(consumer1);
        Thread consumerThread2 = new Thread(consumer2);
        
        // 스레드 시작
        consumerThread1.start();
        consumerThread2.start();
        producer1.start();
        producer2.start();
        
        // 프로듀서 완료 대기
        producer1.join();
        producer2.join();
        
        // 잠시 대기 후 컨슈머 중지
        Thread.sleep(2000);
        consumer1.stop();
        consumer2.stop();
        
        consumerThread1.interrupt();
        consumerThread2.interrupt();
        
        System.out.println("Final queue size: " + queue.size());
        System.out.println();
    }
    
    /**
     * 리소스 모니터링 테스트
     */
    public static void testResourceMonitoring() throws InterruptedException {
        System.out.println("========== Resource Monitoring Test ==========");
        
        ResourceMonitor monitor = new ResourceMonitor();
        
        // 메트릭 추가 (메트릭명, 윈도우 크기, 임계값)
        monitor.addMetric("cpu_usage", 5, 80.0);
        monitor.addMetric("memory_usage", 5, 85.0);
        monitor.addMetric("response_time", 5, 500.0);
        
        // 수동으로 몇 개의 메트릭 값 추가
        System.out.println("--- Manual Metric Updates ---");
        monitor.updateMetric("cpu_usage", 45.0);
        monitor.updateMetric("cpu_usage", 55.0);
        monitor.updateMetric("cpu_usage", 85.0); // 임계값 초과
        monitor.updateMetric("cpu_usage", 90.0); // 임계값 초과
        
        monitor.updateMetric("memory_usage", 70.0);
        monitor.updateMetric("memory_usage", 75.0);
        monitor.updateMetric("memory_usage", 80.0);
        
        monitor.updateMetric("response_time", 200.0);
        monitor.updateMetric("response_time", 300.0);
        monitor.updateMetric("response_time", 600.0); // 임계값 초과
        
        monitor.printMetrics();
        
        // 자동 메트릭 생성 시작
        System.out.println("--- Automatic Metric Generation (5 seconds) ---");
        monitor.startRandomMetricGeneration();
        
        // 5초 동안 실행
        Thread.sleep(5000);
        
        monitor.stopMonitoring();
        monitor.printMetrics();
        
        System.out.println();
    }
    
    /**
     * 하트비트 모니터링 테스트
     */
    public static void testHeartbeatMonitoring() throws InterruptedException {
        System.out.println("========== Heartbeat Monitoring Test ==========");
        
        HeartbeatMonitor monitor = new HeartbeatMonitor(2000); // 2초 간격
        
        // 노드 추가
        monitor.addNode("node1");
        monitor.addNode("node2");
        monitor.addNode("node3");
        
        // 모니터링 시작
        monitor.startMonitoring();
        
        // 처음에는 모든 노드가 정상
        System.out.println("Initial status:");
        System.out.println("Node1 healthy: " + monitor.isNodeHealthy("node1"));
        System.out.println("Node2 healthy: " + monitor.isNodeHealthy("node2"));
        System.out.println("Node3 healthy: " + monitor.isNodeHealthy("node3"));
        
        // 3초 후 node1과 node2의 하트비트 업데이트
        Thread.sleep(3000);
        monitor.updateHeartbeat("node1");
        monitor.updateHeartbeat("node2");
        
        // 3초 더 대기 (node3는 하트비트 업데이트 없음)
        Thread.sleep(3000);
        
        // 다시 node1만 하트비트 업데이트
        monitor.updateHeartbeat("node1");
        
        // 5초 더 대기
        Thread.sleep(5000);
        
        System.out.println("Final status:");
        System.out.println("Node1 healthy: " + monitor.isNodeHealthy("node1"));
        System.out.println("Node2 healthy: " + monitor.isNodeHealthy("node2"));
        System.out.println("Node3 healthy: " + monitor.isNodeHealthy("node3"));
        
        monitor.stopMonitoring();
        System.out.println();
    }
    
    /**
     * 메인 메소드 - 모든 테스트 실행
     */
    public static void main(String[] args) {
        System.out.println("Cloud/On-Premise 환경 핵심 알고리즘 테스트 시작");
        System.out.println("=".repeat(60));
        
        try {
            // 1. 로드 밸런서 테스트
            testLoadBalancer();
            
            // 2. 일관된 해싱 테스트
            testConsistentHashing();
            
            // 3. 서킷 브레이커 테스트
            testCircuitBreaker();
            
            // 4. 데이터 파티셔닝 테스트
            testDataPartitioning();
            
            // 5. 메시지 큐 테스트
            testMessageQueue();
            
            // 6. 리소스 모니터링 테스트
            testResourceMonitoring();
            
            // 7. 하트비트 모니터링 테스트
            testHeartbeatMonitoring();
            
        } catch (InterruptedException e) {
            System.err.println("테스트 중 인터럽트 발생: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("테스트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("모든 테스트 완료");
    }
}