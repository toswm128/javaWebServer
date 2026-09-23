import http.HttpServer;
import router.Router;
import router.RouterConfig;


public class Main {

  public static void main(String[] args) throws Exception {
    Router router = new Router();
    RouterConfig.register(router);
    HttpServer httpServer = new HttpServer();
    httpServer.start(8080, router);

  }
}
