import java.io.IOException;
import java.io.PrintWriter;

public class RegexParsingTest {
    public static void main(String[] args) {
        // 테스트 파일 생성
        createTestFile();
        
        // 패턴 파싱 테스트
        PatternParser parser = new PatternParser("test_data.txt");
        
        System.out.println("=== 파일 내용 ===");
        parser.getAllContent();
        
        System.out.println("\n=== 이메일 추출 ===");
        parser.extractEmails().forEach(System.out::println);
        
        System.out.println("\n=== 전화번호 추출 ===");
        parser.extractPhoneNumbers().forEach(System.out::println);
        
        System.out.println("\n=== 날짜 추출 ===");
        parser.extractDates().forEach(System.out::println);
        
        System.out.println("\n=== IP 주소 추출 ===");
        parser.extractIpAddresses().forEach(System.out::println);
        
        System.out.println("\n=== URL 추출 ===");
        parser.extractUrls().forEach(System.out::println);
        
        System.out.println("\n=== 한글 이름 추출 ===");
        parser.extractKoreanNames().forEach(System.out::println);
        
        System.out.println("\n=== 금액 추출 ===");
        parser.extractPrices().forEach(System.out::println);
        
        // 그룹 캡처 테스트
        GroupCaptureParser groupParser = new GroupCaptureParser("test_data.txt");
        
        System.out.println("\n=== 날짜 그룹 분리 ===");
        groupParser.parseDatesWithGroups();
        
        System.out.println("\n=== 이메일 그룹 분리 ===");
        groupParser.parseEmailsWithGroups();
        
        // 문자열 치환 테스트
        StringReplacementParser replaceParser = new StringReplacementParser("test_data.txt");
        
        System.out.println("\n=== 전화번호 형식 변경 ===");
        replaceParser.replacePhoneNumberFormat().forEach(System.out::println);
        
        System.out.println("\n=== 이메일 마스킹 ===");
        replaceParser.maskEmails().forEach(System.out::println);
        
        System.out.println("\n=== 날짜 형식 변경 ===");
        replaceParser.formatDates().forEach(System.out::println);
    }
    
    // 테스트용 파일 생성
    private static void createTestFile() {
        try (PrintWriter writer = new PrintWriter("test_data.txt")) {
            writer.println("홍길동의 이메일: hong@example.com");
            writer.println("전화번호: 010-1234-5678");
            writer.println("가입일: 2024-01-15");
            writer.println("서버 IP: 192.168.1.100");
            writer.println("웹사이트: https://www.example.com");
            writer.println("김철수 회원님의 결제 금액: ₩50,000");
            writer.println("로그 시간: 2024-02-20 14:30:25");
            writer.println("관리자 이메일: admin@company.co.kr");
            writer.println("긴급 연락처: 010-9876-5432");
            writer.println("데이터베이스 주소: 10.0.0.1");
            writer.println("박영희님의 주문 금액: ₩1,200,000");
            writer.println("API 엔드포인트: https://api.service.com/v1/users");
        } catch (IOException e) {
            System.err.println("테스트 파일 생성 오류: " + e.getMessage());
        }
    }
}