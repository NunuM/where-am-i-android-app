package me.nunum.whereami.framework;

/**
 * Created by nuno on 01-11-2017.
 */

public interface Emitter<T> {

    /**
     * Send data in batches to the receiver.
     *
     * @param receiver
     */
    void emitTo(Receiver<T> receiver);
}
