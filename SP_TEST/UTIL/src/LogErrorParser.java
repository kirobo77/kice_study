import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogErrorParser {
    public static void main(String[] args) throws IOException {
        String filePath = "server.log";
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // 로그 예시: [2025-07-15 12:00:00] ERROR: Some error message
        Pattern logPattern = Pattern.compile("\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\] ERROR: (.+)");

        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = logPattern.matcher(line);
            if (matcher.find()) {
                String date = matcher.group(1);
                String errorMsg = matcher.group(2);
                System.out.println("오류 발생시각: " + date + ", 메시지: " + errorMsg);
            }
        }
        reader.close();
    }
}