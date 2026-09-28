package user;

import http.HttpResponse;
import http.HttpStatus;
import http.Json;
import java.sql.SQLException;
import java.util.List;
import router.Router;


public class UserRoutes {

  private static final UserRepository userRepository = new UserRepository();

  public static void register(Router router) {
    router.get("/users", (request, __) -> {
      List<User> userList;
      try {
        userList = userRepository.findAllUsers();

      } catch (SQLException e) {
        System.out.println(e.getMessage());
        return HttpResponse.text(HttpStatus.INTERNAL_SERVER_ERROR, e.getSQLState());
      }
      String json = Json.write(userList);
      return HttpResponse.text(
          HttpStatus.OK,
          json
      );
    });

    router.get("/users/{id}", (request, patchValue) -> {
      User user;
      try {
        user = userRepository.findUserById(Integer.parseInt(patchValue.get("id")));
      } catch (SQLException e) {
        System.out.println(e.getMessage());
        return HttpResponse.text(HttpStatus.INTERNAL_SERVER_ERROR, e.getSQLState());
      }
      if (user != null) {
        String json = Json.write(user);
        return HttpResponse.text(
            HttpStatus.OK,
            json
        );
      }
      return HttpResponse.text(
          HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."

      );
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
