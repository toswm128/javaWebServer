package user;

import http.HttpResponse;
import http.HttpStatus;
import http.Json;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import router.Router;


public class UserRoutes {

  private static List<User> findAllUsers() throws SQLException {
    String url = System.getenv("DB_URL");
    String user = System.getenv("DB_USER");
    String password = System.getenv("DB_PASSWORD");
    List<User> userList = new ArrayList<>();

    try (Connection connection = DriverManager.getConnection(url, user, password);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users");
        ResultSet resultSet = preparedStatement.executeQuery();
    ) {
      while (resultSet.next()) {
        userList.add(
            new User(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("age")));
      }
    }

    return userList;
  }

  private static User findUserById(int id) throws SQLException {
    String url = System.getenv("DB_URL");
    String user = System.getenv("DB_USER");
    String password = System.getenv("DB_PASSWORD");
    try (Connection connection = DriverManager.getConnection(url, user, password);
        PreparedStatement preparedStatement = connection.prepareStatement(
            "SELECT * FROM users WHERE id = ?");
    ) {
      preparedStatement.setInt(1, id);
      try (ResultSet resultSet = preparedStatement.executeQuery();) {

        if (resultSet.next()) {
          return new User(resultSet.getInt("id"), resultSet.getString("name"),
              resultSet.getInt("age"));
        }
      }
    }
    return null;
  }

  private static void createUser(CreateUserRequest request) throws SQLException {
    String url = System.getenv("DB_URL");
    String user = System.getenv("DB_USER");
    String password = System.getenv("DB_PASSWORD");
    try (Connection connection = DriverManager.getConnection(url, user, password);
        PreparedStatement preparedStatement = connection.prepareStatement(
            "INSERT INTO users (name, age) VALUES (?, ?)");
    ) {
      preparedStatement.setString(1, request.name());
      preparedStatement.setInt(2, request.age());
      preparedStatement.executeUpdate();
    }
  }

  public static void register(Router router) {
    router.get("/users", (request, __) -> {
      List<User> userList;
      try {
        userList = findAllUsers();

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
        user = findUserById(Integer.parseInt(patchValue.get("id")));
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
        createUser(createUserRequest);
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
