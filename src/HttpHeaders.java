import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    public HttpHeaders(String body) {
        byte[] bodyBytes =
                body.getBytes(StandardCharsets.UTF_8);

        headers.put("Content-Type", "text/plain; charset=UTF-8\r\n");
        headers.put("Content-Length", bodyBytes.length + "\r\n");
    }

    public Map<String, String> headers = new HashMap<>();

}
