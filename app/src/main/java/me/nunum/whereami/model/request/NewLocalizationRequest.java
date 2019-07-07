package me.nunum.whereami.model.request;


public final class NewLocalizationRequest {

    private String label;

    private boolean publicForTraining;

    private boolean canOtherUsersSendSamples;

    private boolean publicForPrediction;

    private String user;

    private Double latitude;

    private Double longitude;

    public NewLocalizationRequest() {
        this("", false, false, false, "", 0.0, 0.0);
    }

    public NewLocalizationRequest(String label,
                                  boolean isPublicForTraining,
                                  boolean canOtherUsersSendSamples,
                                  boolean isPublicForPrediction,
                                  String user,
                                  double latitude,
                                  double longitude) {
        this.label = label;
        this.publicForTraining = isPublicForTraining;
        this.canOtherUsersSendSamples = canOtherUsersSendSamples;
        this.publicForPrediction = isPublicForPrediction;
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isPublicForTraining() {
        return publicForTraining;
    }

    public void setPublicForTraining(boolean publicForTraining) {
        this.publicForTraining = publicForTraining;
    }

    public boolean isCanOtherUsersSendSamples() {
        return canOtherUsersSendSamples;
    }

    public void setCanOtherUsersSendSamples(boolean canOtherUsersSendSamples) {
        this.canOtherUsersSendSamples = canOtherUsersSendSamples;
    }

    public boolean isPublicForPrediction() {
        return publicForPrediction;
    }

    public void setPublicForPrediction(boolean publicForPrediction) {
        this.publicForPrediction = publicForPrediction;
    }
}
