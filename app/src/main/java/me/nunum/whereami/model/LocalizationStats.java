package me.nunum.whereami.model;

public class LocalizationStats {

    private Long samples;

    private Float accuracy;

    private Integer positions;

    public LocalizationStats() {
        this(0L, 0.0f, 0);
    }

    public LocalizationStats(Long samples, Float accuracy, Integer positions) {
        this.samples = samples;
        this.accuracy = accuracy;
        this.positions = positions;
    }

    public Long getSamples() {
        return samples;
    }

    public void setSamples(Long samples) {
        this.samples = samples;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
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
        if (getAccuracy() != null ? !getAccuracy().equals(that.getAccuracy()) : that.getAccuracy() != null)
            return false;
        return getPositions() != null ? getPositions().equals(that.getPositions()) : that.getPositions() == null;
    }

    @Override
    public int hashCode() {
        int result = getSamples() != null ? getSamples().hashCode() : 0;
        result = 31 * result + (getAccuracy() != null ? getAccuracy().hashCode() : 0);
        result = 31 * result + (getPositions() != null ? getPositions().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LocalizationStats{" +
                "samples=" + samples +
                ", accuracy=" + accuracy +
                ", positions=" + positions +
                '}';
    }
}
