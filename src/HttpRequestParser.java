import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public record HttpRequestParser() {

    private static String method;
    private static String path;
    private static HttpHeaders headers;
    private static HttpBody body;

    public static HttpHeaders parseHeader(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        headers = new HttpHeaders();
        int b;
        while ((b = input.read()) != -1) {
            buffer.write(b);
            byte[] data = buffer.toByteArray();
            int length = data.length;
            if (length >= 4 &&
                    data[length - 4] == '\r' &&
                    data[length - 3] == '\n' &&
                    data[length - 2] == '\r' &&
                    data[length - 1] == '\n') {
                break;
            }
        }

        String[] headerLines = buffer.toString().split("\r\n");
        String[] requestParts = headerLines[0].split(" ");
        method = requestParts[0];
        path = requestParts[1];
        for (String header : headerLines) {
            if (header.contains(":")) {
                int colonIndex = header.indexOf(':');
                if (colonIndex >= 0) {
                    headers.set(header.substring(0, colonIndex), header.substring(colonIndex + 1).trim());
                }
            }
        }
        return headers;
    }

    public static HttpBody parseBody(InputStream input) throws IOException {
        int contentLength = 0;

        if (headers.getHeaders().isEmpty()) {
            parseHeader(input);
        }

        String contentLengthHeader = headers.get("Content-Length");

        if (contentLengthHeader != null) {
            contentLength = Integer.parseInt(contentLengthHeader);
        }
        byte[] bodyBytes = input.readNBytes(contentLength);
        
        return new HttpBody(new String(bodyBytes, StandardCharsets.UTF_8));
    }

    public static String getMethod() {
        return method;
    }


    public static String getPath() {
        return path;
    }


}
