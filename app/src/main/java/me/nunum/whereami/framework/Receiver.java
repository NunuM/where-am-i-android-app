package me.nunum.whereami.framework;


/**
 * Created by nuno on 01-11-2017.
 */

public interface Receiver<T> {
    void receive(T t, Long batchNumber);
}
