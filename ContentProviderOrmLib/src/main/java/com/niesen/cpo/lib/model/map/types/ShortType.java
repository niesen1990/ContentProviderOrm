package com.niesen.cpo.lib.model.map.types;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import com.niesen.cpo.lib.model.map.SqlColumnMapping;


/**
 * Created by hennie.brink on 2015-03-19.
 */
public class ShortType implements SqlColumnMapping {
    @Override
    public Class<?> getJavaType() {
        return Short.class;
    }

    @Override
    public String getSqlColumnTypeName() {
        return "INTEGER";
    }

    @Override
    public Object toSqlType(Object source) {
        return source;
    }

    @Override
    public Object getColumnValue(Cursor cursor, int columnIndex) {
        return cursor.getShort(columnIndex);
    }

    @Override
    public void setColumnValue(ContentValues contentValues, String key, Object value) {
        contentValues.put(key, (Short)value);
    }

    @Override
    public void setBundleValue(Bundle bundle, String key, Cursor cursor, int columnIndex) {
        bundle.putShort(key, cursor.getShort(columnIndex));
    }

    @Override
    public Object getColumnValue(Bundle bundle, String columnName) {

        return bundle.getShort(columnName);
    }
}
