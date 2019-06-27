package me.nunum.whereami.model;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TrainingProgress implements Comparable<TrainingProgress> {


    private Long id;
    private String status;
    private Date created;
    private Date Updated;
    private Algorithm algorithm;

    public TrainingProgress() {
        this(0L, "", new Algorithm());
    }

    public TrainingProgress(Long id, String status, Algorithm algorithm) {
        this.id = id;
        this.status = status;
        this.algorithm = algorithm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAlgorithmName() {
        return this.algorithm.getName();
    }

    public String prettyDate() {

        SimpleDateFormat format = new SimpleDateFormat("MM d, Y", Locale.getDefault());

        return format.format(this.created);
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public Long getAlgorithmId() {
        return this.algorithm.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrainingProgress that = (TrainingProgress) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public int compareTo(@NonNull TrainingProgress trainingProgress) {
        return this.getAlgorithmName().toLowerCase().compareTo(trainingProgress.getAlgorithmName().toLowerCase());
    }
}
