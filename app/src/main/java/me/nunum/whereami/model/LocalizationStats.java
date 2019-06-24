package me.nunum.whereami.model;

import java.io.Serializable;

@SuppressWarnings("SameParameterValue")
public class LocalizationStats implements Serializable {

    private Long samples;

    private Integer numberOfTrainedModels;

    private Integer positions;

    public LocalizationStats() {
        this(0L, 0, 0);
    }

    private LocalizationStats(Long samples, Integer numberOfTrainedModels, Integer positions) {
        this.samples = samples;
        this.numberOfTrainedModels = numberOfTrainedModels;
        this.positions = positions;
    }

    public Long getSamples() {
        return samples;
    }

    public void setSamples(Long samples) {
        this.samples = samples;
    }

    public Integer getNumberOfTrainedModels() {
        return numberOfTrainedModels;
    }

    public void setNumberOfTrainedModels(Integer numberOfTrainedModels) {
        this.numberOfTrainedModels = numberOfTrainedModels;
    }

    public Integer getPositions() {
        return positions;
    }

    public void setPositions(Integer positions) {
        this.positions = positions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalizationStats)) return false;

        LocalizationStats that = (LocalizationStats) o;

        if (getSamples() != null ? !getSamples().equals(that.getSamples()) : that.getSamples() != null)
            return false;
        if (getNumberOfTrainedModels() != null ? !getNumberOfTrainedModels().equals(that.getNumberOfTrainedModels()) : that.getNumberOfTrainedModels() != null)
            return false;
        return getPositions() != null ? getPositions().equals(that.getPositions()) : that.getPositions() == null;
    }

    @Override
    public int hashCode() {
        int result = getSamples() != null ? getSamples().hashCode() : 0;
        result = 31 * result + (getNumberOfTrainedModels() != null ? getNumberOfTrainedModels().hashCode() : 0);
        result = 31 * result + (getPositions() != null ? getPositions().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LocalizationStats{" +
                "samples=" + samples +
                ", numberOfTrainedModels=" + numberOfTrainedModels +
                ", positions=" + positions +
                '}';
    }

    public void incrementSamples() {
        this.samples += 1;
    }
}
