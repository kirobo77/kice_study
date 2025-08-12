import java.util.concurrent.*;
import java.util.*;

class AgentWorker implements Callable<Integer> {
    private final String agentName;
    private final int taskCount;
    private final int input;

    public AgentWorker(String agentName, int taskCount, int input) {
        this.agentName = agentName;
        this.taskCount = taskCount;
        this.input = input;
    }

    @Override
    public Integer call() {
        int result = 0;
        for (int i = 0; i < taskCount; i++) {
            result += (input + i);
        }
        return result;
    }
}

public class MultiAgentCompletableFuture {
    public static void main(String[] args) {
        Map<String, Integer> policy = Map.of("A", 50, "B", 30, "C", 20);
        int input = 100;

        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : policy.entrySet()) {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                int result = 0;
                for (int i = 0; i < entry.getValue(); i++) {
                    result += (input + i);
                }
                return entry.getKey() + " 작업 결과: " + result;
            });
            futures.add(future);
        }

        // 모든 작업 완료 후 결과 출력
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenRun(() -> futures.forEach(f -> System.out.println(f.join())))
            .join();
    }
}