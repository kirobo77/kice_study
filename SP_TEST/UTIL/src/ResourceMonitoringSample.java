import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Cloud/On-Premise 환경에서 요구되는 핵심 알고리즘 구현
 * 
 * 리소스 모니터링 알고리즘 (Moving Average)
 */
public class ResourceMonitoringSample {

    // ===== 리소스 모니터링 알고리즘 =====
    
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
     * 메인 메소드 - 테스트 실행
     */
    public static void main(String[] args) {
        System.out.println("리소스 모니터링 테스트 시작");
        System.out.println("=".repeat(40));
        
        try {
            // 리소스 모니터링 테스트
            testResourceMonitoring();
        } catch (InterruptedException e) {
            System.err.println("테스트 중 인터럽트 발생: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("테스트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("리소스 모니터링 테스트 완료");
    }    
    
}
