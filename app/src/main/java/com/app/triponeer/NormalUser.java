package com.app.triponeer;

public class NormalUser {
    private String name;
    private String email;
    private String imageUrl;

    private static NormalUser user = null;

    public NormalUser() {
        name = "";
        email = "";
        imageUrl = "";
    }

    public static NormalUser getInstance() {
        if (user == null) {
            user =  new NormalUser();
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
