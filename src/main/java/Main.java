import http.HttpServer;
import router.Router;
import router.RouterConfig;
import user.UserRepository;
import user.UserRoutes;
import user.UserService;


void main() throws Exception {
  Router router = new Router();
  RouterConfig routerConfig = new RouterConfig(
      new UserRoutes(new UserService(new UserRepository())));
  routerConfig.register(router);
  HttpServer httpServer = new HttpServer();
  httpServer.start(8080, router);

}
