import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class TimePerformanceUtil {
    
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * 실행 시간 측정 클래스
     */
    public static class StopWatch {
        private Instant startTime;
        private Instant endTime;
        private boolean running = false;
        
        public void start() {
            this.startTime = Instant.now();
            this.running = true;
        }
        
        public void stop() {
            this.endTime = Instant.now();
            this.running = false;
        }
        
        public long getElapsedTimeMillis() {
            if (running) {
                return Duration.between(startTime, Instant.now()).toMillis();
            }
            return Duration.between(startTime, endTime).toMillis();
        }
        
        public long getElapsedTimeSeconds() {
            if (running) {
                return Duration.between(startTime, Instant.now()).getSeconds();
            }
            return Duration.between(startTime, endTime).getSeconds();
        }
        
        public Duration getElapsedTime() {
            if (running) {
                return Duration.between(startTime, Instant.now());
            }
            return Duration.between(startTime, endTime);
        }
        
        public String getFormattedElapsedTime() {
            Duration elapsed = getElapsedTime();
            long hours = elapsed.toHours();
            long minutes = elapsed.toMinutes() % 60;
            long seconds = elapsed.getSeconds() % 60;
            long millis = elapsed.toMillis() % 1000;
            
            return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
        }
        
        public boolean isRunning() {
            return running;
        }
    }
    
    /**
     * 메서드 실행 시간 측정
     */
    public static <T> T measureExecutionTime(String operationName, java.util.function.Supplier<T> operation) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        try {
            T result = operation.get();
            stopWatch.stop();
            System.out.println(operationName + " 실행 시간: " + stopWatch.getFormattedElapsedTime());
            return result;
        } catch (Exception e) {
            stopWatch.stop();
            System.out.println(operationName + " 실행 중 오류 발생. 소요 시간: " + stopWatch.getFormattedElapsedTime());
            throw e;
        }
    }
    
    /**
     * 현재 시간을 포맷된 문자열로 반환
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }
    
    /**
     * 시간 단위 변환
     */
    public static long convertTime(long time, TimeUnit fromUnit, TimeUnit toUnit) {
        return toUnit.convert(time, fromUnit);
    }
    
    /**
     * 두 시간 사이의 차이를 다양한 단위로 반환
     */
    public static void printTimeDifference(Instant start, Instant end) {
        Duration duration = Duration.between(start, end);
        
        System.out.println("시간 차이:");
        System.out.println("  밀리초: " + duration.toMillis());
        System.out.println("  초: " + duration.getSeconds());
        System.out.println("  분: " + duration.toMinutes());
        System.out.println("  시간: " + duration.toHours());
        System.out.println("  일: " + duration.toDays());
    }
    
    /**
     * 시스템 시간과 UTC 시간 비교
     */
    public static void compareSystemAndUTCTime() {
        LocalDateTime systemTime = LocalDateTime.now();
        LocalDateTime utcTime = LocalDateTime.now(ZoneId.of("UTC"));
        
        System.out.println("시스템 시간: " + systemTime.format(TIMESTAMP_FORMATTER));
        System.out.println("UTC 시간: " + utcTime.format(TIMESTAMP_FORMATTER));
        
        Duration difference = Duration.between(utcTime, systemTime);
        System.out.println("시차: " + difference.toHours() + "시간");
    }
    
    public static void main(String[] args) {
        // StopWatch 테스트
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        // 시뮬레이션된 작업
        try {
            Thread.sleep(1500); // 1.5초 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        stopWatch.stop();
        System.out.println("작업 완료 시간: " + stopWatch.getFormattedElapsedTime());
        
        // 메서드 실행 시간 측정
        String result = measureExecutionTime("문자열 처리", () -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 100000; i++) {
                sb.append("test");
            }
            return sb.toString();
        });
        
        System.out.println("처리된 문자열 길이: " + result.length());
        
        // 현재 타임스탬프
        System.out.println("현재 타임스탬프: " + getCurrentTimestamp());
        
        // 시간 단위 변환
        long millis = 5000;
        System.out.println(millis + "ms = " + convertTime(millis, TimeUnit.MILLISECONDS, TimeUnit.SECONDS) + "초");
        
        // 시간 차이 계산
        Instant start = Instant.now().minusSeconds(3600); // 1시간 전
        Instant end = Instant.now();
        printTimeDifference(start, end);
        
        // 시스템 시간과 UTC 시간 비교
        compareSystemAndUTCTime();
    }
}