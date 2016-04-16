package com.angkorteam.mbaas.sdk.android.library;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.angkorteam.mbaas.sdk.android.library.response.javascript.JavaScriptExecuteResponse;
import com.angkorteam.mbaas.sdk.android.library.response.javascript.JavaScriptPaginationResponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by socheat on 4/16/16.
 */
public abstract class MBaaSAdapter extends RecyclerView.Adapter implements NetworkBroadcastReceiver.NetworkReceiver {

    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROGRESS = 0;

    private final Map<Integer, Map<String, Object>> items;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem = 0;
    private int totalItemCount = 0;
    private boolean loading = false;
    private boolean hasMore = false;
    private long pageNumber = 0;

    private final LinearLayoutManager layoutManager;
    private final Activity activity;
    private final String javascript;
    private final NetworkBroadcastReceiver broadcastReceiver;
    private long currentPosition;

    private int EVENT_DISPLAY = 1;
    private int EVENT_LOAD_MORE = 2;

    private final RecyclerView recyclerView;

    private final RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            MBaaSAdapter.this.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            MBaaSAdapter.this.onScrolled(recyclerView, dx, dy);
        }
    };

    public MBaaSAdapter(Activity activity, NetworkBroadcastReceiver broadcastReceiver, String javascript, RecyclerView recyclerView) {
        this.activity = activity;
        this.javascript = javascript;
        this.broadcastReceiver = broadcastReceiver;
        this.broadcastReceiver.setNetworkReceiver(this);
        this.recyclerView = recyclerView;
        this.items = new LinkedHashMap<>();
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            this.layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                recyclerView.addOnScrollListener(scrollListener);
            }
        } else {
            this.layoutManager = null;
        }
        MBaaSApplication application = null;
        if (activity.getApplicationContext() instanceof MBaaSApplication) {
            application = (MBaaSApplication) activity.getApplicationContext();
        }
        if (application != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("pageNumber", pageNumber + 1);
            Call<JavaScriptExecuteResponse> responseCall = application.getMBaaSClient().javascriptExecutePost(this.javascript, params);
            responseCall.enqueue(new MBaaSCallback<JavaScriptExecuteResponse>(EVENT_DISPLAY, this.activity, this.broadcastReceiver));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.i("MBaaS", "position " + position);
        return this.items.get(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public Map<String, Object> getItem(int position) {
        return this.items.get(position);
    }

    /**
     * Callback method to be invoked when RecyclerView's scroll state changes.
     *
     * @param recyclerView The RecyclerView whose scroll state has changed.
     * @param newState     The updated scroll state. One of {@link RecyclerView#SCROLL_STATE_IDLE},
     *                     {@link RecyclerView#SCROLL_STATE_DRAGGING} or {@link RecyclerView#SCROLL_STATE_SETTLING}.
     */
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

    }

    /**
     * Callback method to be invoked when the RecyclerView has been scrolled. This will be
     * called after the scroll has completed.
     * <p/>
     * This callback will also be called if visible item range changes after a layout
     * calculation. In that case, dx and dy will be 0.
     *
     * @param recyclerView The RecyclerView which scrolled.
     * @param dx           The amount of horizontal scroll.
     * @param dy           The amount of vertical scroll.
     */
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        if (this.layoutManager != null) {
            totalItemCount = this.layoutManager.getItemCount();
            lastVisibleItem = this.layoutManager.findLastVisibleItemPosition();

            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                // End has been reached
                // Do something

                if (hasMore) {
                    loadMore();
                }
                loading = true;
            }
        }
    }

    protected void loadMore() {

        items.put(items.size(), null);
        this.notifyItemInserted(items.size()-1);
        MBaaSApplication application = null;
        if (activity.getApplicationContext() instanceof MBaaSApplication) {
            application = (MBaaSApplication) activity.getApplicationContext();
        }
        if (application != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("pageNumber", pageNumber + 1);
            Call<JavaScriptExecuteResponse> responseCall = application.getMBaaSClient().javascriptExecutePost(this.javascript, params);
            responseCall.enqueue(new MBaaSCallback<JavaScriptExecuteResponse>(EVENT_LOAD_MORE, this.activity, this.broadcastReceiver));
        }
    }

    @Override
    public void onResponse(int eventId, String json) {
        Gson gson = new Gson();
        if (eventId == EVENT_DISPLAY) {
            JavaScriptPaginationResponse response = gson.fromJson(json, JavaScriptPaginationResponse.class);
            this.pageNumber = response.getData().getPageNumber();
            int index = this.items.size() - 1;
            for (Map<String, Object> item : response.getData().getItems()) {
                index = index + 1;

                this.items.put(index, item);
            }
            this.hasMore = response.getData().hasMore();

            recyclerView.setAdapter(this);
        } else if (eventId == EVENT_LOAD_MORE) {

            items.remove(items.size()-1);
            this.notifyItemRemoved(items.size());
            JavaScriptPaginationResponse response = gson.fromJson(json, JavaScriptPaginationResponse.class);
            this.pageNumber = response.getData().getPageNumber();
            int index = this.items.size() - 1;
            for (Map<String, Object> item : response.getData().getItems()) {
                index = index + 1;
                this.items.put(index, item);
            }
            this.hasMore = response.getData().hasMore();
            this.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(int operationId, String message) {

    }

    @Override
    public void onUnauthorized(int operationId) {

    }
}
