package user;

import com.fasterxml.jackson.databind.ObjectMapper;
import http.HttpResponse;
import http.HttpStatus;
import http.Json;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import router.Router;


public class UserRoutes {

  public static List<User> userList = new ArrayList<>(Arrays.asList(
      new User(0, "조민수", 23),
      new User(1, "황제원", 22),
      new User(2, "서은건", 22)
  ));

  static final ObjectMapper objectMapper = new ObjectMapper();

  public static void register(Router router) {
    router.get("/users", (request, __) -> {
      String json = Json.write(userList);
      return HttpResponse.text(
          HttpStatus.OK,
          json
      );
    });

    router.get("/users/find", (request, _) -> {
      String json = Json.write(userList);
      System.out.println(Arrays.asList(request.queryParams()));
      return HttpResponse.text(
          HttpStatus.OK,
          json
      );
    });

    router.get("/users/{id}", (request, patchValue) -> {
      String json = Json.write(userList.get(Integer.parseInt(patchValue.get("id"))));
      return HttpResponse.text(
          HttpStatus.OK,
          json
      );
    });

    router.get("/users/{id}/followers", (request, _) -> {
      String json = Json.write(userList);
      return HttpResponse.text(
          HttpStatus.OK,
          json
      );
    });

    router.post("/users", (request, _) -> {

      CreateUserRequest createUserRequest = Json.read(
          request.body().text(), CreateUserRequest.class);

      userList.add(
          new User(userList.size() + 1, createUserRequest.name(), createUserRequest.age()));
      return HttpResponse.text(
          HttpStatus.CREATED,
          "추가되었습니다."
      );
    });
  }
}
