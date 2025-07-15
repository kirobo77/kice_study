import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SP_TEST {
    // 단어 사전 저장용 Map
    private static final Map<String, String> dictionary = new HashMap<>();

    public static void main(String[] args) throws Exception {
        loadDictionary("DICTIONARY.TXT");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            String[] tokens = line.trim().split("\\s+");
            List<String> vectors = new ArrayList<>();
            for (String token : tokens) {
                String key = token.toLowerCase();
                if (dictionary.containsKey(key)) {
                    vectors.add(dictionary.get(key));
                }
            }
            System.out.println(String.join(" ", vectors));
        }
        
    }

    // 단어 사전 파일 로드
    private static void loadDictionary(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("#");
                if (parts.length == 2) {
                    dictionary.put(parts[0], parts[1]);
                }
            }
        }
    }
}