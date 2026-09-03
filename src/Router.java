import java.util.HashMap;
import java.util.Map;

public class Router {
    public final Map<String, Handler> routes = new HashMap<>();

    public void get(String path, Handler handler) {
        routes.put(path, handler);
    }

    public Handler find(String path) {
        return routes.get(path);
    }

}
