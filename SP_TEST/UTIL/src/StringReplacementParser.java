import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StringReplacementParser extends RegexFileParser {
    
    public StringReplacementParser(String fileName) {
        super(fileName);
    }
    
    // 전화번호 형식 변경 (010-1234-5678 → 010.1234.5678)
    public List<String> replacePhoneNumberFormat() {
        List<String> replacedLines = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d{3})-(\\d{4})-(\\d{4})");
        
        for (String line : lines) {
            String replaced = pattern.matcher(line).replaceAll("$1.$2.$3");
            replacedLines.add(replaced);
        }
        return replacedLines;
    }
    
    // 이메일 마스킹 (test@example.com → t***@example.com)
    public List<String> maskEmails() {
        List<String> maskedLines = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b([A-Za-z0-9._%+-])[A-Za-z0-9._%+-]*@([A-Za-z0-9.-]+\\.[A-Z|a-z]{2,})\\b");
        
        for (String line : lines) {
            String masked = pattern.matcher(line).replaceAll("$1***@$2");
            maskedLines.add(masked);
        }
        return maskedLines;
    }
    
    // 날짜 형식 변경 (2024-01-15 → 2024년 1월 15일)
    public List<String> formatDates() {
        List<String> formattedLines = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        
        for (String line : lines) {
            String formatted = pattern.matcher(line).replaceAll("$1년 $2월 $3일");
            formattedLines.add(formatted);
        }
        return formattedLines;
    }
}