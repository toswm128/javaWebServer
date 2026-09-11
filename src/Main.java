import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {

    private static final Router router = new Router();

    public static List<User> userList = new ArrayList<>(Arrays.asList(
            new User(1, "조민수"),
            new User(2, "황제원"),
            new User(3, "서은건")
    ));


    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("서버 실행 http://localhost:8080");

        router.get("/users", request -> {
            StringBuilder body = new StringBuilder();
            for (User i : userList) {
                body.append(i.getUserName())
                        .append("\r\n");
            }
            return HttpResponse.text(HttpStatus.OK, body.toString());
        });

        router.post("/users", request -> {
            userList.add(new User(userList.size() + 1, request.body().body()));
            StringBuilder body = new StringBuilder();
            for (User i : userList) {
                body.append(i.getUserName()).append("\r\n");
            }
            return HttpResponse.text(HttpStatus.CREATED, body.toString());
        });

        while (true) {
            Socket socket = serverSocket.accept();
            InputStream input = socket.getInputStream();

            HttpHeaders headers = HttpRequestParser.parseHeader(input);
            HttpBody requestBody = HttpRequestParser.parseBody(input);
            String method = HttpRequestParser.getMethod();
            String path = HttpRequestParser.getPath();
            HttpRequest request = new HttpRequest(method, path, headers, requestBody);

            String response = getResponse(request);

            OutputStream out = socket.getOutputStream();

            out.write(
                    response.getBytes(StandardCharsets.UTF_8)
            );

            out.flush();

            socket.close();
        }
    }

    private static String getResponse(HttpRequest request) {
//      client에게 받은 메소드, 주소를 handler에 저장
        Handler handler =
                router.find(
                        request.method(),
                        request.path()
                );


        HttpResponse response = null;
        if (handler == null) {
            response = HttpResponse.text(HttpStatus.NOT_FOUND, "");
        } else {
            response = handler.handle(request);

        }


        System.out.println(response.toHttpString());
        return response.toHttpString();
    }
}