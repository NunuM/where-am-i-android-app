package me.nunum.whereami.framework;

import me.nunum.whereami.model.Position;

public interface OnSample {
    void started();
    void emitted(boolean wasToOnline, final int samples, Position p);
}
