import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MapToPropertiesFile {
    public static void main(String[] args) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("username", "admin");
        map.put("password", "1234");

        Properties props = new Properties();
        props.putAll(map);

        try (FileOutputStream fos = new FileOutputStream("config.properties")) {
            props.store(fos, "설정 정보");
        }
        System.out.println("Properties 파일 저장 완료!");
    }
}