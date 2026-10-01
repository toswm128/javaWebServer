package http;

import http.exception.BadRequestException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import router.Router;
import user.UserService.DataAccessException;
import user.UserService.NotFoundException;

public class HttpServer {

  public void start(int port, Router router) throws IOException {
    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println("서버 실행 http://localhost:" + port);

    ExecutorService executor = Executors.newFixedThreadPool(4);

    while (true) {
      Socket socket = serverSocket.accept();
      Runnable job = () -> {
        handleClient(socket, router);
      };
      executor.execute(job);
      Integer.toBinaryString(16);
    }
  }


  private static void handleClient(Socket socket, Router router) {
    try (socket) {
      try {
        HttpRequest request = HttpRequestParser.parse(socket.getInputStream());
        if (request == null) {
          return;
        }

        String response = getResponse(request, router);

        OutputStream out = socket.getOutputStream();

        out.write(
            response.getBytes(StandardCharsets.UTF_8)
        );

        out.flush();

      } catch (BadRequestException e) {
        OutputStream out = socket.getOutputStream();
        out.write(
            HttpResponse.text(HttpStatus.BAD_REQUEST, e.getMessage()).toHttpString().getBytes(
                StandardCharsets.UTF_8)
        );

        out.flush();

      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String getResponse(HttpRequest request, Router router) {
    HttpResponse response = null;
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
            .handle(request, routeMatch.pathValues());
      }
    } catch (BadRequestException e) {
      response = HttpResponse.text(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (NotFoundException e) {
      response = HttpResponse.text(
          HttpStatus.NOT_FOUND, e.getMessage()
      );
    } catch (DataAccessException e) {
      response = HttpResponse.text(
          HttpStatus.INTERNAL_SERVER_ERROR, "예기치 않은 오류"
      );
    } catch (Exception e) {
      response = HttpResponse.text(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    return response.toHttpString();
  }
}

