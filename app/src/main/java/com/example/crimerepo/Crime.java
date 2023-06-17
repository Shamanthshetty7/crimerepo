package com.example.crimerepo;

public class Crime {
    private String title;
    private String location;
    private String imageUri;
    private String description;


    public Crime(String title,String description, String location,String imageUri) {
        this.title = title;
        this.location = location;
        this.imageUri = imageUri;
        this.description= description;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getImageResId() {
        return imageUri;
    }
    public String getDescription() {
        return description;
    }

    public boolean isSolved() {
        return false;
    }

    public String getImageUri() {
        return imageUri;
    }


}
