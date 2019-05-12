package me.nunum.whereami.framework;

public interface OnResponse<O> {

    void onSuccess(O o);

    void onFailure(Throwable throwable);
}
