import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConsoleMaskingSample {

    private static final String MASK_POLICY_FILE = "mask_policy.txt"; // 마스킹 정책 파일(샘플: "이름#1-2")
    private static final String MASKED_DATA_FILE = "masked_data.txt"; // 마스킹 변환 결과 저장 파일

    public static void main(String[] args) throws Exception {
        System.out.println("입력값(이름,전화번호,이메일): ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine(); // 예: 홍길동,01012345678,abc@naver.com
        String[] fields = input.split(",");

        Map<String, String> policy = loadMaskPolicy();
        Map<String, String> masked = new HashMap<>();

        LocalDateTime start = LocalDateTime.now();

        // 필드에 따른 마스킹 처리 (정책적용)
        for (int i = 0; i < fields.length; i++) {
            String value = fields[i];
            String type = getFieldType(i); // 예시: 0=이름, 1=전화번호, 2=이메일 등
            masked.put(type, maskValue(value, policy.get(type)));
        }

        // 결과 출력 및 저장
        System.out.println("마스킹 결과: " + masked);
        saveMapToFile(masked, MASKED_DATA_FILE);
        LocalDateTime end = LocalDateTime.now();

        System.out.println("작업 수행 시간(ms): " + Duration.between(start, end).toMillis());
    }

    // 정책 파일 읽기: "이름#1-2" 형식
    private static Map<String, String> loadMaskPolicy() throws IOException {
        Map<String, String> map = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(MASK_POLICY_FILE));
        String line;
        while ((line = br.readLine()) != null) {
            String[] arr = line.split("#");
            map.put(arr[0], arr[1]);
        }
        br.close();
        return map;
    }

    // 마스킹 처리: 예시, 범위만 *로 치환
    private static String maskValue(String value, String range) {
        if (range == null) return value;
        String[] idx = range.split("-");
        int start = Integer.parseInt(idx[0]);
        int end = Integer.parseInt(idx[1]);
        StringBuilder sb = new StringBuilder(value);
        for (int i = start; i <= end && i < value.length(); i++) sb.setCharAt(i, '*');
        return sb.toString();
    }

    // 필드 인덱스를 타입명으로 변환
    private static String getFieldType(int idx) {
        if (idx == 0) return "이름";
        if (idx == 1) return "전화번호";
        if (idx == 2) return "이메일";
        return "기타";
    }

    // 결과 저장
    private static void saveMapToFile(Map<String, String> map, String file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        for (String key : map.keySet()) {
            bw.write(key + ":" + map.get(key));
            bw.newLine();
        }
        bw.close();
    }
}
