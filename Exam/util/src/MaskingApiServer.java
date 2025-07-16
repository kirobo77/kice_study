import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.client.HttpClient;
import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class MaskingApiServer {
    // Gson 객체 (JSON 직렬화/역직렬화용)
    private static final Gson gson = new Gson();

    // Jetty 서버 구축 및 엔드포인트 매핑
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080); // 8080 포트 사용
        server.setHandler(new Handler());
        server.start();

        // Jetty HttpClient 샘플 (서버 동작 확인용)
        HttpClient client = new HttpClient();
        client.start();
        // 요청/응답 예시는 필요시 아래 참고
    }

    // Jetty 9 Handler에서 요청별 분기 처리
    public static class Handler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, 
                           HttpServletRequest request, HttpServletResponse response) 
                throws IOException {

            long startTime = System.currentTimeMillis(); // 작업시작 시간

            if ("/mask".equals(target) && "POST".equalsIgnoreCase(request.getMethod())) {
                // 마스킹 엔드포인트
                String line = readBody(request);
                String customerId = request.getParameter("customerId"); // 고객별 마스킹 정책 적용
                Map<String, String> masked = maskData(line, customerId);
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(masked));
            } else if ("/unmask".equals(target) && "POST".equalsIgnoreCase(request.getMethod())) {
                // 언마스킹 엔드포인트
                String line = readBody(request);
                String customerId = request.getParameter("customerId");
                Map<String, String> unmasked = unmaskData(line, customerId);
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(unmasked));
            }
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);

            long endTime = System.currentTimeMillis(); // 작업종료 시간
            System.out.println("작업 소요 시간: " + (endTime - startTime) + " ms");
        }
    }

    // HTTP 요청 본문 읽기
    private static String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = req.getReader();
        String line;
        while ((line = br.readLine()) != null)
            sb.append(line);
        return sb.toString();
    }

    // 마스킹 정책 적용, 마스킹결과 반환 및 변환정보 파일 저장
    private static Map<String, String> maskData(String data, String customerId) throws IOException {
        // 예) "name|phone|email"
        String[] fields = data.split("\\|");
        Map<String, String> policy = loadMaskPolicy(customerId);

        Map<String, String> masked = new LinkedHashMap<>();
        Map<String, String> transInfo = new LinkedHashMap<>();
        for (int i = 0; i < fields.length; i++) {
            String fieldKey = "field" + (i + 1);
            String policyStr = policy.getOrDefault(fieldKey, "");
            if ("phone".equals(policyStr)) {
                masked.put(fieldKey, maskPhone(fields[i]));
                transInfo.put(fieldKey, fields[i]);
            } else {
                masked.put(fieldKey, fields[i]);
            }
        }
        // 변환정보 파일 저장 (언마스킹용)
        saveTransInfo(customerId, masked, transInfo);
        return masked;
    }

    // 언마스킹 변환(변환정보 파일에서 원본데이터 치환)
    private static Map<String, String> unmaskData(String data, String customerId) throws IOException {
        String[] fields = data.split("\\|");
        Map<String, String> original = loadTransInfo(customerId);
        Map<String, String> unmasked = new LinkedHashMap<>();
        for (int i = 0; i < fields.length; i++) {
            String fieldKey = "field" + (i + 1);
            if (original.containsKey(fieldKey)) {
                unmasked.put(fieldKey, original.get(fieldKey));
            } else {
                unmasked.put(fieldKey, fields[i]);
            }
        }
        return unmasked;
    }

    // 전화번호 부분 마스킹 예시 (3번째~6번째 자리 '*')
    private static String maskPhone(String phone) {
        if (phone.length() > 6)
            return phone.substring(0, 3) + "****" + phone.substring(7);
        return phone;
    }

    // 마스킹 정책 파일 읽기 (고객별)
    private static Map<String, String> loadMaskPolicy(String customerId) throws IOException {
        // 예시 정책파일: customerId_maskpolicy.txt, 내용: field1=name\nfield2=phone\nfield3=email
        List<String> lines = Files.readAllLines(Paths.get(customerId + "_maskpolicy.txt"));
        Map<String, String> map = new HashMap<>();
        for (String ln : lines) {
            String[] kv = ln.split("=");
            map.put(kv[0], kv[1]);
        }
        return map;
    }

    // 변환정보 파일 저장 (마스킹 시)
    private static void saveTransInfo(String customerId, Map<String, String> masked, Map<String, String> transInfo) throws IOException {
        // 저장: customerId_transinfo.txt, field1:원본값|field2:원본값 ...
        StringJoiner sj = new StringJoiner("|");
        for (Map.Entry<String, String> entry : transInfo.entrySet()) {
            sj.add(entry.getKey() + ":" + entry.getValue());
        }
        Files.write(Paths.get(customerId + "_transinfo.txt"), sj.toString().getBytes());
    }

    // 변환정보 파일 로드 (언마스킹 시)
    private static Map<String, String> loadTransInfo(String customerId) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(customerId + "_transinfo.txt"));
        Map<String, String> map = new HashMap<>();
        for (String part : String.join("", lines).split("\\|")) {
            String[] kv = part.split(":");
            map.put(kv[0], kv[1]);
        }
        return map;
    }
}
