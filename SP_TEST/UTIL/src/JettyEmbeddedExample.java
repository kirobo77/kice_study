import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import javax.servlet.http.*;
import javax.servlet.*;

public class JettyEmbeddedExample {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080); // 8080 포트로 Jetty 서버 인스턴스 생성

        ServletHandler handler = new ServletHandler(); // 서블릿 핸들러 준비
        handler.addServletWithMapping(HelloServlet.class, "/hello"); // "/hello" 경로에 서블릿 매핑
        server.setHandler(handler); // 서버에 핸들러 등록

        server.start(); // 서버 시작
        server.join();  // 메인 스레드 대기(서버 종료까지)
    }

    // 간단한 Hello 서블릿 구현
    public static class HelloServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException {
            resp.setContentType("text/plain; charset=utf-8");
            resp.getWriter().write("[GET]Hello, Jetty Embedded!");
        }
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException {
            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write("[POST]Hello, Jetty Embedded!");
        }        
    }
}