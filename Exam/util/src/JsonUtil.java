import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class JsonUtil {
	private static final Gson gson = new Gson();

	// 1. Json 문자열 내 특정 값 검색
	public static String findValue(String json, String key) {
		JsonElement element = JsonParser.parseString(json);
		return findValueRecursive(element, key);
	}

	private static String findValueRecursive(JsonElement element, String key) {
		if (element.isJsonObject()) {
			JsonObject obj = element.getAsJsonObject();
			if (obj.has(key)) {
				return obj.get(key).toString();
			}
			for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				String found = findValueRecursive(entry.getValue(), key);
				if (found != null)
					return found;
			}
		} else if (element.isJsonArray()) {
			for (JsonElement item : element.getAsJsonArray()) {
				String found = findValueRecursive(item, key);
				if (found != null)
					return found;
			}
		}
		return null;
	}

	// 2. Json 문자열 내 특정 값 치환
	public static String replaceValue(String json, String key, String newValue) {
		JsonElement element = JsonParser.parseString(json);
		replaceValueRecursive(element, key, newValue);
		return gson.toJson(element);
	}

	private static void replaceValueRecursive(JsonElement element, String key, String newValue) {
		if (element.isJsonObject()) {
			JsonObject obj = element.getAsJsonObject();
			if (obj.has(key)) {
				obj.addProperty(key, newValue);
			}
			for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				replaceValueRecursive(entry.getValue(), key, newValue);
			}
		} else if (element.isJsonArray()) {
			for (JsonElement item : element.getAsJsonArray()) {
				replaceValueRecursive(item, key, newValue);
			}
		}
	}

	// 3. Json 문자열을 객체로 변환
	public static <T> T fromJson(String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}

	// 4. Json 문자열을 Map으로 변환
	public static Map<String, Object> toMap(String json) {
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();
		return gson.fromJson(json, type);
	}

	// 5. 객체를 Json 문자열로 변환
	public static String toJson(Object obj) {
		return gson.toJson(obj);
	}

	// 6. 리스트 내의 Map 중에서 주어진 key를 포함하는 Map을 반환
	public static Map<?, ?> findMapByKey(List<Map<?, ?>> list, Object key) {
		if (list == null || key == null)
			return null;

		for (Map<?, ?> map : list) {
			if (map != null && map.containsKey(key)) {
				return map;
			}
		}
		return null; // 없으면 null 반환
	}
}