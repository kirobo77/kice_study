import java.util.concurrent.CompletableFuture;

public class CompletableFutureBasic {
    public static void main(String[] args) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            // 비동기 작업
            return "Hello";
        }).thenApply(result -> result + " World!");

        // 결과값 대기 및 반환
        System.out.println(future.join()); // 출력: Hello World!
    }
}