package me.nunum.whereami.model;

public class Device {

    private String firebaseToken;

    public Device(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    @Override
    public String toString() {
        return "Device{" +
                "firebaseToken='" + firebaseToken + '\'' +
                '}';
    }
}
