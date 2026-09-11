public record HttpRequest(
        String method,
        String path,
        HttpHeaders headers,
        String body
) {
}
