import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultiAgentThreadExample {
	
	static class AgentWorker implements Runnable {
	    private final String agentName;
	    private final int taskCount;
	    private final int input;

	    public AgentWorker(String agentName, int taskCount, int input) {
	        this.agentName = agentName;
	        this.taskCount = taskCount;
	        this.input = input;
	    }

	    @Override
	    public void run() {
	        int result = 0;
	        for (int i = 0; i < taskCount; i++) {
	            result += (input + i);
	        }
	        System.out.println(agentName + " 작업 완료: " + result);
	    }
	}
	
    public static void main(String[] args) {
        // 각 Agent별 할당량
        Map<String, Integer> policy = Map.of("A", 50, "B", 30, "C", 20);
        int input = 100;

        List<Thread> threads = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : policy.entrySet()) {
            Thread t = new Thread(new AgentWorker(entry.getKey(), entry.getValue(), input));
            threads.add(t);
            t.start();
        }

        // 모든 스레드가 종료될 때까지 대기
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.out.println("스레드 대기 중 인터럽트 발생");
            }
        }
        System.out.println("모든 Agent 작업 완료");
    }
}