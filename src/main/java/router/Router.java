package router;

import http.Handler;

import java.util.*;

public class Router {

    public record Route(
            String method,
            List<PathSegment> pathSegment,
            Handler handler
    ) {
    }

    public record RouteMatch(
            Handler handler,
            Map<String, String> pathValues
    ) {
    }

    public record PathSegment(
            String path,
            boolean variable,
            String valueName
    ) {

    }

    private List<PathSegment> parsePathSegments(String path) {
        List<PathSegment> pathList = new ArrayList<>();
        for (String p : path.trim().split("/")) {
            String pathPiece = p.trim();
            String valueName = null;
            boolean variable = false;
            if (pathPiece.startsWith("{") && pathPiece.endsWith("}")) {
                valueName = pathPiece.substring(1, pathPiece.length() - 1);
                variable = true;
            }
            pathList.add(new PathSegment(pathPiece, variable, valueName));
        }


        return pathList;
    }

    private final Map<String, Route> routes = new HashMap<>();

    public void get(String path, Handler handler) {
        String methodPath = "GET " + path;
        routes.put(methodPath, new Route("GET", parsePathSegments(path), handler));
    }

    public void post(String path, Handler handler) {
        String methodPath = "POST " + path;
        routes.put(methodPath, new Route("POST", parsePathSegments(path), handler));
    }

    public RouteMatch find(String method, String path) {
        Route thisRoute = routes.get(method + " " + path);
        if (thisRoute == null) {
            List<PathSegment> routingPathList = parsePathSegments(path);

            for (Route route : routes.values()) {
                List<PathSegment> pathSegment = route.pathSegment();
                if (routingPathList.size() == pathSegment.size() && Objects.equals(route.method(), method)) {
                    Map<String, String> pathMap = CheckPath(pathSegment, routingPathList);
                    if (pathMap != null)
                        return new RouteMatch(route.handler(), pathMap);
                }
            }
            return null;
        } else
            return new RouteMatch(routes.get(method + " " + path).handler(), new HashMap<>());


    }

    private Map<String, String> CheckPath(List<PathSegment> routedPath, List<PathSegment> routingPath) {
        Map<String, String> pathMap = new HashMap<>();
        for (int i = 0; routedPath.size() > i; i++) {
            if (routedPath.get(i).variable()) {
                pathMap.put(routedPath.get(i).valueName(), routingPath.get(i).path());
            } else if (!Objects.equals(routedPath.get(i).path(), routingPath.get(i).path())) {
                return null;
            }
        }

        return pathMap;

    }

}