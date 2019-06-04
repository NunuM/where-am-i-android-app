package me.nunum.whereami.model;

import android.support.annotation.NonNull;

import java.util.Date;

public class Post implements Comparable<Post> {

    private Long id;

    private String title;

    private String imageURL;

    private Date created;

    public Post() {
        this(0L, "", "", new Date());
    }

    public Post(Long id, String title, String imageURL, Date created) {
        this.id = id;
        this.title = title;
        this.imageURL = imageURL;
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public int compareTo(@NonNull Post o) {
        return this.getCreated().compareTo(o.getCreated());
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", created=" + created +
                '}';
    }
}
