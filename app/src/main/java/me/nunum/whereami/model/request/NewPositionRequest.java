package me.nunum.whereami.model.request;

public final class NewPositionRequest {

    private final String label;

    public NewPositionRequest() {
        this("");
    }

    public NewPositionRequest(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "NewPositionRequest{" +
                "label='" + label + '\'' +
                '}';
    }
}
