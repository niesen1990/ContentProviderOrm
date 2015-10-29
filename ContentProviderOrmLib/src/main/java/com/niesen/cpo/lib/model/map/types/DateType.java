package com.niesen.cpo.lib.model.map.types;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import com.niesen.cpo.lib.model.map.SqlColumnMapping;

import java.util.Date;


/**
 * Created by hennie.brink on 2015-03-19.
 */
public class DateType implements SqlColumnMapping {
    @Override
    public Class<?> getJavaType() {
        return Date.class;
    }

    @Override
    public String getSqlColumnTypeName() {
        return "INTEGER";
    }

    @Override
    public Long toSqlType(Object source) {
        return ((Date)source).getTime();
    }

    @Override
    public Object getColumnValue(Cursor cursor, int columnIndex) {
        return new Date(cursor.getLong(columnIndex));
    }

    @Override
    public void setColumnValue(ContentValues contentValues, String key, Object value) {
        contentValues.put(key, toSqlType(value));
    }

    @Override
    public void setBundleValue(Bundle bundle, String key, Cursor cursor, int columnIndex) {
        bundle.putLong(key, cursor.getLong(columnIndex));
    }

    @Override
    public Object getColumnValue(Bundle bundle, String columnName) {

        return new Date(bundle.getLong(columnName));
    }
}
