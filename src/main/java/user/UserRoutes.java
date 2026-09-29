package user;

import http.HttpResponse;
import http.HttpStatus;
import http.Json;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import router.Router;
import user.UserService.DataAccessException;


public class UserRoutes {

  private static final UserRepository userRepository = new UserRepository();
  private static final UserService userService = new UserService();

  public static void register(Router router) {
    router.get("/users", (request, __) -> {
      try {
        List<User> userList = userService.getUsers();
        String json = Json.write(userList);
        return HttpResponse.text(
            HttpStatus.OK,
            json
        );


      } catch (DataAccessException e) {
        return HttpResponse.text(
            HttpStatus.INTERNAL_SERVER_ERROR, "예기치 않은 오류"
        );
      }
    });

    router.get("/users/{id}", (request, patchValue) -> {
      try {
        Optional<User> user = userService.getUser(Integer.parseInt(patchValue.get("id")));
        if (user.isEmpty()) {
          return HttpResponse.text(
              HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."
          );
        }
        String json = Json.write(user.get());
        return HttpResponse.text(
            HttpStatus.OK,
            json
        );

      } catch (NumberFormatException e) {
        return HttpResponse.text(HttpStatus.BAD_REQUEST, "유효하지 않은 값입니다.");
      } catch (DataAccessException e) {
        return HttpResponse.text(
            HttpStatus.INTERNAL_SERVER_ERROR, "예기치 않은 오류"
        );
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
