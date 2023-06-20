package com.example.crimerepo;

public class Crime {
    private String title;
    private String location;
    private String imageUri;
    private String description;
    public String status;
private String reportId;

    public Crime(String reportId,String title,String description, String location,String imageUri,String status) {
        this.title = title;
        this.location = location;
        this.imageUri = imageUri;
        this.description= description;
        this.reportId=reportId;
        this.status=status;
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
    public String getReportId() {
        return reportId;
    }

    public CharSequence getStatus() {
        return status;
    }
}
