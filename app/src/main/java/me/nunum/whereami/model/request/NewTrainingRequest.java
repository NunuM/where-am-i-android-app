package me.nunum.whereami.model.request;

public final class NewTrainingRequest {

    private final Long algorithmId;

    private final Long providerId;

    public NewTrainingRequest() {
        this(0L, 0L);
    }

    public NewTrainingRequest(Long algorithmId, Long providerId) {
        this.algorithmId = algorithmId;
        this.providerId = providerId;
    }

    public Long getAlgorithmId() {
        return algorithmId;
    }

    public Long getProviderId() {
        return providerId;
    }

    @Override
    public String toString() {
        return "NewTrainingRequest{" +
                "algorithmId=" + algorithmId +
                ", providerId=" + providerId +
                '}';
    }
}
