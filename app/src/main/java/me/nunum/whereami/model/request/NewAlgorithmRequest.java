package me.nunum.whereami.model.request;

@SuppressWarnings("SameParameterValue")
final class NewAlgorithmRequest {

    private final String name;

    private final String authorName;

    private final String paperURL;


    public NewAlgorithmRequest() {
        this("", "", "");
    }

    private NewAlgorithmRequest(String name, String authorName, String paperURL) {
        this.name = name;
        this.authorName = authorName;
        this.paperURL = paperURL;
    }

    public String getName() {
        return name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getPaperURL() {
        return paperURL;
    }

    @Override
    public String toString() {
        return "NewAlgorithmRequest{" +
                "name='" + name + '\'' +
                ", authorName='" + authorName + '\'' +
                ", paperURL='" + paperURL + '\'' +
                '}';
    }

}
