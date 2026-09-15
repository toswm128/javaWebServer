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

        public Map<String, String> getValue() {
            return value;
        }

        private Map<String, String> value;

        public PatchValueDTO(Handler handler, Map<String, String> value) {
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
                    Map<String, String> pathMap = CheckPath(routedPath, routingPath);
                    if (pathMap != null) {
                        return new PatchValueDTO(routes.get(s), pathMap);
                    }
                }
            }
        }
        return new PatchValueDTO(routes.get(method + " " + path), null);
    }

    private Map<String, String> CheckPath(String[] routedPath, String[] routingPath) {
        Map<String, String> pathMap = new HashMap<>();

        for (int i = 0; routedPath.length > i; i++) {
            if (routedPath[i].trim().startsWith("{") && routedPath[i].trim().endsWith("}")) {
                pathMap.put(routedPath[i].trim().substring(1, routedPath[i].trim().length() - 1), routingPath[i].trim());
            } else if (!Objects.equals(routedPath[i], routingPath[i])) {
                return null;
            }
        }

        return pathMap;

    }

}