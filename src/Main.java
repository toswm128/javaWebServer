import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class Main {

    private static final Router router = new Router();

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8080);

        System.out.println("서버 실행 http://localhost:8080");

        router.get("/hello", () -> {
            System.out.println("Hello 받음");
            return "Hello";
        });
        router.get("/users", () -> "User List");
        router.get("/info", () -> "당신의 정보입니다.");

        while (true) {

            Socket socket = serverSocket.accept();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );

            String requestLine = reader.readLine();

            if (requestLine == null) {
                socket.close();
                continue;
            }

            System.out.println("Request: " + requestLine);

            String[] parts = requestLine.split(" ");

            String method = parts[0];
            String path = parts[1];
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
            }

            String response = getResponse(method, path);

            OutputStream out = socket.getOutputStream();

            out.write(
                    response.getBytes(StandardCharsets.UTF_8)
            );

            out.flush();

            socket.close();
        }
    }

    private static String getResponse(String method, String path) {

        Handler handler = router.find(path);

        String status;
        String body;

        if (handler == null) {
            status = "404 Not Found";
            body = "404 Not Found";
        } else {
            status = "200 OK";
            body = handler.handle();
        }


        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        return "HTTP/1.1 " + status + "\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n" +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "\r\n" + body;
    }
}