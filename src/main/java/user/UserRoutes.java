package user;

import http.HttpResponse;
import http.HttpStatus;
import http.Json;
import java.sql.SQLException;
import java.util.List;
import router.Router;


public class UserRoutes {

  private static final UserRepository userRepository = new UserRepository();
  private static final UserService userService = new UserService();

  public static void register(Router router) {
    router.get("/users", (request, __) -> {
      List<User> userList = userService.getUsers();
      String json = Json.write(userList);
      return HttpResponse.text(
          HttpStatus.OK,
          json
      );
    });

    router.get("/users/{id}", (request, patchValue) -> {
      try {
        User user = userService.getUser(Integer.parseInt(patchValue.get("id")));
        String json = Json.write(user);
        return HttpResponse.text(
            HttpStatus.OK,
            json
        );

      } catch (NumberFormatException e) {
        return HttpResponse.text(HttpStatus.BAD_REQUEST, "유효하지 않은 값입니다.");
      }
    });

    router.post("/users", (request, _) -> {
      CreateUserRequest createUserRequest = Json.read(
          request.body().text(), CreateUserRequest.class);

      try {
        userRepository.createUser(createUserRequest);
      } catch (SQLException e) {
        System.out.println(e.getMessage());
        return HttpResponse.text(HttpStatus.INTERNAL_SERVER_ERROR, e.getSQLState());
      }

      return HttpResponse.text(
          HttpStatus.CREATED,
          "추가되었습니다."
      );
    });
  }
}
