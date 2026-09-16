package http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        HttpHeaders headers = new HttpHeaders();
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
        try {

            String[] headerLines = buffer.toString().split("\r\n");
            String[] requestParts = headerLines[0].split(" ");
            String method = requestParts[0];
            String path = requestParts[1];
            for (String header : headerLines) {
                if (header.contains(":")) {
                    int colonIndex = header.indexOf(':');
                    if (colonIndex >= 0) {
                        headers.set(header.substring(0, colonIndex), header.substring(colonIndex + 1).trim());
                    }
                }
            }

            int contentLength = 0;
            String contentLengthHeader = headers.get("Content-Length");

            if (contentLengthHeader != null) {
                contentLength = Integer.parseInt(contentLengthHeader);
            }
            byte[] bodyBytes = input.readNBytes(contentLength);

            return new HttpRequest(method, path, headers, new HttpBody(new String(bodyBytes, StandardCharsets.UTF_8)));

        } catch (Exception e) {
            return null;
        }
    }


}
