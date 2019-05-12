package me.nunum.whereami.model.request;


public final class NewLocalizationRequest {

    private String label;

    private boolean isVisible;

    private String user;

    private Double latitude;

    private Double longitude;

    public NewLocalizationRequest() {
        this("", false, "", 0.0, 0.0);
    }

    public NewLocalizationRequest(String label,
                                  boolean isVisible,
                                  String user,
                                  double latitude,
                                  double longitude) {
        this.label = label;
        this.isVisible = isVisible;
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

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
