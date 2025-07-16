import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 날짜 포맷 변환 및 파싱 유틸리티 클래스
 */
public class DateFormatUtil {

    /**
     * LocalDate를 지정한 패턴의 문자열로 변환
     * @param date LocalDate 객체
     * @param pattern 날짜 포맷 패턴 (예: "yyyy-MM-dd")
     * @return 포맷된 문자열
     */
    public static String format(LocalDate date, String pattern) {
        if (date == null || pattern == null) return null;
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * LocalDateTime을 지정한 패턴의 문자열로 변환
     * @param dateTime LocalDateTime 객체
     * @param pattern 날짜/시간 포맷 패턴 (예: "yyyy-MM-dd HH:mm:ss")
     * @return 포맷된 문자열
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 문자열을 LocalDate로 파싱
     * @param dateStr 날짜 문자열
     * @param pattern 날짜 포맷 패턴
     * @return LocalDate 객체 (파싱 실패 시 null)
     */
    public static LocalDate parseToDate(String dateStr, String pattern) {
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 문자열을 LocalDateTime으로 파싱
     * @param dateTimeStr 날짜/시간 문자열
     * @param pattern 날짜/시간 포맷 패턴
     * @return LocalDateTime 객체 (파싱 실패 시 null)
     */
    public static LocalDateTime parseToDateTime(String dateTimeStr, String pattern) {
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 현재 날짜/시간을 지정한 패턴의 문자열로 반환
     * @param pattern 날짜/시간 포맷 패턴
     * @return 포맷된 현재 날짜/시간 문자열
     */
    public static String now(String pattern) {
        return format(LocalDateTime.now(), pattern);
    }
}
