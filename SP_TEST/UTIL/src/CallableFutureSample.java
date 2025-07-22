import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CallableFutureSample {
	
	// Callable을 구현하여 결과값 반환
	static class SumTask implements Callable<Integer> {
	    private final int start;
	    private final int count;

	    public SumTask(int start, int count) {
	        this.start = start;
	        this.count = count;
	    }

	    @Override
	    public Integer call() {
	        int sum = 0;
	        for (int i = 0; i < count; i++) {
	            sum += (start + i);
	        }
	        return sum;
	    }
	}
	
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // 각 Agent별 작업 제출
        Future<Integer> resultA = executor.submit(new SumTask(100, 50)); // A Agent
        Future<Integer> resultB = executor.submit(new SumTask(100, 30)); // B Agent
        Future<Integer> resultC = executor.submit(new SumTask(100, 20)); // C Agent

        // 결과값 받기 (get()은 블로킹 호출)
        System.out.println("A Agent 결과: " + resultA.get());
        System.out.println("B Agent 결과: " + resultB.get());
        System.out.println("C Agent 결과: " + resultC.get());

        executor.shutdown();
    }
}