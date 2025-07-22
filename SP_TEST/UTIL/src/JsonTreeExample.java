import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonTreeExample {
    public static void main(String[] args) {
        String json = "{\"id\":1,\"students\":[\"Anna\",\"Jerry\"],\"subject\":{\"name\":\"Java\",\"professor\":\"Tony\"}}";
        JsonElement element = JsonParser.parseString(json);
        JsonObject object = element.getAsJsonObject();
        long id = object.get("id").getAsLong();
        System.out.println("id: " + id);
        JsonArray students = object.get("students").getAsJsonArray();
        for (JsonElement stu : students) {
            System.out.println(stu.getAsString());
        }
        JsonObject subject = object.get("subject").getAsJsonObject();
        System.out.println(subject.get("name").getAsString());
    }
}