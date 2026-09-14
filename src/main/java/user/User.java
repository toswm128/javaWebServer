package user;

public class User {

    public User(int id, String name, int age) {
        setUserId(id);
        setUserName(name);
        setAge(age);
    }

    private int userId;
    private String userName;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private int age;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
