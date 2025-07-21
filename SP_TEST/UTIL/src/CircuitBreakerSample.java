import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cloud/On-Premise 환경에서 요구되는 핵심 알고리즘 구현
 * 
 * 장애 감지 알고리즘  알고리즘 (Heartbeat with Circuit Breaker)
 */
public class CircuitBreakerSample {


    // ===== 장애 감지 알고리즘 (Heartbeat with Circuit Breaker) =====
    
	  
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
     * 메인 메소드 - 테스트 실행
     */
    public static void main(String[] args) {
        System.out.println("서킷 브레이커 & 하트비트 모니터링 테스트 테스트 시작");
        System.out.println("=".repeat(40));
        
        try {
            
            // 서킷 브레이커 테스트
            testCircuitBreaker();   
            // 하트비트 모니터링 테스트
            testHeartbeatMonitoring();            
        } catch (Exception e) {
            System.err.println("테스트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("서킷 브레이커 & 하트비트 모니터링 테스트 테스트 완료");
    }    
    
}
