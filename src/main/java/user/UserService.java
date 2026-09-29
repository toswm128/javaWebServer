package user;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {

  public static class DataAccessException extends RuntimeException {

    public DataAccessException(Throwable cause) {
      super(cause);
    }
  }

  private static final UserRepository userRepository = new UserRepository();

  public List<User> getUsers() {
    List<User> userList;
    try {
      userList = userRepository.findAllUsers();
    } catch (SQLException e) {
      System.out.println(e.getSQLState());
      throw new DataAccessException(e);
    }
    return userList;
  }

  public Optional<User> getUser(int id) {
    User user;
    try {
      user = userRepository.findUserById(id);
      return Optional.ofNullable(user);
    } catch (SQLException e) {
      System.out.println(e.getSQLState());
      throw new DataAccessException(e);
    }
  }

}
