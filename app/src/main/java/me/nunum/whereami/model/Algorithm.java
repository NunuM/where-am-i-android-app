package me.nunum.whereami.model;

import java.util.ArrayList;

public class Algorithm {
    private String name;
    private Long id;

    private String paperURL;
    private String authorName;
    private ArrayList<AlgorithmProvider> providers;

    public Algorithm() {
        this("", 0L, "", new ArrayList<AlgorithmProvider>());
    }

    public Algorithm(String name, Long id, String authorName, ArrayList<AlgorithmProvider> providers) {
        this.name = name;
        this.id = id;
        this.authorName = authorName;
        this.providers = providers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaperURL() {
        return paperURL;
    }

    public void setPaperURL(String paperURL) {
        this.paperURL = paperURL;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public ArrayList<AlgorithmProvider> getProviders() {
        return providers;
    }

    public void setProviders(ArrayList<AlgorithmProvider> providers) {
        this.providers = providers;
    }
}
