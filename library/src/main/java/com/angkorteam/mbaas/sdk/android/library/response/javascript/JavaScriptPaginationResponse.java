package com.angkorteam.mbaas.sdk.android.library.response.javascript;

import com.angkorteam.mbaas.sdk.android.library.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by socheat on 2/27/16.
 */
public class JavaScriptPaginationResponse extends Response<JavaScriptPaginationResponse.Body> {

    public JavaScriptPaginationResponse() {
        this.data = new Body();
    }

    public final static class Body {

        @Expose
        @SerializedName("hasMore")
        private boolean hasMore;

        @Expose
        @SerializedName("pageNumber")
        private int pageNumber;

        @Expose
        @SerializedName("itemPerPage")
        private int itemPerPage;

        @Expose
        @SerializedName("items")
        private Map<String, Object>[] items;

        public boolean hasMore() {
            return hasMore;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public int getItemPerPage() {
            return itemPerPage;
        }

        public Map<String, Object>[] getItems() {
            return items;
        }

    }

}
