import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternParser extends RegexFileParser {
    
    public PatternParser(String fileName) {
        super(fileName);
    }
    
    // 이메일 추출
    public List<String> extractEmails() {
        List<String> emails = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
        
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                emails.add(matcher.group());
            }
        }
        return emails;
    }
    
    // 전화번호 추출
    public List<String> extractPhoneNumbers() {
        List<String> phones = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b\\d{3}-\\d{4}-\\d{4}\\b");
        
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                phones.add(matcher.group());
            }
        }
        return phones;
    }
    
    // 날짜 추출
    public List<String> extractDates() {
        List<String> dates = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                dates.add(matcher.group());
            }
        }
        return dates;
    }
    
    // IP 주소 추출
    public List<String> extractIpAddresses() {
        List<String> ips = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b");
        
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                ips.add(matcher.group());
            }
        }
        return ips;
    }
    
    // URL 추출
    public List<String> extractUrls() {
        List<String> urls = new ArrayList<>();
        Pattern pattern = Pattern.compile("https?://[^\\s]+");
        
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                urls.add(matcher.group());
            }
        }
        return urls;
    }
    
    // 한글 이름 추출
    public List<String> extractKoreanNames() {
        List<String> names = new ArrayList<>();
        Pattern pattern = Pattern.compile("[가-힣]{2,4}");
        
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                names.add(matcher.group());
            }
        }
        return names;
    }
    
    // 금액 추출
    public List<String> extractPrices() {
        List<String> prices = new ArrayList<>();
        Pattern pattern = Pattern.compile("₩[\\d,]+");
        
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                prices.add(matcher.group());
            }
        }
        return prices;
    }
    
}