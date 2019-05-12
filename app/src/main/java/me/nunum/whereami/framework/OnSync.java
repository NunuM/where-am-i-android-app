package me.nunum.whereami.framework;

import me.nunum.whereami.model.Position;

public interface OnSync {
    void batchNumber(Long batch, Position position);

    void failed(Long batch, Throwable throwable);
}
