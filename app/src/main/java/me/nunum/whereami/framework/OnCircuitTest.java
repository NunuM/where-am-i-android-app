package me.nunum.whereami.framework;

public interface OnCircuitTest {

    /**
     * Invoked in success
     */
    void onConnectionSucceeded();

    /**
     * Invoked on failure
     */
    void onConnectionFailed();
}
