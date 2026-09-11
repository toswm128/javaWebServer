package HTTP;

public record HttpRequest(
        String method,
        String path,
        HttpHeaders headers,
        HttpBody body
) {
}
