package me.nunum.whereami.model;

public class Algorithm {
    private String name;
    private Long id;

    private String paperURL;
    private Integer ratring;
    private String authorName;

    public Algorithm() {
    }

    public Algorithm(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public String getPaperURL() {
        return paperURL;
    }

    public Integer getRatring() {
        return ratring;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }
}
