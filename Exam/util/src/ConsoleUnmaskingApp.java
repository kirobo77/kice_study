import java.io.*;
import java.util.*;

/**
 * 콘솔 기반 언마스킹 프로그램 (원본 정보 파일 활용)
 */
public class ConsoleUnmaskingApp {
    // 고객명:원본데이터 형태의 파일에서 데이터 조회
    public static String loadOriginalData(String customerName, String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] kv = line.split(":");
                if (kv.length == 2 && kv[0].equals(customerName)) return kv[1];
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("언마스킹할 고객명 입력: ");
        String name = sc.nextLine();

        String filePath = "original_data.txt";
        String originalData = loadOriginalData(name, filePath);

        if (originalData != null) {
            System.out.println("원본 데이터: " + originalData);
        } else {
            System.out.println("해당 고객의 원본 데이터가 없습니다.");
        }
    }
}
