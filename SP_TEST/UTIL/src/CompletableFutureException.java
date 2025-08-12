import java.util.concurrent.CompletableFuture;

public class CompletableFutureException {
    public static void main(String[] args) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            if (true) throw new RuntimeException("오류 발생!");
            return "정상";
        }).exceptionally(ex -> "예외 처리됨: " + ex.getMessage());

        System.out.println(future.join()); // 출력: 예외 처리됨: 오류 발생!
    }
}