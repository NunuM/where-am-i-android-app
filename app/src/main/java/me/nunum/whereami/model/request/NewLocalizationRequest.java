package me.nunum.whereami.model.request;


public final class NewLocalizationRequest {

    private String label;

    private boolean isPublic;

    private String user;

    private Double latitude;

    private Double longitude;

    public NewLocalizationRequest() {
        this("", false, "", 0.0, 0.0);
    }

    public NewLocalizationRequest(String label,
                                  boolean isPublic,
                                  String user,
                                  double latitude,
                                  double longitude) {
        this.label = label;
        this.isPublic = isPublic;
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

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
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
