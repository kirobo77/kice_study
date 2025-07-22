public class ThreadExample2 {
	
	// Runnable 인터페이스를 구현하여 스레드 실행
	public static class MyRunnable implements Runnable {
	    @Override
	    public void run() {
	        for (int i = 0; i < 5; i++) {
	            System.out.println("MyRunnable 실행: " + i);
	            try {
	                Thread.sleep(500);
	            } catch (InterruptedException e) {
	                System.out.println("인터럽트 발생");
	            }
	        }
	    }
	}
	
    public static void main(String[] args) {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
    }
}