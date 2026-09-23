package http;

import http.exception.BadRequestException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import router.Router;

public class HttpServer {

  public void start(int port, Router router) throws IOException {
    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println("서버 실행 http://localhost:" + port);

    while (true) {
      Socket socket = serverSocket.accept();
      OutputStream out = socket.getOutputStream();
      try {
        HttpRequest request = HttpRequestParser.parse(socket.getInputStream());
        String response = getResponse(request, router);
        out.write(
            response.getBytes(StandardCharsets.UTF_8)
        );

      } catch (BadRequestException e) {
        out.write(
            HttpResponse.text(HttpStatus.BAD_REQUEST, e.getMessage()).toHttpString().getBytes(
                StandardCharsets.UTF_8)
        );
      }

      out.flush();
      socket.close();
    }
  }

  private static String getResponse(HttpRequest request, Router router) {
    HttpResponse response = null;
    if (request == null) {
      throw new BadRequestException("request가 불완전합니다.");
    }

    try {
      Router.RouteMatch routeMatch =
          router.find(
              request.method(),
              request.path()
          );
      if (routeMatch == null) {
        response = HttpResponse.text(
            HttpStatus.NOT_FOUND,
            "Not Found"
        );
      } else {
        response = routeMatch.handler()
            .handle(request, routeMatch.pathValues(), request.queryParams());
      }
    } catch (BadRequestException e) {
      response = HttpResponse.text(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (Exception e) {
      response = HttpResponse.text(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    return response.toHttpString();
  }
}

