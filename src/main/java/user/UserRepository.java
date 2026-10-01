package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

  private static Connection connectDB() throws SQLException {
    String url = System.getenv("DB_URL");
    String user = System.getenv("DB_USER");
    String password = System.getenv("DB_PASSWORD");

    return DriverManager.getConnection(url, user, password);
  }

  public List<User> findAllUsers() throws SQLException {
    List<User> userList = new ArrayList<>();

    try (Connection connection = connectDB();
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

  public Optional<User> findUserById(int id) throws SQLException {
    try (Connection connection = connectDB();
        PreparedStatement preparedStatement = connection.prepareStatement(
            "SELECT * FROM users WHERE id = ?");
    ) {
      preparedStatement.setInt(1, id);
      try (ResultSet resultSet = preparedStatement.executeQuery();) {

        if (resultSet.next()) {
          return Optional.of(new User(resultSet.getInt("id"), resultSet.getString("name"),
              resultSet.getInt("age")));
        }
      }
    }
    return Optional.empty();
  }

  public void createUser(CreateUserRequest request) throws SQLException {
    try (Connection connection = connectDB();
        PreparedStatement preparedStatement = connection.prepareStatement(
            "INSERT INTO users (name, age) VALUES (?, ?)");
    ) {
      preparedStatement.setString(1, request.name());
      preparedStatement.setInt(2, request.age());
      preparedStatement.executeUpdate();
    }
  }

}
