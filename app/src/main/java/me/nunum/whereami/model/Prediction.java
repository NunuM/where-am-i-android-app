package me.nunum.whereami.model;

import android.support.annotation.NonNull;

public class Prediction implements Comparable<Prediction> {

    private Long id;

    private Float accuracy;

    private Long localizationId;

    private Long requestId;

    private Long providerId;

    private Long predictedPositionId;

    private String predictionLabel;

    private Integer predictionFeedback;

    public Prediction() {

        this(0L, 0f, 0L, 0L, 0L, 0L, "", 0);
    }

    public Prediction(Long id,
                      Float accuracy,
                      Long localizationId,
                      Long requestId,
                      Long providerId,
                      Long predictedPositionId,
                      String predictionLabel,
                      Integer predictionFeedback) {
        this.id = id;
        this.accuracy = accuracy;
        this.localizationId = localizationId;
        this.requestId = requestId;
        this.providerId = providerId;
        this.predictedPositionId = predictedPositionId;
        this.predictionLabel = predictionLabel;
        this.predictionFeedback = predictionFeedback;
    }


    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    public Long getLocalizationId() {
        return localizationId;
    }

    public void setLocalizationId(Long localizationId) {
        this.localizationId = localizationId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Long getPredictedPositionId() {
        return predictedPositionId;
    }

    public void setPredictedPositionId(Long predictedPositionId) {
        this.predictedPositionId = predictedPositionId;
    }

    public String getPredictionLabel() {
        return predictionLabel;
    }

    public void setPredictionLabel(String predictionLabel) {
        this.predictionLabel = predictionLabel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPredictionFeedback() {
        return predictionFeedback;
    }

    public void setPredictionFeedback(Integer predictionFeedback) {
        this.predictionFeedback = predictionFeedback;
    }

    @Override
    public String toString() {
        return "Prediction{" +
                "id=" + id +
                ", accuracy=" + accuracy +
                ", localizationId=" + localizationId +
                ", requestId=" + requestId +
                ", providerId=" + providerId +
                ", predictedPositionId=" + predictedPositionId +
                ", predictionLabel='" + predictionLabel + '\'' +
                ", predictionFeedback=" + predictionFeedback +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Prediction that = (Prediction) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        return getRequestId() != null ? getRequestId().equals(that.getRequestId()) : that.getRequestId() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getRequestId() != null ? getRequestId().hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(@NonNull Prediction o) {
        return o.requestId.compareTo(this.requestId);
    }
}
