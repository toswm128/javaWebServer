import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    private final Map<String, String> headers = new HashMap<>();

    public HttpHeaders(String body) {

        byte[] bodyBytes =
                body.getBytes(StandardCharsets.UTF_8);

        headers.put("Content-Type", "text/plain; charset=UTF-8");
        headers.put("Content-Length", String.valueOf(bodyBytes.length));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }


}
