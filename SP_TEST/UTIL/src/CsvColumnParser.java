import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CsvColumnParser {
    public static void main(String[] args) throws IOException {
        String filePath = "data.csv";
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // CSV의 각 라인을 정규표현식으로 split
        // 여기서는 콤마(,)로 구분, 따옴표 처리 간단화
        String line;
        while ((line = reader.readLine()) != null) {
            // 값이 콤마를 포함하지 않는 경우에 유용 (간단형)
            String[] fields = line.split(",");
            // 예: 2번째 칼럼(인덱스 1) 추출
            if (fields.length > 1) {
                System.out.println("2번째 칼럼 값: " + fields[1]);
            }
        }
        reader.close();
    }
}