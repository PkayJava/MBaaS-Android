package com.angkorteam.mbaas.sdk.android.library;

/**
 * Created by socheat on 4/14/16.
 */
public interface MBaaSOperation {

    public void operationResponse(int operationId, Object object);

    public void operationRetry(int operationId);
}
