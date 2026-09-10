import java.nio.charset.StandardCharsets;
import java.util.Map;

public record HttpResponse(
        HttpStatus status,
        HttpHeaders headers,
        String body
) {

    public static HttpResponse text(
            HttpStatus status,
            String body
    ) {
        HttpHeaders headers = new HttpHeaders();

        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        headers.set(
                "Content-type",
                "text/plain; charset=UTF-8"
        );
        headers.set(
                "Content-Length",
                String.valueOf(bodyBytes.length)
        );

        return new HttpResponse(
                status,
                headers,
                body
        );
    }

    public String toHttpString() {
        StringBuilder result = new StringBuilder();

        result.append("HTTP/1.1 ")
                .append(status.code())
                .append(" ")
                .append(status.reason())
                .append("\r\n");
        for (Map.Entry<String, String> header : headers.getHeaders().entrySet()) {
            result.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }
        result.append("\r\n");

        if (body != null) {
            result.append(body);
        }
        return result.toString();
    }
}
