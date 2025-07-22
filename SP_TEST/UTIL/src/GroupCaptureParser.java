import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupCaptureParser extends RegexFileParser {
    
    public GroupCaptureParser(String fileName) {
        super(fileName);
    }
    
    // 날짜를 년, 월, 일로 분리
    public void parseDatesWithGroups() {
        Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Matcher matcher = pattern.matcher(line);
            
            while (matcher.find()) {
                System.out.println("줄 " + (i + 1) + ":");
                System.out.println("  전체 날짜: " + matcher.group(0));
                System.out.println("  년: " + matcher.group(1));
                System.out.println("  월: " + matcher.group(2));
                System.out.println("  일: " + matcher.group(3));
                System.out.println();
            }
        }
    }
    
    // 이메일을 사용자명과 도메인으로 분리
    public void parseEmailsWithGroups() {
        Pattern pattern = Pattern.compile("\\b([A-Za-z0-9._%+-]+)@([A-Za-z0-9.-]+\\.[A-Z|a-z]{2,})\\b");
        
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Matcher matcher = pattern.matcher(line);
            
            while (matcher.find()) {
                System.out.println("줄 " + (i + 1) + ":");
                System.out.println("  전체 이메일: " + matcher.group(0));
                System.out.println("  사용자명: " + matcher.group(1));
                System.out.println("  도메인: " + matcher.group(2));
                System.out.println();
            }
        }
    }
    
    // 전화번호를 지역번호, 중간번호, 뒷번호로 분리
    public void parsePhoneNumbersWithGroups() {
        Pattern pattern = Pattern.compile("\\b(\\d{3})-(\\d{4})-(\\d{4})\\b");
        
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Matcher matcher = pattern.matcher(line);
            
            while (matcher.find()) {
                System.out.println("줄 " + (i + 1) + ":");
                System.out.println("  전체 전화번호: " + matcher.group(0));
                System.out.println("  지역번호: " + matcher.group(1));
                System.out.println("  중간번호: " + matcher.group(2));
                System.out.println("  뒷번호: " + matcher.group(3));
                System.out.println();
            }
        }
    }
}