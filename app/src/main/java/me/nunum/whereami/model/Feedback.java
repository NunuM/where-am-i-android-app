package me.nunum.whereami.model;

/**
 * Created by nuno on 04/07/2019.
 */

public class Feedback {

    private String contact;

    private String message;

    public Feedback(String contact, String message) {
        this.contact = contact;
        this.message = message;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
