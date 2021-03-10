package com.app.triponeer;

public class SocialMediaUser {
    public String name;
    public String email;
    public String imageUrl;

    private static SocialMediaUser user = null;

    public SocialMediaUser() {
        name = "";
        email = "";
        imageUrl = "";
    }

    public static SocialMediaUser getInstance() {
        if (user == null) {
            user = new SocialMediaUser();
        }
        return user;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String accName) {
        this.name = accName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String accEmail) {
        this.email = accEmail;
    }

}
