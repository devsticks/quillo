package io.quillo.quillo.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import io.quillo.quillo.R;

/**
 * Created by Tom on 2018/02/01.
 */

public class LoadingViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public LoadingViewHolder(View v) {
        super(v);
        progressBar = (ProgressBar) v.findViewById(R.id.searchProgressBar);
    }
}
