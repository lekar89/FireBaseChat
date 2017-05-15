package pom.lekar.firebasechat.models;


public class FriendlyMessage {
    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private String imageUrl;
    private String videoUrl;
    private String audioUrl;
    private String latLong;
    public FriendlyMessage() {
    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String mLatLong) {
        latLong = mLatLong;
    }

    public FriendlyMessage(String name, String photoUrl) {
        this.name     = name;
        this.photoUrl = photoUrl;
    }

    public FriendlyMessage(String text, String name, String photoUrl, String imageUrl,String videoUrl) {
        this.text     = text;
        this.name     = name;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String mVideoUrl) {
        videoUrl = mVideoUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String mAudioUrl) {
        audioUrl = mAudioUrl;
    }





}

