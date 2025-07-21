import java.util.LinkedList;
import java.util.Queue;

/**
 * Cloud/On-Premise 환경에서 요구되는 핵심 알고리즘 구현
 * 
 * 메시지 큐 알고리즘 (Producer-Consumer Pattern)
 */
public class MessageQueueSample {

    // ===== 메시지 큐 알고리즘 =====
    
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
     * 메인 메소드 - 테스트 실행
     */
    public static void main(String[] args) {
        System.out.println("메시지 큐 테스트 시작");
        System.out.println("=".repeat(40));
        
        try {
            // 메시지 큐 테스트
            testMessageQueue();
        } catch (InterruptedException e) {
            System.err.println("테스트 중 인터럽트 발생: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("테스트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("메시지 큐 테스트 완료");
    }    
    
}
