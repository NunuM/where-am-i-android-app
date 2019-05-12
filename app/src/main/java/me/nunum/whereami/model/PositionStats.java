package me.nunum.whereami.model;

public class PositionStats {


    private Integer routers;

    private Integer samples;

    private Integer networks;

    private String strongestSignal;

    public PositionStats() {
    }

    public PositionStats(Integer routers, Integer samples, Integer networks, String strongestSignal) {
        this.routers = routers;
        this.samples = samples;
        this.networks = networks;
        this.strongestSignal = strongestSignal;
    }

    public Integer getRouters() {
        return routers;
    }

    public void setRouters(Integer routers) {
        this.routers = routers;
    }

    public Integer getSamples() {
        return samples;
    }

    public void setSamples(Integer samples) {
        this.samples = samples;
    }

    public Integer getNetworks() {
        return networks;
    }

    public void setNetworks(Integer networks) {
        this.networks = networks;
    }

    public String getStrongestSignal() {
        return strongestSignal;
    }

    public void setStrongestSignal(String strongestSignal) {
        this.strongestSignal = strongestSignal;
    }

}
