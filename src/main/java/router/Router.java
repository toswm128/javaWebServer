package router;

import http.Handler;

import java.util.*;

public class Router {
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

    private final Map<String, Handler> routes = new HashMap<>();
    private final Map<String, List<PathSegment>> pathLists = new HashMap<>();

    public void get(String path, Handler handler) {
        String methodPath = "GET " + path;
        pathLists.put(methodPath, parsePathSegments(methodPath));
        routes.put(methodPath, handler);
    }

    public void post(String path, Handler handler) {
        String methodPath = "POST " + path;
        pathLists.put(methodPath, parsePathSegments(methodPath));
        routes.put(methodPath, handler);
    }

    public RouteMatch find(String method, String path) {
        if (routes.get(method + " " + path) == null) {
            List<PathSegment> routingPathList = parsePathSegments(method + " " + path);

            for (String listKey : pathLists.keySet()) {
                System.out.println(listKey + "  " + pathLists.get(listKey) + "  " + Arrays.asList(routingPathList));
                if (routingPathList.size() == pathLists.get(listKey).size()) {
                    Map<String, String> pathMap = CheckPath(pathLists.get(listKey), routingPathList);
                    if (pathMap != null)
                        return new RouteMatch(routes.get(listKey), pathMap);
                }
            }
        }
        return new RouteMatch(routes.get(method + " " + path), null);
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