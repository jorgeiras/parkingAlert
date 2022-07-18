package com.example.parkingalert;

public class ParkingSpace {

    private String UserID;
    private String encodedBitmapPhoto;
    private double latitude;
    private double longitude;
    private long timeStamp;
    private boolean paymentArea;
    private String docID;

    public ParkingSpace(){

    }

    public ParkingSpace(String userID, String encodedBitmapPhoto, double latitude, double longitude, long timeStamp, boolean paymentArea) {
        UserID = userID;
        this.encodedBitmapPhoto = encodedBitmapPhoto;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
        this.paymentArea = paymentArea;
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

    public boolean isPaymentArea() {
        return paymentArea;
    }

    public void setPaymentArea(boolean paymentArea) {
        this.paymentArea = paymentArea;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }
}
