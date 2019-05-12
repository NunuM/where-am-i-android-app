package me.nunum.whereami.model.request;


public final class SpamRequest {

    private Long id;

    private String className;

    public SpamRequest() {

    }

    public SpamRequest(Long id, String className) {
        this.id = id;
        this.className = className;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpamRequest)) return false;

        SpamRequest that = (SpamRequest) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SpamRequest{" +
                "id=" + id +
                '}';
    }
}