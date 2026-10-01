package user;


import http.HttpResponse;
import http.HttpStatus;
import http.Json;
import http.exception.BadRequestException;
import java.util.List;
import router.Router;


public class UserRoutes {

  private final UserService userService;

  public UserRoutes(UserService userService) {
    this.userService = userService;
  }

  public void register(Router router) {
    router.get("/users", (request, __) -> {
      List<User> userList = this.userService.getUsers();
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
        throw new BadRequestException("id값이 잘못되었습니다.");
      }
    });

    router.post("/users", (request, _) -> {
      CreateUserRequest createUserRequest = Json.read(
          request.body().text(), CreateUserRequest.class);
      userService.addUser(createUserRequest);
      return HttpResponse.text(
          HttpStatus.CREATED,
          "추가되었습니다."
      );
    });
  }
}
