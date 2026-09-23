package router;

import http.Handler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Router {

  public record Route(
      String method,
      List<PathSegment> pathSegments,
      Handler handler
  ) {

  }

  public record PathSegment(
      String path,
      boolean variable,
      String valueName
  ) {

  }

  public record RoutingPath(
      String method,
      String path,
      List<PathSegment> pathSegments,
      Map<String, String> pathValueMap
  ) {

  }

  public record RouteMatch(
      Handler handler,
      Map<String, String> pathValues
  ) {


  }

  private List<PathSegment> parsePathSegments(String path) {
    List<PathSegment> pathList = new ArrayList<>();
    for (String p : path.trim().split("/")) {
      if (p.equals("")) {
        continue;
      }
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

  private RoutingPath parseRoutingPath(String method, String path,
      List<PathSegment> mathedPath) {
    List<PathSegment> pathSegments = parsePathSegments(path);
    Map<String, String> pathValues = new HashMap<>();
    int i = 0;

    for (PathSegment p : mathedPath) {
      if (p.variable()) {
        PathSegment pathSegment = pathSegments.get(i);
        if (pathSegments.get(i) != null) {
          pathValues.put(p.valueName, pathSegment.path);
        }
      }
      i++;
    }

    return new RoutingPath(method, path, pathSegments, pathValues);
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
      List<PathSegment> mathedPath = matchPath(method, path);
      if (mathedPath != null) {
        StringBuilder mathedPathString = new StringBuilder();
        for (PathSegment m : mathedPath) {
          mathedPathString.append("/").append(m.path());
        }
        RoutingPath routingPath = parseRoutingPath(method, path, mathedPath);
        return new RouteMatch(routes.get(method + " " + mathedPathString.toString()).handler(),
            routingPath.pathValueMap());

      }
      return null;
    } else {
      return new RouteMatch(routes.get(method + " " + path).handler(), new HashMap<>());
    }


  }

  private List<PathSegment> matchPath(String method, String path) {
    List<PathSegment> routingPathList = parsePathSegments(path);

    for (Route route : routes.values()) {
      List<PathSegment> pathSegment = route.pathSegments();
      if (routingPathList.size() == pathSegment.size() && Objects.equals(route.method(),
          method)) {
        Map<String, String> pathMap = checkPath(pathSegment, routingPathList);
        if (pathMap != null) {
          return route.pathSegments();
        }
      }
    }
    return null;
  }

  private Map<String, String> checkPath(List<PathSegment> routedPath,
      List<PathSegment> routingPath) {
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