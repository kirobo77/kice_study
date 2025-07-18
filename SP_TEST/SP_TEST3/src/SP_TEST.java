import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SP_TEST {
	private static final Map<String, String> dictionary = new HashMap<>();
	private static final Set<String> stopwords = new HashSet<>();
	private static final List<ModelInfo> models = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		loadDictionary("DICTIONARY.TXT");
		loadStopwords("STOPWORD.TXT");
		loadModels("MODELS.JSON");

		Server server = new Server(8080);
		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(MainServlet.class, "/");
		server.setHandler(handler);
		server.start();
		server.join();
	}

	// 모델 정보 클래스
	public static class ModelInfo {
		String modelname;
		String url;
		List<ClassInfo> classes;
	}

	public static class ClassInfo {
		String code;
		String value;
	}

	// 메인 서블릿
	public static class MainServlet extends HttpServlet {
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
			Gson gson = new Gson();
			JsonObject requestJson = gson.fromJson(new InputStreamReader(req.getInputStream()), JsonObject.class);
			String modelName = requestJson.get("modelname").getAsString();
			JsonArray queries = requestJson.getAsJsonArray("queries");

			ModelInfo model = models.stream().filter(m -> m.modelname.equals(modelName)).findFirst().orElse(null);
			if (model == null) {
				resp.setStatus(400);
				resp.getWriter().write("{\"error\":\"Model not found\"}");
				return;
			}

			List<String> results = new ArrayList<>();
			for (JsonElement queryElem : queries) {
				String query = queryElem.getAsString();
				String processed = preprocess(query);
				String code = requestModel(model.url, processed);
				String value = model.classes.stream().filter(c -> c.code.equals(code)).map(c -> c.value).findFirst()
						.orElse("unknown");
				results.add(value);
			}
			JsonObject responseJson = new JsonObject();
			JsonArray resArr = new JsonArray();
			for (String r : results)
				resArr.add(r);
			responseJson.add("results", resArr);

			resp.setContentType("application/json");
			resp.getWriter().write(gson.toJson(responseJson));
		}

		// 문장 전처리 (토큰화, 임베딩, 불용어 제거)
		private String preprocess(String sentence) {
			String[] tokens = sentence.trim().split("\\s+");
			List<String> vectors = new ArrayList<>();
			for (String token : tokens) {
				String key = token.toLowerCase();
				String vector = dictionary.get(key);
				if (vector != null && !stopwords.contains(vector)) {
					vectors.add(vector);
				}
			}
			return String.join(" ", vectors);
		}

		// 모델 서버에 HTTP POST 요청 (Jetty 9 HttpClient)
		private String requestModel(String url, String processed) {
			Gson gson = new Gson(); // Gson 인스턴스
			HttpClient httpClient = new HttpClient(); // Jetty HttpClient 생성
			try {
				// HttpClient 시작
				httpClient.start();
				// JSON 바디 생성
				String json = String.format("{\"query\":\"%s\"}", processed);

				// POST 요청 생성 및 전송
				ContentResponse response = httpClient.POST(url).header(HttpHeader.CONTENT_TYPE, "application/json") // Content-Type
																													// 지정
						.content(new StringContentProvider(json), "application/json") // JSON 바디 설정
						.send(); // 동기 전송

				// 응답 코드 및 바디 처리
				String responseBody = response.getContentAsString();
				JsonObject res = gson.fromJson(responseBody, JsonObject.class);
				httpClient.stop(); // HttpClient 종료 (실전에서는 재사용 권장)
				return res.get("result").getAsString(); // 결과 추출
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	// 사전, 불용어, 모델 로드 메소드 (문항1-2와 동일)
    private static void loadDictionary(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("#");
                if (parts.length == 2) {
                    dictionary.put(parts[0], parts[1]);
                }
            }
        }
    }

    private static void loadStopwords(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                stopwords.add(line.trim());
            }
        }
    }

	private static void loadModels(String path) throws IOException {
		Gson gson = new Gson();
		try (Reader reader = new FileReader(path)) {
			JsonObject obj = gson.fromJson(reader, JsonObject.class);
			JsonArray arr = obj.getAsJsonArray("models");
			for (JsonElement e : arr) {
				models.add(gson.fromJson(e, ModelInfo.class));
			}
		}
	}
}