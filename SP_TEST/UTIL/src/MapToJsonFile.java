import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class MapToJsonFile {
    public static void main(String[] args) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("city", "Seoul");
        map.put("country", "Korea");

        Gson gson = new Gson();
        try (Writer writer = new FileWriter("data.json")) {
            gson.toJson(map, writer); // Map을 JSON 형식으로 저장
        }
        System.out.println("JSON 파일 저장 완료!");
    }
}