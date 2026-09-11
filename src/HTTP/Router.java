package HTTP;

import java.util.HashMap;
import java.util.Map;

public class Router {
    public final Map<String, Handler> routes = new HashMap<>();

    public void get(String path, Handler handler) {
        routes.put("GET " + path, handler);
    }

    public void post(String path, Handler handler) {
        routes.put("POST " + path, handler);
    }

    public Handler find(String method, String path) {
        return routes.get(method + " " + path);
    }

}
