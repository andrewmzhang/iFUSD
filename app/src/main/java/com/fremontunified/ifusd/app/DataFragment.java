package com.fremontunified.ifusd.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class DataFragment extends Fragment implements Response.Listener<Feed>, Response.ErrorListener {
    private Feed feed;

    private VolleySingleton volley;

    private FeedConsumer feedConsumer;
    private boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = inflater.getContext();

        initVolley(context);
        update();
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        volley.stop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FeedConsumer) {
            feedConsumer = (FeedConsumer) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        feedConsumer = null;
    }

    @Override
    public void onResponse(Feed newFeed) {
        this.feed = newFeed;
        if (feedConsumer != null) {
            feedConsumer.setFeed(feed);
        }
        isLoading = false;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (feedConsumer != null) {
            feedConsumer.handleError(error.getLocalizedMessage());
        }
        isLoading = false;
    }

    private void initVolley(Context context) {
        if (volley == null) {
            volley = VolleySingleton.getInstance(context);
        }
    }

    private void update() {
        if (feed == null && !isLoading()) {
            String url = "http://www.fremont.k12.ca.us/site/RSS.aspx?DomainID=1&ModuleInstanceID=4613&PageID=1";
            volley.addToRequestQueue(new RssRequest(Request.Method.GET, url, this, this));
            isLoading = true;
        } else {
            if (feedConsumer != null) {
                feedConsumer.setFeed(feed);
            }
        }
    }

    public boolean isLoading() {
        return isLoading;
    }

}
