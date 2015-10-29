package com.niesen.cpo.lib.model.loader;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.niesen.cpo.lib.model.util.CPOrmCursor;


/**
 *  A cursor adaptor that will automatically handle view and view holder creation.
 *  Extend this class and implement the abstract methods.
 *  T = Domain Model Object
 *  K = View Holder Class
 */
public abstract class CPOrmCursorAdaptor<Model, ViewHolder> extends CursorAdapter {

    private final int layoutId;

    public CPOrmCursorAdaptor(Context context, int layoutId) {

        this(context, null, layoutId);
    }

    public CPOrmCursorAdaptor(Context context, Cursor c, int layoutId) {

        this(context, c, layoutId, 0);
    }

    public CPOrmCursorAdaptor(Context context, Cursor c, int layoutId, int flags) {

        super(context, c, flags);
        this.layoutId = layoutId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        View view = LayoutInflater.from(context).inflate(layoutId, viewGroup, false);
        ViewHolder viewHolder = createViewHolder(view);
        view.setTag(viewHolder);

        bindView(view, context, cursor);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        setViewInformation(viewHolder, ((CPOrmCursor<Model>)cursor).inflate());
    }

    public abstract ViewHolder createViewHolder(View view);

    public abstract void setViewInformation(ViewHolder viewHolder, Model information);

    @Override
    public void changeCursor(Cursor cursor) {

        if(cursor instanceof CPOrmCursor || cursor == null){
            super.changeCursor(cursor);
        }
        else throw new IllegalArgumentException("The cursor is not of the instance " + CPOrmCursor.class.getSimpleName());
    }

    /**
     * Returns the inflated item at the cursor position
     * @param position The position to inflate
     * @return The inflated item if found, null otherwise
     */
    public Model getInflatedItem(int position) {

        CPOrmCursor<Model> cursor = (CPOrmCursor<Model>) getCursor();

        return cursor != null && cursor.moveToPosition(position) ? cursor.inflate() : null;
    }
}
