import java.io.*;
import java.net.*;

public class Main{
    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8080);

        System.out.println("서버 실행 http://localhost:8080");

        while (true) {

            Socket socket = serverSocket.accept();

            socket.

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );

            String line;

            while ((line = reader.readLine()) != null) {

                System.out.println(line);

                if (line.isEmpty()) {
                    break;
                }
            }

            String body = "Hello Java";

            String response =
                    "HTTP/1.1 201 OK\r\n" +
                            "Content-Type: text/plain; charset=UTF-8\r\n" +
                            "Content-Length: " +
                            body.getBytes().length +
                            "\r\n" +
                            "\r\n" +
                            body;

            OutputStream out =
                    socket.getOutputStream();

            out.write(response.getBytes());

            out.flush();

            socket.close();
        }
    }
}