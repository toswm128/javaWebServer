import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8080);

        System.out.println("서버 실행 http://localhost:8080");

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

            String body;
            String status;

            if (method.equals("GET") && path.equals("/hello")) {
                status = "200 OK";
                body = "Hello";

            } else if (method.equals("GET") && path.equals("/users")) {
                status = "200 OK";
                body = "User List";
            } else {
                status = "404 Not Found";
                body = "Not Found";
            }

            byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

            String response =
                    "HTTP/1.1 " + status + "\r\n" +
                            "Content-Type: text/plain; charset=UTF-8\r\n" +
                            "Content-Length: " + bodyBytes.length + "\r\n" +
                            "\r\n" + body;

            OutputStream out = socket.getOutputStream();

            out.write(
                    response.getBytes(StandardCharsets.UTF_8)
            );

            out.flush();

            socket.close();
        }
    }
}