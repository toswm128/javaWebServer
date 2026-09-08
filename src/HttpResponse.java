public record HttpResponse(
        int status,
        String statusText,
        HttpHeaders headers,
        String body
) {
}
