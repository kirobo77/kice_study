import java.util.*;

/**
 * Smooth Weighted Round Robin 알고리즘 상세 분석
 */
public class SmoothWRRAnalysis {

    static class Server {
        String name;
        int weight;          // 원래 가중치 (고정값)
        int currentWeight;   // 현재 가중치 (동적 변경)

        Server(String name, int weight) {
            this.name = name;
            this.weight = weight;
            this.currentWeight = 0;
        }

        @Override
        public String toString() {
            return String.format("%s(w:%d, cw:%d)", name, weight, currentWeight);
        }
    }

    static class SmoothWRR {
        private List<Server> servers;
        private int totalWeight;

        public SmoothWRR(List<Server> servers) {
            this.servers = servers;
            this.totalWeight = servers.stream().mapToInt(s -> s.weight).sum();
        }

        public Server selectServer() {
            if (servers.isEmpty()) return null;

            System.out.println("\n--- 서버 선택 과정 ---");

            // 1단계: 모든 서버의 현재 가중치를 원래 가중치만큼 증가
            System.out.println("1단계: 현재 가중치 += 원래 가중치");
            for (Server server : servers) {
                server.currentWeight += server.weight;
                System.out.printf("  %s: %d + %d = %d%n",
                        server.name,
                        server.currentWeight - server.weight,
                        server.weight,
                        server.currentWeight);
            }

            // 2단계: 현재 가중치가 가장 높은 서버 선택
            System.out.println("\n2단계: 최고 현재 가중치 서버 선택");
            Server selected = servers.stream()
                    .max(Comparator.comparingInt(s -> s.currentWeight))
                    .orElse(null);

            System.out.printf("  선택된 서버: %s (현재 가중치: %d)%n",
                    selected.name, selected.currentWeight);

            // 3단계: 선택된 서버의 현재 가중치를 전체 가중치만큼 감소
            System.out.println("\n3단계: 선택된 서버의 현재 가중치 -= 전체 가중치");
            System.out.printf("  %s: %d - %d = %d%n",
                    selected.name,
                    selected.currentWeight,
                    totalWeight,
                    selected.currentWeight - totalWeight);

            selected.currentWeight -= totalWeight;

            // 현재 상태 출력
            System.out.println("\n현재 서버 상태:");
            for (Server server : servers) {
                System.out.printf("  %s%n", server);
            }

            return selected;
        }

        public void printCurrentState() {
            System.out.println("\n=== 현재 서버 상태 ===");
            for (Server server : servers) {
                System.out.printf("%-8s | 원래 가중치: %d | 현재 가중치: %3d%n",
                        server.name, server.weight, server.currentWeight);
            }
            System.out.printf("전체 가중치: %d%n", totalWeight);
        }
    }

    /**
     * 수학적 증명을 위한 분석
     */
    static class MathematicalAnalysis {

        public static void analyzeBalance() {
            System.out.println("\n=== 수학적 균형 분석 ===");

            List<Server> servers = Arrays.asList(
                    new Server("A", 3),
                    new Server("B", 2),
                    new Server("C", 1)
            );

            SmoothWRR wrr = new SmoothWRR(servers);
            Map<String, Integer> selectionCount = new HashMap<>();

            // 한 사이클 (totalWeight = 6번) 실행
            System.out.println("한 사이클 (6번 선택) 분석:");

            for (int i = 1; i <= 6; i++) {
                System.out.printf("\n<<< %d번째 선택 >>>", i);
                Server selected = wrr.selectServer();
                selectionCount.merge(selected.name, 1, Integer::sum);
            }

            // 최종 결과 분석
            System.out.println("\n=== 한 사이클 후 결과 ===");
            int totalSelections = 6;

            for (Server server : servers) {
                int count = selectionCount.getOrDefault(server.name, 0);
                double actualRatio = (double) count / totalSelections;
                double expectedRatio = (double) server.weight / wrr.totalWeight;

                System.out.printf("서버 %s: 선택 %d회 (%.1f%%) | 예상 %.1f%% | 오차: %.1f%%\n",
                        server.name, count, actualRatio * 100, expectedRatio * 100,
                        Math.abs(actualRatio - expectedRatio) * 100);
            }

            // 현재 가중치가 모두 0으로 돌아왔는지 확인 (사이클 완료 증명)
            System.out.println("\n사이클 완료 검증 (모든 현재 가중치가 0이어야 함):");
            boolean cycleComplete = servers.stream()
                    .allMatch(s -> s.currentWeight == 0);
            System.out.println("사이클 완료: " + cycleComplete);
        }

