package com.niesen.cpo.lib.model.util;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.LruCache;

import com.niesen.cpo.lib.model.generate.TableDetails;

import java.lang.ref.SoftReference;


/**
 * This class is a wrapper for cursor returned by the ORM, it has some helper methods like inflating to an object from the cursor
 */
public class CPOrmCursor<T> extends CursorWrapper {

    private final TableDetails tableDetails;
    private LruCache<Integer, SoftReference<T>> objectCache;

    public CPOrmCursor(TableDetails tableDetails, Cursor cursor) {
        super(cursor);
        this.tableDetails = tableDetails;
    }

    public CPOrmCursor(TableDetails tableDetails, Cursor cursor, int cacheSize) {
        this(tableDetails, cursor);
        enableCache(cacheSize);
    }

    /**
     * Uses an LRU cache to store some of the objects returned by this cursor.  This can be help full if some objects
     * contain lazy initialized values, and it improves performance.
     * @param size The size of the cache to create
     */
    public void enableCache(int size) {

        if(size > 0)
            this.objectCache = new LruCache<>(size);
    }

    /**
     * Initializes the cache with the cursor's count.  The cached values are
     * stored using soft references, and should not cause any memory issues,
     * but setting the size is the preferred way of enabling the cache.
     * @see #enableCache(int)
     */
    public void enableCache() {

        enableCache(getCount());
    }

    public boolean isCacheEnabled(){
        return objectCache != null;
    }

    /**
     * Inflates an object at the current cursor position.  If the cache is enabled, and the object exists in the
     * cache, then that object wil be returned, otherwise it is inflated and added to the cache before returning.
     * @return The inflated object.
     */
    public T inflate(){
        return getObjectFromCacheOrInflate();
    }

    /**
     * @return The table details that is used to construct the object
     */
    public TableDetails getTableDetails() {
        return tableDetails;
    }

    /**
     * Attempts to retrieve an object from the cache, if it does not exist in the cache, and the cache is enabled, then
     * the object will be inflated and added to cache before returning.
     * @return The inflated object
     */
    private T getObjectFromCacheOrInflate() {

        if(objectCache == null) return ModelInflater.inflate(this, tableDetails);

        SoftReference<T> objectReference = objectCache.get(getPosition());

        T cachedObject = null;
        if(objectReference != null) cachedObject = objectReference.get();

        if(cachedObject == null) cachedObject = insertCacheObject();

        return cachedObject;
    }

    /**
     * Inflates the object at the current cursor position, and inserts it into the cache with the position as ID
     * @return The inflated object
     */
    private T insertCacheObject() {

        T cachedObject = ModelInflater.inflate(this, tableDetails);
        objectCache.put(getPosition(), new SoftReference<T>(cachedObject));

        return cachedObject;
    }
}
