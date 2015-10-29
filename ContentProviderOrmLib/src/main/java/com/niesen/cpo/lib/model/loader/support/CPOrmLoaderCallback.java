package com.niesen.cpo.lib.model.loader.support;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

import com.niesen.cpo.lib.model.query.Select;


/**
 * Created by hennie.brink on 2015-05-11.
 */

public class CPOrmLoaderCallback<Model> implements LoaderManager.LoaderCallbacks<Cursor> {

    private final Context context;
    private final CursorAdapter listAdapter;
    private final Select<Model> select;

    public CPOrmLoaderCallback(Context context, CursorAdapter listAdapter, Select<Model> select) {

        this.context = context;
        this.listAdapter = listAdapter;
        this.select = select;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CPOrmLoader<Model>(context, select);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        listAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        listAdapter.changeCursor(null);
    }
}
