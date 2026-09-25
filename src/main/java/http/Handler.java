package http;

import java.util.Map;

public interface Handler {

  HttpResponse handle(HttpRequest httpRequest, Map<String, String> pathValue)
      throws InterruptedException;
}

