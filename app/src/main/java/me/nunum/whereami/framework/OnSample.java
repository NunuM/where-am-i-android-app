package me.nunum.whereami.framework;

import me.nunum.whereami.model.Position;

public interface OnSample {
    void emitted(boolean wasToOnline, final int samples, Position p);
}
