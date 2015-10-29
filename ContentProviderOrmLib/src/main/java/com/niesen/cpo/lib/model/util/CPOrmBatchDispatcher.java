package com.niesen.cpo.lib.model.util;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;

import com.niesen.cpo.lib.model.CPOrm;
import com.niesen.cpo.lib.model.CPSyncHelper;
import com.niesen.cpo.lib.model.generate.TableDetails;

import java.util.ArrayList;

/**
 * Created by hennie.brink on 2015-05-17.
 */
public class CPOrmBatchDispatcher<T> extends ArrayList<T> {

    private final Context context;
    private final Class<? extends T> insertObject;
    private final int dispatchSize;
    private final Uri itemUri;
    private final TableDetails tableDetails;

    private ContentProviderClient provider;
    private boolean isSync;
    private boolean releaseProvider;

    public CPOrmBatchDispatcher(Context context, Class<? extends T> insertObject, int dispatchSize) {

        this.context = context;
        this.insertObject = insertObject;
        this.dispatchSize = dispatchSize;
        this.itemUri = CPOrm.getItemUri(context, insertObject);
        this.tableDetails = CPOrm.findTableDetails(context, insertObject);

        ensureCapacity(dispatchSize);
    }

    public CPOrmBatchDispatcher(Context context, ContentProviderClient provider, Class<? extends T> insertObject, boolean isSync, int dispatchSize) {

        this(context, insertObject, dispatchSize);
        this.provider = provider;
        this.isSync = isSync;
        releaseProvider = false;
    }

    @Override
    public boolean add(T object) {

        checkSizeAndDispatch();
        return super.add(object);
    }

    private void checkSizeAndDispatch() {

        if (size() >= dispatchSize)
            dispatch();
    }

    public void dispatch() {

        if (isEmpty())
            return;

        if (provider == null) {
            this.provider = context.getContentResolver().acquireContentProviderClient(itemUri);
            releaseProvider = true;
        }

        try {
            if (isSync) CPSyncHelper.insert(context, provider, toArray());
            else {
                ContentValues[] values = ModelInflater.deflateAll(tableDetails, toArray());

                provider.bulkInsert(itemUri, values);
            }
            clear();
        } catch (RemoteException e) {
            release(false);
            throw new CPOrmException("Failed to insert objects", e);
        }
    }

    public void release(boolean dispatchRemaining) {

        if (dispatchRemaining)
            dispatch();

        clear();
        if (releaseProvider)
            provider.release();
    }
}
