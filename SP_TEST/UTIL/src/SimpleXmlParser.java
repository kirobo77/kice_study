import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleXmlParser {
    public static void main(String[] args) throws IOException {
        String filePath = "sample.xml";
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // <title>내용</title> 형식 태그 값을 추출
        Pattern xmlPattern = Pattern.compile("<title>(.*?)</title>");

        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = xmlPattern.matcher(line);
            if (matcher.find()) {
                System.out.println("추출된 title: " + matcher.group(1));
            }
        }
        reader.close();
    }
}