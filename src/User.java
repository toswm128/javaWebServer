public class User {

    public User(int id, String name) {
        setUserId(id);
        setUserName(name);
    }

    private int UserId;
    private String userName;

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
