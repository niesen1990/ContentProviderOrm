package com.niesen.cpo.lib.model;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;

import com.niesen.cpo.lib.model.generate.TableDetails;
import com.niesen.cpo.lib.model.util.ModelInflater;
import com.niesen.cpo.lib.provider.CPOrmContentProvider;
import com.niesen.cpo.lib.provider.util.UriMatcherHelper;


/**
 * This class contains basic helper functions to assist with data synchronization.  What
 * differentiates this class from {@link CPOrm} is that all
 * operations will be passed with a special parameter telling the content provider
 * that a network sync should not be scheduled when notifying content observers.
 */
public class CPSyncHelper {

    public static <T> void insert(Context context, ContentProviderClient provider, T... dataModelObjects) throws RemoteException {

        if (dataModelObjects.length == 1) {

            T modelObject = dataModelObjects[0];
            TableDetails tableDetails = CPOrm.findTableDetails(context, modelObject.getClass());
            ContentValues contentValues = ModelInflater.deflate(tableDetails, modelObject);
            Uri insertUri = UriMatcherHelper.generateItemUri(context, tableDetails)
                    .appendQueryParameter(CPOrmContentProvider.PARAMETER_SYNC, "false").build();

            provider.insert(insertUri, contentValues);
        } else {

            TableDetails tableDetails = CPOrm.findTableDetails(context, dataModelObjects[0].getClass());
            ContentValues[] insertObjects = ModelInflater.deflateAll(tableDetails, dataModelObjects);

            if (tableDetails != null) {
                Uri insertUri = UriMatcherHelper.generateItemUri(context, tableDetails)
                        .appendQueryParameter(CPOrmContentProvider.PARAMETER_SYNC, "false").build();
                provider.bulkInsert(insertUri, insertObjects);
            }
        }
    }

    public static <T> T insertAndReturn(Context context, ContentProviderClient provider, T dataModelObject) throws RemoteException {
        TableDetails tableDetails = CPOrm.findTableDetails(context, dataModelObject.getClass());
        ContentValues contentValues = ModelInflater.deflate(tableDetails, dataModelObject);
        Uri insertUri = UriMatcherHelper.generateItemUri(context, tableDetails)
                .appendQueryParameter(CPOrmContentProvider.PARAMETER_SYNC, "false").build();
        Uri itemUri = provider.insert(insertUri, contentValues);
        return CPOrm.findSingleItem(context, itemUri, tableDetails);
    }

    public static <T> void update(Context context, ContentProviderClient provider, T dataModelObject) throws RemoteException {
        TableDetails tableDetails = CPOrm.findTableDetails(context, dataModelObject.getClass());
        ContentValues contentValues = ModelInflater.deflate(tableDetails, dataModelObject);
        Object columnValue = ModelInflater.deflateColumn(tableDetails, tableDetails.findPrimaryKeyColumn(), dataModelObject);
        Uri itemUri = UriMatcherHelper.generateItemUri(context, tableDetails, String.valueOf(columnValue))
                .appendQueryParameter(CPOrmContentProvider.PARAMETER_SYNC, "false").build();
        provider.update(itemUri, contentValues, null, null);
    }

    public static <T> void updateColumns(Context context, ContentProviderClient provider, T dataModelObject, String... columns) throws RemoteException {
        TableDetails tableDetails = CPOrm.findTableDetails(context, dataModelObject.getClass());
        ContentValues contentValues = ModelInflater.deflate(tableDetails, dataModelObject);
        Object columnValue = ModelInflater.deflateColumn(tableDetails, tableDetails.findPrimaryKeyColumn(), dataModelObject);
        Uri itemUri = UriMatcherHelper.generateItemUri(context, tableDetails, String.valueOf(columnValue))
                .appendQueryParameter(CPOrmContentProvider.PARAMETER_SYNC, "false").build();

        for (String contentColumn : tableDetails.getColumnNames()) {

            boolean includeColumn = false;
            for (String column : columns) {
                if (contentColumn.equals(column)) {
                    includeColumn = true;
                    break;
                }
            }

            if (!includeColumn)
                contentValues.remove(contentColumn);
        }

        provider.update(itemUri, contentValues, null, null);
    }

    public static <T> void updateColumnsExcluding(Context context, ContentProviderClient provider, T dataModelObject, String... columnsToExclude) throws RemoteException {

        TableDetails tableDetails = CPOrm.findTableDetails(context, dataModelObject.getClass());
        ContentValues contentValues = ModelInflater.deflate(tableDetails, dataModelObject);
        Object columnValue = ModelInflater.deflateColumn(tableDetails, tableDetails.findPrimaryKeyColumn(), dataModelObject);
        Uri itemUri = UriMatcherHelper.generateItemUri(context, tableDetails, String.valueOf(columnValue))
                .appendQueryParameter(CPOrmContentProvider.PARAMETER_SYNC, "false").build();

        for (String columnToExclude : columnsToExclude) {

            contentValues.remove(columnToExclude);
        }

        provider.update(itemUri, contentValues, null, null);

    }

    public static <T> void delete(Context context, ContentProviderClient provider, T dataModelObject) throws RemoteException {

        TableDetails tableDetails = CPOrm.findTableDetails(context, dataModelObject.getClass());
        Object columnValue = ModelInflater.deflateColumn(tableDetails, tableDetails.findPrimaryKeyColumn(), dataModelObject);
        Uri itemUri = UriMatcherHelper.generateItemUri(context, tableDetails, String.valueOf(columnValue))
                .appendQueryParameter(CPOrmContentProvider.PARAMETER_SYNC, "false").build();

        provider.delete(itemUri, null, null);

    }
}
