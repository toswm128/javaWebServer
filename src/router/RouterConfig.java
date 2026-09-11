package router;


import user.UserRoutes;

public class RouterConfig {
    public static void register(Router router) {
        UserRoutes.register(router);
    }
}
