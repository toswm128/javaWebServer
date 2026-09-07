import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class Main {

    private static final Router router = new Router();

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8080);


        System.out.println("서버 실행 http://localhost:8080");

        router.get("/hello", request -> {
            System.out.println("Hello 받음");
            return "Hello";
        });
        router.get("/users", request -> "User List");
        router.get("/info", request -> "당신의 정보입니다.");
        router.post("/users", request -> {
            System.out.println(request);
            return "데이터가 추가되었습니다.";
        });

        while (true) {

            Socket socket = serverSocket.accept();

            InputStream input = socket.getInputStream();

            String headers = readHeaders(input);

            String[] lines = headers.split("\r\n");


            String[] requestParts = lines[0].split(" ");

            Map<String, String> headerData = new HashMap<>();

            String method = requestParts[0];
            String path = requestParts[1];
            String line;

            int contentLength = 0;

            for (String header : lines) {
                String[] headerSplit = header.split(":");
                if (!headerSplit[0].isEmpty()) {
                    headerData.put(headerSplit[0], header.substring(headerSplit[0].length()).trim());
                }
            }


            byte[] bodyBytes =
                    input.readNBytes(contentLength);

            String requestBody =
                    new String(
                            bodyBytes,
                            StandardCharsets.UTF_8
                    );

            System.out.println("method = " + method);
            System.out.println("path = " + path);
            System.out.println("body = " + requestBody);

            HttpRequest request =
                    new HttpRequest(
                            method,
                            path,
                            headerData,
                            requestBody
                    );

            String response = getResponse(request);

            OutputStream out = socket.getOutputStream();

            out.write(
                    response.getBytes(StandardCharsets.UTF_8)
            );

            out.flush();

            socket.close();
        }
    }

    private static String readHeaders(InputStream input) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int b;

        while ((b = input.read()) != -1) {

            buffer.write(b);

            byte[] data = buffer.toByteArray();
            int length = data.length;

            if (length >= 4 &&
                    data[length - 4] == '\r' &&
                    data[length - 3] == '\n' &&
                    data[length - 2] == '\r' &&
                    data[length - 1] == '\n') {

                break;
            }
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static String getResponse(HttpRequest request) {

        Handler handler =
                router.find(
                        request.method(),
                        request.path()
                );

        String status;
        String body;

        if (handler == null) {
            status = "404 Not Found";
            body = "404 Not Found";
        } else {
            status = "200 OK";
            body = handler.handle(request);
        }

        byte[] bodyBytes =
                body.getBytes(StandardCharsets.UTF_8);

        return "HTTP/1.1 " + status + "\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n" +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "\r\n" +
                body;
    }
}