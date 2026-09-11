package HTTP;

public interface Handler {
    HttpResponse handle(HttpRequest httpRequest);
}

