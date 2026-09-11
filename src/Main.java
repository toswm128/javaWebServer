import HTTP.*;
import Router.Router;
import Router.RouterConfig;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class Main {


    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("서버 실행 http://localhost:8080");
        Router router = new Router();
        RouterConfig.register(router);

        while (true) {
            Socket socket = serverSocket.accept();
            HttpRequest request = HttpRequestParser.parse(socket.getInputStream());

            String response = getResponse(request, router);

            OutputStream out = socket.getOutputStream();

            out.write(
                    response.getBytes(StandardCharsets.UTF_8)
            );

            out.flush();

            socket.close();
        }
    }

    private static String getResponse(HttpRequest request, Router router) {
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