package me.nunum.whereami.model.request;

public final class PredictionFeedbackRequest {

    private boolean wasCorrect;

    public PredictionFeedbackRequest() {
        this(false);
    }

    public PredictionFeedbackRequest(boolean wasCorrect) {
        this.wasCorrect = wasCorrect;
    }

    public boolean isWasCorrect() {
        return wasCorrect;
    }

    public void setWasCorrect(boolean wasCorrect) {
        this.wasCorrect = wasCorrect;
    }

    @Override
    public String toString() {
        return "PredictionFeedbackRequest{" +
                "wasCorrect=" + wasCorrect +
                '}';
    }
}
