package com.niesen.cpo.lib.provider.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;

import com.niesen.cpo.lib.model.CPOrmConfiguration;
import com.niesen.cpo.lib.model.generate.TableDetails;
import com.niesen.cpo.lib.model.util.ManifestHelper;
import com.niesen.cpo.lib.model.util.TableDetailsCache;

import java.util.LinkedHashMap;
import java.util.Map;

public class UriMatcherHelper {

    public static int MATCHER_CODE_INTERVALS = 100;
    public static int MATCHER_ALL = 1;
    public static int MATCHER_SINGLE = 2;

    private final Map<Integer, TableDetails> matcherCodes;
    private final String authority;
    private UriMatcher uriMatcher;

    public UriMatcherHelper(Context context) {

        this.matcherCodes = new LinkedHashMap<Integer, TableDetails>();
        authority = ManifestHelper.getAuthority(context);
    }

    public void init(Context context, CPOrmConfiguration CPOrmConfiguration, TableDetailsCache detailsCache) {

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        int matcherInterval = MATCHER_CODE_INTERVALS;

        for (Class<?> dataModelObject : CPOrmConfiguration.getDataModelObjects()) {

            TableDetails tableDetails = detailsCache.findTableDetails(context, dataModelObject);

            matcherCodes.put(matcherInterval, tableDetails);
            uriMatcher.addURI(authority, tableDetails.getTableName(), matcherInterval + MATCHER_ALL);
            uriMatcher.addURI(authority, tableDetails.getTableName() + "/*", matcherInterval + MATCHER_SINGLE);

            matcherInterval += MATCHER_CODE_INTERVALS;
        }
    }

    public TableDetails getTableDetails(Uri uri) {

        int matchCode = uriMatcher.match(uri);
        try {
            return findTableDetails(matchCode);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Could not find table information for Uri, make sure the model factory knows of this table: " + uri, ex);
        }
    }

    public String getType(Uri uri) {

        int matchCode = uriMatcher.match(uri);
        TableDetails tableDetails = findTableDetails(matchCode);
        StringBuilder mimeType = new StringBuilder();
        mimeType.append("android.cursor.");

        if (isSingleItemRequested(matchCode)) mimeType.append(".item");
        else mimeType.append(".dir");
        mimeType.append("/");

        mimeType.append("vnd.");
        mimeType.append(authority);
        if (mimeType.charAt(mimeType.length() - 1) != '.') mimeType.append(".");

        mimeType.append(tableDetails.getTableName());

        return mimeType.toString();
    }

    public boolean isSingleItemRequested(int code) {
        return matcherCodes.containsKey(code - MATCHER_SINGLE);
    }

    public boolean isSingleItemRequested(Uri uri) {
        return isSingleItemRequested(uriMatcher.match(uri));
    }

    public Uri generateItemUri(TableDetails tableDetails) {

        return new Uri.Builder()
                .scheme("content")
                .authority(tableDetails.getAuthority())
                .appendEncodedPath(tableDetails.getTableName())
                .build();

    }

    public Uri generateSingleItemUri(TableDetails tableDetails, String itemId) {

        return new Uri.Builder()
                .scheme("content")
                .authority(tableDetails.getAuthority())
                .appendEncodedPath(tableDetails.getTableName() + "/")
                .appendEncodedPath(itemId)
                .build();

    }

    public Uri generateSingleItemUri(TableDetails tableDetails, long itemId) {

        return ContentUris.withAppendedId(generateItemUri(tableDetails), itemId);

    }

    private TableDetails findTableDetails(int code) {

        if (matcherCodes.containsKey(code)) return matcherCodes.get(code);
        else if (matcherCodes.containsKey(code - MATCHER_ALL))
            return matcherCodes.get(code - MATCHER_ALL);
        else if (matcherCodes.containsKey(code - MATCHER_SINGLE))
            return matcherCodes.get(code - MATCHER_SINGLE);
        else throw new IllegalArgumentException("No URI match found for code: " + code);
    }

    public static Uri.Builder generateItemUri(Context context, TableDetails tableDetails) {

        String authority = tableDetails.getAuthority();
        return new Uri.Builder()
                .scheme("content")
                .authority(authority)
                .appendEncodedPath(tableDetails.getTableName());
    }

    public static Uri.Builder generateItemUri(Context context, TableDetails tableDetails, String itemId) {

        String authority = tableDetails.getAuthority();

        return new Uri.Builder()
                .scheme("content")
                .authority(authority)
                .appendEncodedPath(tableDetails.getTableName())
                .appendEncodedPath(itemId);
    }
}