        public static void compareWithTraditionalWRR() {
            System.out.println("\n=== Traditional WRR vs Smooth WRR 비교 ===");

            // Traditional WRR 시뮬레이션
            System.out.println("Traditional WRR (A:3, B:2, C:1):");
            List<String> traditional = Arrays.asList(
                    "A", "A", "A", "B", "B", "C"
            );
            System.out.println("  순서: " + String.join(" -> ", traditional));

            // Smooth WRR 시뮬레이션
            System.out.println("\nSmooth WRR (A:3, B:2, C:1):");
            List<Server> servers = Arrays.asList(
                    new Server("A", 3),
                    new Server("B", 2),
                    new Server("C", 1)
            );

            SmoothWRR wrr = new SmoothWRR(servers);
            List<String> smooth = new ArrayList<>();

            for (int i = 0; i < 6; i++) {
                Server selected = wrr.selectServer();
                smooth.add(selected.name);
            }

            System.out.println("  순서: " + String.join(" -> ", smooth));

            // 연속성 분석
            System.out.println("\n연속성 분석:");
            System.out.println("Traditional WRR:");
            analyzeConsecutiveness(traditional);

            System.out.println("Smooth WRR:");
            analyzeConsecutiveness(smooth);
        }

        private static void analyzeConsecutiveness(List<String> sequence) {
            Map<String, Integer> maxConsecutive = new HashMap<>();

            int currentStreak = 1;
            String currentServer = sequence.get(0);
            int maxStreakForCurrent = 1;

            for (int i = 1; i < sequence.size(); i++) {
                if (sequence.get(i).equals(currentServer)) {
                    currentStreak++;
                    maxStreakForCurrent = Math.max(maxStreakForCurrent, currentStreak);
                } else {
                    maxConsecutive.put(currentServer,
                            Math.max(maxConsecutive.getOrDefault(currentServer, 0), maxStreakForCurrent));
                    currentServer = sequence.get(i);
                    currentStreak = 1;
                    maxStreakForCurrent = 1;
                }
            }
            maxConsecutive.put(currentServer,
                    Math.max(maxConsecutive.getOrDefault(currentServer, 0), maxStreakForCurrent));

            for (Map.Entry<String, Integer> entry : maxConsecutive.entrySet()) {
                System.out.printf("  서버 %s 최대 연속: %d회%n",
                        entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 실제 성능 비교 시뮬레이션
     */
    static class PerformanceSimulation {

        static class LoadMetrics {
            Map<String, List<Integer>> serverLoads = new HashMap<>();

            void recordLoad(String server, int load) {
                serverLoads.computeIfAbsent(server, k -> new ArrayList<>()).add(load);
            }

            void printAnalysis() {
                System.out.println("\n=== 부하 분석 결과 ===");

                for (Map.Entry<String, List<Integer>> entry : serverLoads.entrySet()) {
                    String server = entry.getKey();
                    List<Integer> loads = entry.getValue();

                    double avg = loads.stream().mapToInt(Integer::intValue).average().orElse(0);
                    int max = loads.stream().mapToInt(Integer::intValue).max().orElse(0);
                    int min = loads.stream().mapToInt(Integer::intValue).min().orElse(0);

                    // 표준편차 계산
                    double variance = loads.stream()
                            .mapToDouble(load -> Math.pow(load - avg, 2))
                            .average().orElse(0);
                    double stdDev = Math.sqrt(variance);

                    System.out.printf("서버 %s: 평균=%.1f, 최대=%d, 최소=%d, 표준편차=%.2f%n",
                            server, avg, max, min, stdDev);
                }
            }
        }

        public static void simulateLoad() {
            System.out.println("\n=== 부하 분산 시뮬레이션 ===");

            List<Server> servers = Arrays.asList(
                    new Server("High-Spec", 5),    // 고성능 서버
                    new Server("Mid-Spec", 3),     // 중간 서버
                    new Server("Low-Spec", 2)      // 저성능 서버
            );

            SmoothWRR wrr = new SmoothWRR(servers);
            LoadMetrics metrics = new LoadMetrics();

            // 시간대별 부하 시뮬레이션 (20개 요청)
            System.out.println("20개 요청 처리 과정:");

            for (int i = 1; i <= 20; i++) {
                Server selected = wrr.selectServer();

                // 서버별 처리 시간 시뮬레이션 (가중치가 높을수록 빠름)
                int processingTime = 100 / selected.weight + new Random().nextInt(20);

                metrics.recordLoad(selected.name, processingTime);

                System.out.printf("요청 %2d -> %s (처리시간: %dms)%n",
                        i, selected.name, processingTime);
            }

            metrics.printAnalysis();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Smooth Weighted Round Robin 상세 분석 ===");

        // 1. 기본 동작 원리 시연
        System.out.println("\n1. 기본 동작 원리 시연");
        List<Server> servers = Arrays.asList(
                new Server("A", 3),
                new Server("B", 2),
                new Server("C", 1)
        );

        SmoothWRR wrr = new SmoothWRR(servers);
        wrr.printCurrentState();

        // 3번 선택 과정을 자세히 보여줌
        for (int i = 1; i <= 3; i++) {
            System.out.printf("\n================== %d번째 요청 ==================", i);
            Server selected = wrr.selectServer();
            System.out.printf("\n>>> 선택된 서버: %s <<<", selected.name);
        }

        // 2. 수학적 분석
        MathematicalAnalysis.analyzeBalance();

        // 3. Traditional WRR과 비교
        MathematicalAnalysis.compareWithTraditionalWRR();

        // 4. 성능 시뮬레이션
        PerformanceSimulation.simulateLoad();

        // 5. 알고리즘의 핵심 특성 요약
        System.out.println("\n=== Smooth WRR의 핵심 특성 ===");
        System.out.println("1. 균등 분산: 연속된 요청이 한 서버에 몰리지 않음");
        System.out.println("2. 가중치 보장: 장기적으로 정확한 비율 유지");
        System.out.println("3. 예측 가능: 수학적으로 증명된 균형점");
        System.out.println("4. 효율성: O(n) 시간 복잡도로 빠른 선택");
        System.out.println("5. 안정성: 사이클마다 초기 상태로 복귀");
    }
}

/**
 * Nginx 구현과 동일한 최적화된 버전
 */
class OptimizedSmoothWRR {

    static class ServerNode {
        String name;
        int weight;
        int effectiveWeight;  // 헬스체크 실패시 동적 조정
        int currentWeight;

        ServerNode(String name, int weight) {
            this.name = name;
            this.weight = weight;
            this.effectiveWeight = weight;
            this.currentWeight = 0;
        }
    }

    private List<ServerNode> servers;
    private int totalWeight;

    public OptimizedSmoothWRR(List<ServerNode> servers) {
        this.servers = servers;
        updateTotalWeight();
    }

    private void updateTotalWeight() {
        totalWeight = servers.stream()
                .mapToInt(s -> s.effectiveWeight)
                .sum();
    }

    public ServerNode selectServer() {
        if (servers.isEmpty() || totalWeight <= 0) {
            return null;
        }

        ServerNode best = null;

        for (ServerNode server : servers) {
            // 현재 가중치를 효과적 가중치만큼 증가
            server.currentWeight += server.effectiveWeight;

            // 가장 높은 현재 가중치를 가진 서버 찾기
            if (best == null || server.currentWeight > best.currentWeight) {
                best = server;
            }
        }

        if (best != null) {
            // 선택된 서버의 현재 가중치를 전체 가중치만큼 감소
            best.currentWeight -= totalWeight;
        }

        return best;
    }

    // 헬스체크 실패시 가중치 조정
    public void adjustWeight(String serverName, boolean healthy) {
        for (ServerNode server : servers) {
            if (server.name.equals(serverName)) {
                if (healthy) {
                    server.effectiveWeight = Math.min(server.effectiveWeight + 1, server.weight);
                } else {
                    server.effectiveWeight = Math.max(server.effectiveWeight - 1, 0);
                }
                updateTotalWeight();
                break;
            }
        }
    }
}