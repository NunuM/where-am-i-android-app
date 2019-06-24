package me.nunum.whereami.model.request;

@SuppressWarnings("SameParameterValue")
public final class PostRequest {

    private final String title;

    private final String imageURL;

    private final String sourceURL;


    public PostRequest() {
        this("", "", "");
    }

    private PostRequest(String title, String imageURL, String sourceURL) {
        this.title = title;
        this.imageURL = imageURL;
        this.sourceURL = sourceURL;
    }

    public String getTitle() {
        return title;
    }


    public String getImageURL() {
        return imageURL;
    }


    public String getSourceURL() {
        return sourceURL;
    }


    @Override
    public String toString() {
        return "PostRequest{" +
                "title='" + title + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", sourceURL='" + sourceURL + '\'' +
                '}';
    }
}
