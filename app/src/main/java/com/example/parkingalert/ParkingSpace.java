package com.example.parkingalert;

public class ParkingSpace {

    private String UserID;
    private String encodedBitmapPhoto;
    private double latitude;
    private double longitude;
    private long timeStamp;

    public ParkingSpace(){

    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getEncodedBitmapPhoto() {
        return encodedBitmapPhoto;
    }

    public void setEncodedBitmapPhoto(String encodedBitmapPhoto) {
        this.encodedBitmapPhoto = encodedBitmapPhoto;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
