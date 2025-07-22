import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class CalendarUtil {
    
    /**
     * 특정 월의 모든 날짜를 반환
     */
    public static List<LocalDate> getDatesInMonth(int year, int month) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.with(TemporalAdjusters.lastDayOfMonth());
        
        LocalDate current = firstDay;
        while (!current.isAfter(lastDay)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        
        return dates;
    }
    
    /**
     * 특정 월의 평일만 반환
     */
    public static List<LocalDate> getWeekdaysInMonth(int year, int month) {
        return getDatesInMonth(year, month).stream()
            .filter(date -> date.getDayOfWeek() != DayOfWeek.SATURDAY && 
                           date.getDayOfWeek() != DayOfWeek.SUNDAY)
            .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
    }
    
    /**
     * 특정 월의 주말만 반환
     */
    public static List<LocalDate> getWeekendsInMonth(int year, int month) {
        return getDatesInMonth(year, month).stream()
            .filter(date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || 
                           date.getDayOfWeek() == DayOfWeek.SUNDAY)
            .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
    }
    
    /**
     * 특정 월의 특정 요일들을 반환
     */
    public static List<LocalDate> getSpecificDayOfWeekInMonth(int year, int month, DayOfWeek dayOfWeek) {
        return getDatesInMonth(year, month).stream()
            .filter(date -> date.getDayOfWeek() == dayOfWeek)
            .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
    }
    
    /**
     * 분기별 첫 번째 날과 마지막 날 반환
     */
    public static LocalDate[] getQuarterRange(int year, int quarter) {
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("분기는 1-4 사이여야 합니다.");
        }
        
        int startMonth = (quarter - 1) * 3 + 1;
        LocalDate startDate = LocalDate.of(year, startMonth, 1);
        LocalDate endDate = startDate.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
        
        return new LocalDate[]{startDate, endDate};
    }
    
    /**
     * 해당 연도의 모든 월 이름 반환
     */
    public static List<String> getMonthNames() {
        List<String> months = new ArrayList<>();
        for (Month month : Month.values()) {
            months.add(month.toString());
        }
        return months;
    }
    
    /**
     * 윤년 여부 확인
     */
    public static boolean isLeapYear(int year) {
        return Year.isLeap(year);
    }
    
    /**
     * 특정 년도의 총 일수 반환
     */
    public static int getDaysInYear(int year) {
        return Year.of(year).length();
    }
    
    public static void main(String[] args) {
        int year = 2024;
        int month = 12;
        
        // 해당 월의 모든 날짜
        List<LocalDate> allDates = getDatesInMonth(year, month);
        System.out.println(year + "년 " + month + "월의 총 일수: " + allDates.size());
        
        // 평일과 주말 개수
        List<LocalDate> weekdays = getWeekdaysInMonth(year, month);
        List<LocalDate> weekends = getWeekendsInMonth(year, month);
        
        System.out.println("평일 개수: " + weekdays.size());
        System.out.println("주말 개수: " + weekends.size());
        
        // 특정 요일들
        List<LocalDate> mondays = getSpecificDayOfWeekInMonth(year, month, DayOfWeek.MONDAY);
        System.out.println("월요일들: " + mondays);
        
        // 분기별 범위
        for (int quarter = 1; quarter <= 4; quarter++) {
            LocalDate[] range = getQuarterRange(year, quarter);
            System.out.println(quarter + "분기: " + range[0] + " ~ " + range[1]);
        }
        
        // 월 이름들
        System.out.println("월 이름들: " + getMonthNames());
        
        // 윤년 여부
        System.out.println(year + "년은 윤년인가? " + isLeapYear(year));
        System.out.println(year + "년의 총 일수: " + getDaysInYear(year));
    }
}