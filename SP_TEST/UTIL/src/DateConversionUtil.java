import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateConversionUtil {
    
    /**
     * LocalDate를 Date로 변환
     */
    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * Date를 LocalDate로 변환
     */
    public static LocalDate dateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    /**
     * LocalDateTime을 Date로 변환
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * Date를 LocalDateTime으로 변환
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    /**
     * LocalDateTime을 Timestamp로 변환
     */
    public static long localDateTimeToTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }
    
    /**
     * Timestamp를 LocalDateTime으로 변환
     */
    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }
    
    /**
     * 문자열을 LocalDate로 변환 (여러 포맷 지원)
     */
    public static LocalDate parseDate(String dateString) {
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy")
        };
        
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateString, formatter);
            } catch (Exception e) {
                // 다음 포맷터 시도
            }
        }
        
        throw new IllegalArgumentException("지원되지 않는 날짜 형식: " + dateString);
    }
    
    /**
     * LocalDate를 다양한 형식의 문자열로 변환
     */
    public static String formatDate(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    public static void main(String[] args) {
        // 변환 테스트
        LocalDate localDate = LocalDate.of(2024, 12, 25);
        LocalDateTime localDateTime = LocalDateTime.of(2024, 12, 25, 14, 30, 45);
        
        // LocalDate <-> Date
        Date dateFromLocal = localDateToDate(localDate);
        LocalDate localFromDate = dateToLocalDate(dateFromLocal);
        
        System.out.println("LocalDate -> Date: " + dateFromLocal);
        System.out.println("Date -> LocalDate: " + localFromDate);
        
        // LocalDateTime <-> Date
        Date dateFromDateTime = localDateTimeToDate(localDateTime);
        LocalDateTime dateTimeFromDate = dateToLocalDateTime(dateFromDateTime);
        
        System.out.println("LocalDateTime -> Date: " + dateFromDateTime);
        System.out.println("Date -> LocalDateTime: " + dateTimeFromDate);
        
        // Timestamp 변환
        long timestamp = localDateTimeToTimestamp(localDateTime);
        LocalDateTime dateTimeFromTimestamp = timestampToLocalDateTime(timestamp);
        
        System.out.println("LocalDateTime -> Timestamp: " + timestamp);
        System.out.println("Timestamp -> LocalDateTime: " + dateTimeFromTimestamp);
        
        // 문자열 파싱 테스트
        String[] testDates = {"2024-12-25", "2024/12/25", "25-12-2024", "25/12/2024"};
        
        for (String dateString : testDates) {
            try {
                LocalDate parsed = parseDate(dateString);
                System.out.println("파싱된 날짜 (" + dateString + "): " + parsed);
            } catch (Exception e) {
                System.out.println("파싱 실패: " + dateString);
            }
        }
        
        // 포맷팅 테스트
        String[] patterns = {
            "yyyy-MM-dd",
            "yyyy년 MM월 dd일",
            "MMM dd, yyyy",
            "E, MMM dd yyyy"
        };
        
        for (String pattern : patterns) {
            System.out.println("포맷 (" + pattern + "): " + formatDate(localDate, pattern));
        }
    }
}