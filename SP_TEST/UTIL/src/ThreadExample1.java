public class ThreadExample1 {
	
	// Thread 클래스를 상속하여 스레드 구현
	public static class MyThread extends Thread {
	    @Override
	    public void run() {
	        // 스레드가 실행할 작업
	        for (int i = 0; i < 5; i++) {
	            System.out.println("MyThread 실행: " + i);
	            try {
	                Thread.sleep(500); // 0.5초 대기
	            } catch (InterruptedException e) {
	                System.out.println("인터럽트 발생");
	            }
	        }
	    }
	}
	
    public static void main(String[] args) {
        MyThread thread = new MyThread();
        thread.start(); // 스레드 시작
    }
}