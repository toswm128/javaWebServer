package http;

import java.sql.SQLException;
import java.util.Map;

public interface Handler {

  HttpResponse handle(HttpRequest httpRequest, Map<String, String> pathValue)
      throws InterruptedException, SQLException;
}

