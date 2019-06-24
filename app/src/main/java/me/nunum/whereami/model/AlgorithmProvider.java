package me.nunum.whereami.model;

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class AlgorithmProvider {

    private Long id;

    private String method;

    private Boolean isDeployed;

    private Float predictionRate;

    public AlgorithmProvider() {
        this(0L, "", false, 0f);
    }

    private AlgorithmProvider(Long id, String method, Boolean isDeployed, Float predictionRate) {
        this.id = id;
        this.method = method;
        this.isDeployed = isDeployed;
        this.predictionRate = predictionRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Boolean getDeployed() {
        return isDeployed;
    }

    public void setDeployed(Boolean deployed) {
        isDeployed = deployed;
    }

    public Float getPredictionRate() {
        return predictionRate;
    }

    public void setPredictionRate(Float predictionRate) {
        this.predictionRate = predictionRate;
    }

    @Override
    public String toString() {
        return "AlgorithmProvider{" +
                "id=" + id +
                ", method='" + method + '\'' +
                ", isDeployed=" + isDeployed +
                ", predictionRate=" + predictionRate +
                '}';
    }
}
