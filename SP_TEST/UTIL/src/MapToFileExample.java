import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapToFileExample {
    public static void main(String[] args) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("apple", "사과");
        map.put("banana", "바나나");
        map.put("grape", "포도");

        String filePath = "output.txt";     // 저장할 파일 경로
        String delimiter = "#";             // Key/Value 구분자

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                // Key와 Value를 구분자로 연결해 한 줄로 기록
                bw.write(entry.getKey() + delimiter + entry.getValue());
                bw.newLine(); // 줄 바꿈
            }
        }
        System.out.println("파일 저장 완료!");
    }
}