import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;

public class JettyHttpClientExample {
    public static void main(String[] args) throws Exception {
        HttpClient httpClient = new HttpClient(); // HttpClient 인스턴스 생성
        httpClient.start(); // 내부 리소스 초기화

        // 전송할 JSON 데이터 생성
        String processed = "sample query";
        String json = String.format("{\"query\":\"%s\"}", processed);

        // POST 요청 생성 및 전송
        ContentResponse response = httpClient.POST("http://localhost:8080/hello") // 요청 URL
            .header(HttpHeader.CONTENT_TYPE, "application/json")                  // Content-Type 헤더 지정
            .content(new StringContentProvider(json), "application/json")          // 요청 바디에 JSON 데이터 설정
            .send();                                                              // 동기 요청

        String responseBody = response.getContentAsString(); // 응답 본문 추출
        System.out.println("서버 응답: " + responseBody);

        httpClient.stop(); // 리소스 반환
    }
}