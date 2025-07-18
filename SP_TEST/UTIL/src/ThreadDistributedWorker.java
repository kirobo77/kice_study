import com.google.gson.Gson;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.*;

// 메인 클래스
public class ThreadDistributedWorker {
    private final Map<String, Integer> agentAllocations;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public ThreadDistributedWorker(Map<String, Integer> agentAllocations) {
        this.agentAllocations = agentAllocations;
    }

    // 작업 분배 및 실행
    public void distributeAndExecuteTasks(int[] jobs) throws InterruptedException, ExecutionException {
        int offset = 0;
        List<Future<List<String>>> futures = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : agentAllocations.entrySet()) {
            String agent = entry.getKey();
            int count = entry.getValue();
            int[] subJobs = Arrays.copyOfRange(jobs, offset, offset + count);
            Callable<List<String>> task = new AgentWorker(agent, subJobs);
            futures.add(executor.submit(task));
            offset += count;
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // 결과 출력
        for (Future<List<String>> future : futures) {
            List<String> results = future.get(); // 작업 결과 수신
            for (String result : results) {
                System.out.println(result);
            }
        }
    }

    // 정책 파일 로드
    public static Map<String, Integer> loadAllocations(String path) throws Exception {
        Gson gson = new Gson();
        Map<String, Double> temp = gson.fromJson(new FileReader(path), Map.class);
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, Double> entry : temp.entrySet()) {
            result.put(entry.getKey(), entry.getValue().intValue());
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        Map<String, Integer> allocations = loadAllocations("allocation.json");
        ThreadDistributedWorker worker = new ThreadDistributedWorker(allocations);

        // 총 작업량은 100개 (A:50, B:30, C:20)
        int[] jobs = new int[100];
        for (int i = 0; i < 100; i++) {
            jobs[i] = i + 1;
        }

        worker.distributeAndExecuteTasks(jobs);
    }
}

// Agent 작업자 클래스 (Callable로 변경)
class AgentWorker implements Callable<List<String>> {
    private final String agentName;
    private final int[] jobs;

    public AgentWorker(String agentName, int[] jobs) {
        this.agentName = agentName;
        this.jobs = jobs;
    }

    @Override
    public List<String> call() {
        List<String> results = new ArrayList<>();
        for (int job : jobs) {
            // 실제 처리 로직 (예: job * 2)
            int result = job * 2;
            results.add(agentName + " 처리 결과: " + job + " => " + result);
        }
        return results;
    }
}
