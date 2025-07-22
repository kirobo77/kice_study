import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailExtractor {
    public static void main(String[] args) throws IOException {
        // 읽을 파일 경로를 지정하세요
        String filePath = "example.txt";
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // 이메일 형식의 정규표현식 (간단 버전)
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = emailPattern.matcher(line);
            // 한 줄에서 발견되는 모든 이메일을 검색
            while (matcher.find()) {
                System.out.println("추출된 이메일: " + matcher.group());
            }
        }
        reader.close();
    }
}