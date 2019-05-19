package me.nunum.whereami.model.request;

public final class NewTrainingRequest {

    private final Long algorithmId;

    public NewTrainingRequest() {
        this(1L);
    }

    public NewTrainingRequest(Long algorithmId) {
        this.algorithmId = algorithmId;
    }

    public Long getAlgorithmId() {
        return algorithmId;
    }

    @Override
    public String toString() {
        return "NewTrainingRequestFragment{" +
                "algorithmId=" + algorithmId +
                '}';
    }
}
