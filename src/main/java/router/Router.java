package router;

import http.Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Router {
    public class PatchValueDTO {
        public Handler getHandler() {
            return handler;
        }

        private Handler handler;

        public String getValue() {
            return value;
        }

        private String value;

        public PatchValueDTO(Handler handler, String value) {
            this.handler = handler;
            this.value = value;
        }

    }

    private final Map<String, Handler> routes = new HashMap<>();

    public void get(String path, Handler handler) {
        routes.put("GET " + path, handler);
    }

    public void post(String path, Handler handler) {
        routes.put("POST " + path, handler);
    }

    public PatchValueDTO find(String method, String path) {
        if (routes.get(method + " " + path) == null) {
            String methodPath = method + " " + path;
            for (String s : routes.keySet()) {
                String[] routedPath = s.split("/");
                String[] routingPath = methodPath.split("/");
                if (routedPath.length == routingPath.length) {
                    int key = CheckPath(routedPath, routingPath);
                    if (key != -1) {
                        return new PatchValueDTO(routes.get(s), routingPath[key].trim());
                    }
                }
            }
        }
        return new PatchValueDTO(routes.get(method + " " + path), null);
    }

    private int CheckPath(String[] routedPath, String[] routingPath) {
        int key = -1;

        for (int i = 0; routedPath.length > i; i++) {
            if (routedPath[i].contains("{")) {
                key = i;
            } else if (!Objects.equals(routedPath[i], routingPath[i])) {
                return -1;
            }
        }

        return key;

    }

}