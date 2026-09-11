package Router;


import User.UserRoutes;

public class RouterConfig {
    public static void register(Router router) {
        UserRoutes.register(router);
    }
}
