package pom.lekar.firebasechat;

/**
 * Created by lekar on 08.05.17.
 */

public class User {
    public String uid;
    public String email;
    public String firebaseToken;

    public User() {}

    public User(String uid, String email, String firebaseToken) {
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
    }
}