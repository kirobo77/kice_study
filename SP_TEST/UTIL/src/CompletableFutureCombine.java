import java.util.concurrent.CompletableFuture;

public class CompletableFutureCombine {
    public static void main(String[] args) {
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> "World!");

        // 두 작업의 결과를 결합
        CompletableFuture<String> greeting = hello.thenCombine(world, (h, w) -> h + " " + w);

        System.out.println(greeting.join()); // 출력: Hello World!
    }
}