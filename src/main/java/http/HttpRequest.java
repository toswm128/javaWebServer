package http;

import java.util.Map;

public record HttpRequest(
    String method,
    String path,
    HttpHeaders headers,
    HttpBody body,
    Map<String, String> queryParams
) {

}
