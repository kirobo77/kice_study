import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class MultiAgentResultCollect {
	
	static class AgentWorker implements Callable<Integer> {
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
	        System.out.println(agentName + " 작업 완료: " + result);
	        return result;
	    }
	}
	
    public static void main(String[] args) throws Exception {
        Map<String, Integer> policy = Map.of("A", 50, "B", 30, "C", 20);
        int input = 100;
        ExecutorService executor = Executors.newFixedThreadPool(policy.size());
        Map<String, Future<Integer>> futures = new HashMap<>();

        // Agent별 작업 제출
        for (Map.Entry<String, Integer> entry : policy.entrySet()) {
            futures.put(entry.getKey(), executor.submit(
                new AgentWorker(entry.getKey(), entry.getValue(), input)));
        }

        // 결과 수집
        Map<String, Integer> results = new HashMap<>();
        for (Map.Entry<String, Future<Integer>> entry : futures.entrySet()) {
            results.put(entry.getKey(), entry.getValue().get());
        }

        System.out.println("전체 결과: " + results);
        executor.shutdown();
    }
}