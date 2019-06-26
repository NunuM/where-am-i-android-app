package me.nunum.whereami.model;

import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Created by nuno on 25/06/2019.
 */

public class Notification implements Comparable<Notification> {

    private final int id;

    private final String message;

    private boolean seen;

    private final String prettyDate;

    private final long timestamp;

    private final UUID uid;


    public Notification(int id, String message, boolean seen, String prettyDate, long timestamp, String uid) {
        this.id = id;
        this.message = message;
        this.seen = seen;
        this.prettyDate = prettyDate;
        this.timestamp = timestamp;
        this.uid = UUID.fromString(uid);
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getPrettyDate() {
        return prettyDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUid() {
        return uid.toString();
    }

    @Override
    public int compareTo(@NonNull Notification o) {
        if(this.timestamp > o.timestamp){
            return -1;
        }
        return this.timestamp == o.timestamp ? 0 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;

        return uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", seen=" + seen +
                ", prettyDate='" + prettyDate + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
