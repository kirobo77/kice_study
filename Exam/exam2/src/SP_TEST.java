import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class SP_TEST {
    private static final Map<String, String> dictionary = new HashMap<>();
    private static final Set<String> stopwords = new HashSet<>();

    public static void main(String[] args) throws Exception {
        loadDictionary("DICTIONARY.TXT");
        loadStopwords("STOPWORD.TXT");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            String[] tokens = line.trim().split("\\s+");
            List<String> vectors = new ArrayList<>();
            for (String token : tokens) {
                String key = token.toLowerCase();
                String vector = dictionary.get(key);
                if (vector != null && !stopwords.contains(vector)) {
                    vectors.add(vector);
                }
            }
            System.out.println(String.join(" ", vectors));
        }
    }

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

    private static void loadStopwords(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                stopwords.add(line.trim());
            }
        }
    }
}