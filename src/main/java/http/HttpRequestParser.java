package http;

import http.exception.BadRequestException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpRequestParser {

  public static HttpRequest parse(InputStream input) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    HttpHeaders headers = new HttpHeaders();
    int b;
    boolean isDone = true;
    while ((b = input.read()) != -1) {
      buffer.write(b);
      byte[] data = buffer.toByteArray();
      int length = data.length;
      if (length >= 4 &&
          data[length - 4] == '\r' &&
          data[length - 3] == '\n' &&
          data[length - 2] == '\r' &&
          data[length - 1] == '\n') {
        isDone = false;
        break;
      }
    }
    if (buffer.size() == 0) {
      return null;
    }
    if (isDone) {
      throw new BadRequestException("잘못된 요청입니다.");
    }
    try {
      String[] headerLines = buffer.toString().split("\r\n");
      String[] requestParts = headerLines[0].split(" ");
      if (requestParts.length < 2) {
        throw new BadRequestException("잘못된 요청입니다.");
      }
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
      if (contentLength < 0) {
        throw new IllegalArgumentException("content-length는 음수일 수 없습니다.");
      }
      byte[] bodyBytes = input.readNBytes(contentLength);
      if (bodyBytes.length != contentLength) {
        throw new BadRequestException("잘못된 요청입니다.");
      }

      return new HttpRequest(method, path, headers,
          new HttpBody(new String(bodyBytes, StandardCharsets.UTF_8)));

    } catch (NumberFormatException e) {
      throw new BadRequestException("잘못된 요청입니다.",
          e);
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("잘못된 요청입니다.",
          e);
    }

  }


}
