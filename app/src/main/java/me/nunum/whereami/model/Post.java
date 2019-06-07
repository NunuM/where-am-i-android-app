package me.nunum.whereami.model;

import android.support.annotation.NonNull;

import java.util.Date;

public class Post implements Comparable<Post> {

    private Long id;

    private String title;

    private String sourceURL;

    private String imageURL;

    private Date created;

    public Post() {
        this(0L, "", "", "", new Date());
    }

    public Post(Long id, String title, String sourceURL, String imageURL, Date created) {
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

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    @Override
    public int compareTo(@NonNull Post o) {
        return this.getCreated().compareTo(o.getCreated()) * -1;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;

        Post post = (Post) o;

        return getId().equals(post.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
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
