import java.util.Arrays;
import java.util.List;

public class StreamNumberExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

        int sumOfSquares = numbers.stream()
            .filter(n -> n % 2 == 0)     // 짝수만
            .map(n -> n * n)             // 제곱
            .reduce(0, Integer::sum);    // 합계

        System.out.println(sumOfSquares); // 56 (2*2 + 4*4 + 6*6)
    }
}