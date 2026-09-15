package http;

import http.exception.BadRequestException;
import router.Router;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpServer {

    public void start(int port, Router router) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("서버 실행 http://localhost:" + port);

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
        HttpResponse response = null;

        try {
            Router.PatchValueDTO handler =
                    router.find(
                            request.method(),
                            request.path()
                    );
            if (handler.getHandler() == null) {
                response = HttpResponse.text(
                        HttpStatus.NOT_FOUND,
                        "Not Found"
                );
            } else {
                response = handler.getHandler().handle(request, handler.getValue());
            }
        } catch (BadRequestException e) {
            response = HttpResponse.text(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            response = HttpResponse.text(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }

        return response.toHttpString();
    }
}

