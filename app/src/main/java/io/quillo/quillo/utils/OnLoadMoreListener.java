package io.quillo.quillo.utils;

/**
 * Created by Tom on 2018/01/31.
 */

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class OnLoadMoreListener extends RecyclerView.OnScrollListener {
    private int mPreviousTotal = 0;
    private boolean mLoading = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleThreshold = 5;
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();
        int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

        if (lastVisibleItem == totalItemCount - 1 &&
                totalItemCount > visibleThreshold) {
            onLoadMore();
        }
    }

    public abstract void onLoadMore();
}
