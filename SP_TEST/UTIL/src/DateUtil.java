import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class DateUtil {
    
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 현재 날짜를 문자열로 반환
     */
    public static String getCurrentDateString() {
        return LocalDate.now().format(DEFAULT_FORMATTER);
    }
    
    /**
     * 현재 날짜시간을 문자열로 반환
     */
    public static String getCurrentDateTimeString() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }
    
    /**
     * 두 날짜 사이의 일수 계산
     */
    public static long getDaysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    /**
     * 나이 계산
     */
    public static int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    
    /**
     * 해당 월의 첫 번째 날
     */
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }
    
    /**
     * 해당 월의 마지막 날
     */
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }
    
    /**
     * 다음 월요일 찾기
     */
    public static LocalDate getNextMonday(LocalDate date) {
        return date.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    }
    
    /**
     * 평일인지 확인
     */
    public static boolean isWeekday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }
    
    /**
     * 업무일 추가 (주말 제외)
     */
    public static LocalDate addBusinessDays(LocalDate date, int days) {
        LocalDate result = date;
        int addedDays = 0;
        
        while (addedDays < days) {
            result = result.plusDays(1);
            if (isWeekday(result)) {
                addedDays++;
            }
        }
        
        return result;
    }
    
    /**
     * 타임스탬프 생성
     */
    public static long getCurrentTimestamp() {
        return Instant.now().getEpochSecond();
    }
    
    /**
     * 타임스탬프를 날짜로 변환
     */
    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }
    
    // 테스트 메서드
    public static void main(String[] args) {
        System.out.println("현재 날짜: " + getCurrentDateString());
        System.out.println("현재 날짜시간: " + getCurrentDateTimeString());
        
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(1990, 5, 15);
        
        System.out.println("나이: " + calculateAge(birthday) + "세");
        System.out.println("이번 달 첫날: " + getFirstDayOfMonth(today));
        System.out.println("이번 달 마지막날: " + getLastDayOfMonth(today));
        System.out.println("다음 월요일: " + getNextMonday(today));
        System.out.println("오늘은 평일인가? " + isWeekday(today));
        System.out.println("5 업무일 후: " + addBusinessDays(today, 5));
        
        long timestamp = getCurrentTimestamp();
        System.out.println("현재 타임스탬프: " + timestamp);
        System.out.println("타임스탬프를 날짜로: " + timestampToLocalDateTime(timestamp));
    }
}