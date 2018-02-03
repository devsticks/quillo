package io.quillo.quillo.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.ListingAdapter;
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.QuilloDatabase;
import io.quillo.quillo.interfaces.BookmarkListener;
import io.quillo.quillo.interfaces.ListingCellListener;

/**
 * Created by shkla on 2018/01/22.
 */

public class BookmarksFragment extends Fragment implements ListingCellListener, BookmarkListener {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private ListingAdapter adapter;
    private QuilloDatabase quilloDatabase;

    private SwipeRefreshLayout swipeRefreshLayout;

    public static BookmarksFragment newInstance(){
        BookmarksFragment bookmarksFragment = new BookmarksFragment();
        return  bookmarksFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quilloDatabase = new QuilloDatabase();
        adapter = new ListingAdapter(this, getContext(), false);
        setupDatabase();
    }

    public void setupDatabase(){
        quilloDatabase.setBookmarkListener(this);
        quilloDatabase.observeBookmarks();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view  = inflater.inflate(R.layout.fragment_home_search, container, false);
        ButterKnife.bind(this, view);
        setupView(view);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupView(view);
            }
        });

        return view;
    }
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).showBottomNavBar();
    }

    @Override
    public void onListingClick(Listing listing) {
        ((MainActivity)getActivity()).showListingDetailFragment(listing);
    }

    @Override
    public void onUnBookmarkClick(Listing listing) {
        quilloDatabase.removeBookmark(listing);
    }

    @Override
    public void onBookmarkClick(Listing listing) {

    }

    @Override
    public void onBookmarkAdded(Listing listing) {
        listing.setBookmarked(true);
        adapter.addListing(listing);
    }

    @Override
    public void onBookmarkRemoved(String listingUid) {
        adapter.removeListing(listingUid);

    }

    private void setupView(View view){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_white));
        recyclerView.addItemDecoration(itemDecoration);

        onRefreshComplete();
    }

    private void onRefreshComplete() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}
