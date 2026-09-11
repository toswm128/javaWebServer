package http;

public interface Handler {
    HttpResponse handle(HttpRequest httpRequest);
}

