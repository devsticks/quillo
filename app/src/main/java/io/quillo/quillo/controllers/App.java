package io.quillo.quillo.controllers;

import android.app.Application;

import com.bumptech.glide.request.target.ViewTarget;

import io.quillo.quillo.R;

/**
 * Created by shkla on 2018/02/06.
 */

public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();
        ViewTarget.setTagId(R.id.glide_tag);
    }
}
