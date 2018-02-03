package io.quillo.quillo.interfaces;

/**
 * Created by Tom on 2018/01/31.
 */

public interface PasswordListener{
    public void onPasswordLoaded(String password);
    public void onPasswordLoadFailed();

}
