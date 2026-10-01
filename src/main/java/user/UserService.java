package user;

import http.exception.DataAccessException;
import http.exception.NotFoundException;
import java.sql.SQLException;
import java.util.List;

public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

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

  public void addUser(CreateUserRequest request) {
    try {
      userRepository.createUser(request);
    } catch (SQLException e) {
      System.out.println(e.getSQLState());
      throw new DataAccessException(e);
    }
  }

}
