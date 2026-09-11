package user;

import http.HttpResponse;
import http.HttpStatus;
import router.Router;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class UserRoutes {

    public static List<User> userList = new ArrayList<>(Arrays.asList(
            new User(1, "조민수"),
            new User(2, "황제원"),
            new User(3, "서은건")
    ));

    public static void register(Router router) {
        router.get("/users", request -> {
            StringBuilder body = new StringBuilder();
            for (User i : userList) {
                body.append(i.getUserName())
                        .append("\r\n");
            }
            return HttpResponse.text(HttpStatus.OK, body.toString());
        });
        router.post("/users", request -> {
            userList.add(new User(userList.size() + 1, request.body().text()));
            StringBuilder body = new StringBuilder();
            for (User i : userList) {
                body.append(i.getUserName()).append("\r\n");
            }
            return HttpResponse.text(HttpStatus.CREATED, body.toString());
        });
    }
}
