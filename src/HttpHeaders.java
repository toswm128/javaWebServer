import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHeaders {
    private final Map<String, String> headers = new LinkedHashMap<>();

    public void set(String name, String value) {
        headers.put(name, value);
    }

    public String get(String name) {
        return headers.get(name);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }


}
