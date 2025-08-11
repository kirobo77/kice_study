import com.google.gson.Gson;
import java.util.List;

// Subject 클래스 정의 (JSON의 subject 객체와 매핑)
class Subject {
    private String name;
    private String professor;
    
    // Getter 메서드들
    public String getName() {
        return name;
    }
    
    public String getProfessor() {
        return professor;
    }
}

// StudentInfo 클래스 정의 (전체 JSON 구조와 매핑)
class StudentInfo {
    private long id;
    private List<String> students;
    private Subject subject;
    
    // Getter 메서드들
    public long getId() {
        return id;
    }
    
    public List<String> getStudents() {
        return students;
    }
    
    public Subject getSubject() {
        return subject;
    }
}

public class JsonToObjectExample {
    public static void main(String[] args) {
        // 원본 JSON 문자열 그대로 유지
        String json = "{\"id\":1,\"students\":[\"Anna\",\"Jerry\"],\"subject\":{\"name\":\"Java\",\"professor\":\"Tony\"}}";
        
        // Gson을 사용하여 JSON을 Java 객체로 변환
        Gson gson = new Gson();
        StudentInfo studentInfo = gson.fromJson(json, StudentInfo.class);
        
        // 원본 코드와 동일한 출력 순서로 데이터 출력
        long id = studentInfo.getId();
        System.out.println("id: " + id);
        
        List<String> students = studentInfo.getStudents();
        
        // 학생 이름들 출력 (원본 코드의 for 루프와 동일)
        for (String stu : students) {
            System.out.println(stu);
        }
        
        // 과목명 출력 (원본 코드의 마지막 출력과 동일)
        System.out.println(studentInfo.getSubject().getName());
    }
}