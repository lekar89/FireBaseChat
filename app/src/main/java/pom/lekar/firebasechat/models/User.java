package pom.lekar.firebasechat.models;

/**
 * Created by lekar on 08.05.17.
 */

public class User {
    private String uid;
    private String name;
    private String photoUrl;

    public User() {}

    public User(String uid, String nsme, String photoUrl) {
        this.uid = uid;
        this.name = nsme;
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String mUid) {
        uid = mUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        name = mName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String mPhotoUrl) {
        photoUrl = mPhotoUrl;
    }
}