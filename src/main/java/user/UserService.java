package user;

import java.sql.SQLException;
import java.util.List;

public class UserService {

  public static class DataAccessException extends RuntimeException {

    public DataAccessException(Throwable cause) {
      super(cause);
    }
  }

  public static class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
      super(message);
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

  public User getUser(int id) {
    try {
      return userRepository.findUserById(id)
          .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
    } catch (SQLException e) {
      System.out.println(e.getSQLState());
      throw new DataAccessException(e);
    }
  }

}
