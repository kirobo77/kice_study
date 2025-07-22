import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidationUtil {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * 날짜 문자열 유효성 검증
     */
    public static boolean isValidDate(String dateString) {
        try {
            LocalDate.parse(dateString, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * 날짜가 특정 범위 내에 있는지 검증
     */
    public static boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    
    /**
     * 미래 날짜인지 확인
     */
    public static boolean isFutureDate(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }
    
    /**
     * 과거 날짜인지 확인
     */
    public static boolean isPastDate(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }
    
    /**
     * 18세 이상인지 확인
     */
    public static boolean isAdult(LocalDate birthDate) {
        return calculateAge(birthDate) >= 18;
    }
    
    private static int calculateAge(LocalDate birthDate) {
        return LocalDate.now().getYear() - birthDate.getYear();
    }
    
    public static void main(String[] args) {
        // 날짜 유효성 검증 테스트
        String[] testDates = {"2024-02-29", "2024-13-01", "2024-02-30", "2024-12-25"};
        
        for (String dateString : testDates) {
            System.out.println(dateString + " 유효한가? " + isValidDate(dateString));
        }
        
        // 날짜 범위 검증
        LocalDate testDate = LocalDate.of(2024, 6, 15);
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        
        System.out.println("날짜 범위 내에 있는가? " + isDateInRange(testDate, startDate, endDate));
        
        // 미래/과거 날짜 검증
        LocalDate futureDate = LocalDate.now().plusDays(30);
        LocalDate pastDate = LocalDate.now().minusDays(30);
        
        System.out.println("미래 날짜인가? " + isFutureDate(futureDate));
        System.out.println("과거 날짜인가? " + isPastDate(pastDate));
        
        // 성인 여부 확인
        LocalDate adultBirthDate = LocalDate.of(1990, 1, 1);
        LocalDate minorBirthDate = LocalDate.of(2010, 1, 1);
        
        System.out.println("성인인가? " + isAdult(adultBirthDate));
        System.out.println("미성년자인가? " + !isAdult(minorBirthDate));
    }
}