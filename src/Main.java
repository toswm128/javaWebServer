import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Main {

    private static final Router router = new Router();

    public static List<User> userList = new ArrayList<>(Arrays.asList(
            new User(1, "조민수"),
            new User(2, "황제원"),
            new User(3, "서은건")
    ));


    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("서버 실행 http://localhost:8080");
        router.get("/users", request -> {
            int status = 200;
            StringBuilder body = new StringBuilder();
            for (User i : userList) {
                body.append(i.getUserName()).append("\r\n");
            }

            return buildResponse(status, body.toString());
        });
        router.post("/users", request -> {
            int status = 201;
            userList.add(new User(1, request.body()));
            StringBuilder body = new StringBuilder();
            for (User i : userList) {
                body.append(i.getUserName()).append("\r\n");
            }
            return buildResponse(status, body.toString());
        });

        while (true) {
            Socket socket = serverSocket.accept();
            InputStream input = socket.getInputStream();

            String headers = readHeaders(input);
            String[] headerLines = headers.split("\r\n");
            String[] requestParts = headerLines[0].split(" ");
            Map<String, String> headerData = new HashMap<>();
            String method = requestParts[0];
            String path = requestParts[1];
            int contentLength = 0;

//            헤더 MAP 만들기
            for (String header : headerLines) {
                if (header.contains(":")) {
                    int colonIndex = header.indexOf(':');
                    if (colonIndex >= 0) {
                        headerData.put(header.substring(0, colonIndex), header.substring(colonIndex + 1).trim());
                    }
                }
            }

            String contentLengthHeader = headerData.get("Content-Length");

//          body 받기
            if (contentLengthHeader != null) {
                contentLength = Integer.parseInt(contentLengthHeader);
            }
            byte[] bodyBytes =
                    input.readNBytes(contentLength);

            String requestBody =
                    new String(
                            bodyBytes,
                            StandardCharsets.UTF_8
                    );

            HttpRequest request =
                    new HttpRequest(
                            method,
                            path,
                            headerData,
                            requestBody
                    );

            String response = getResponse(request);

            OutputStream out = socket.getOutputStream();

            out.write(
                    response.getBytes(StandardCharsets.UTF_8)
            );

            out.flush();

            socket.close();
        }
    }

    private static String readHeaders(InputStream input) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

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

        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static HttpResponse buildResponse(int status, String body) {
        String statusText = "";
        switch (status) {
            case 100 -> statusText = "Continue";
            case 101 -> statusText = "Switching Protocols";
            case 102 -> statusText = "Processing";
            case 103 -> statusText = "Early Hints";
            case 104 -> statusText = "Upload Resumption Supported";

            case 200 -> statusText = "OK";
            case 201 -> statusText = "Created";
            case 202 -> statusText = "Accepted";
            case 203 -> statusText = "Non-Authoritative Information";
            case 204 -> statusText = "No Content";
            case 205 -> statusText = "Reset Content";
            case 206 -> statusText = "Partial Content";
            case 207 -> statusText = "Multi-Status";
            case 208 -> statusText = "Already Reported";
            case 226 -> statusText = "IM Used";

            case 300 -> statusText = "Multiple Choices";
            case 301 -> statusText = "Moved Permanently";
            case 302 -> statusText = "Found";
            case 303 -> statusText = "See Other";
            case 304 -> statusText = "Not Modified";
            case 305 -> statusText = "Use Proxy";
            case 306 -> statusText = "Unused";
            case 307 -> statusText = "Temporary Redirect";
            case 308 -> statusText = "Permanent Redirect";

            case 400 -> statusText = "Bad Request";
            case 401 -> statusText = "Unauthorized";
            case 402 -> statusText = "Payment Required";
            case 403 -> statusText = "Forbidden";
            case 404 -> statusText = "Not Found";
            case 405 -> statusText = "Method Not Allowed";
            case 406 -> statusText = "Not Acceptable";
            case 407 -> statusText = "Proxy Authentication Required";
            case 408 -> statusText = "Request Timeout";
            case 409 -> statusText = "Conflict";
            case 410 -> statusText = "Gone";
            case 411 -> statusText = "Length Required";
            case 412 -> statusText = "Precondition Failed";
            case 413 -> statusText = "Content Too Large";
            case 414 -> statusText = "URI Too Long";
            case 415 -> statusText = "Unsupported Media Type";
            case 416 -> statusText = "Range Not Satisfiable";
            case 417 -> statusText = "Expectation Failed";
            case 418 -> statusText = "Unused";
            case 421 -> statusText = "Misdirected Request";
            case 422 -> statusText = "Unprocessable Content";
            case 423 -> statusText = "Locked";
            case 424 -> statusText = "Failed Dependency";
            case 425 -> statusText = "Too Early";
            case 426 -> statusText = "Upgrade Required";
            case 428 -> statusText = "Precondition Required";
            case 429 -> statusText = "Too Many Requests";
            case 431 -> statusText = "Request Header Fields Too Large";
            case 451 -> statusText = "Unavailable For Legal Reasons";

            case 500 -> statusText = "Internal Server Error";
            case 501 -> statusText = "Not Implemented";
            case 502 -> statusText = "Bad Gateway";
            case 503 -> statusText = "Service Unavailable";
            case 504 -> statusText = "Gateway Timeout";
            case 505 -> statusText = "HTTP Version Not Supported";
            case 506 -> statusText = "Variant Also Negotiates";
            case 507 -> statusText = "Insufficient Storage";
            case 508 -> statusText = "Loop Detected";
            case 510 -> statusText = "Not Extended";
            case 511 -> statusText = "Network Authentication Required";

            default -> statusText = "Unknown Status";
        }

        return new HttpResponse(status, statusText, new HttpHeaders(body), body);
    }

    private static String getResponse(HttpRequest request) {

        Handler handler =
                router.find(
                        request.method(),
                        request.path()
                );

        String status;
        String body;
        Map<String, String> headers = new HashMap<>();
        HttpResponse response = null;

        if (handler == null) {
            status = "404 Not Found";
            body = "404 Not Found";
        } else {
            response = handler.handle(request);
            headers = response.headers().getHeaders();
            status = response.status() + " " + response.statusText();
            body = response.body();
        }

        byte[] bodyBytes =
                body.getBytes(StandardCharsets.UTF_8);

        StringBuilder result = new StringBuilder();
        result.append("HTTP/1.1. ")
                .append(status)
                .append("\r\n");
        for (Map.Entry<String, String> header :
                headers.entrySet()) {
            result.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }
        result.append("\r\n");
        if (response != null) {
            result.append(response.body());
        }

        return result.toString();
    }
}