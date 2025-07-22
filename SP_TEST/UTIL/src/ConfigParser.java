import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigParser {
    public static void main(String[] args) throws IOException {
        String filePath = "config.txt";
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // 'key = value' 형식의 정규표현식
        Pattern pattern = Pattern.compile("^\\s*(\\w+)\\s*=\\s*(.+)$");

        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                // 그룹 1: key, 그룹 2: value
                System.out.println("Key: " + matcher.group(1) + ", Value: " + matcher.group(2));
            }
        }
        reader.close();
    }
}