package me.nunum.whereami.framework;

/**
 * Created by nuno on 01-11-2017.
 */

public interface Marshaller<Input,Output> {
    Output marshall(Input input);
}
