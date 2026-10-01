package router;


import user.UserRoutes;

public class RouterConfig {

  private final UserRoutes userRoutes;

  public RouterConfig(UserRoutes userRoutes) {
    this.userRoutes = userRoutes;
//    this.anyRoutes = anyRoutes;
  }

  public void register(Router router) {
    this.userRoutes.register(router);
  }
}
