package com.niesen.cpo.lib.model.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import com.niesen.cpo.lib.model.generate.TableDetails;

import java.util.List;


/**
 * Handles the inflation and deflation of Java objects to and from content values/cursors
 */
public class ModelInflater {

    public static Object deflateColumn(TableDetails tableDetails, TableDetails.ColumnDetails columnDetails, Object dataModelObject) {

        try {

            Object value = columnDetails.getColumnField().get(dataModelObject);

            if (value == null) return null;
            else return columnDetails.getColumnTypeMapping().toSqlType(value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Unable to access protected field, change the access level: " + columnDetails.getColumnName());
        }
    }

    public static ContentValues deflate(TableDetails tableDetails, Object dataModelObject) {

        List<TableDetails.ColumnDetails> columns = tableDetails.getColumns();
        int size = columns.size();
        ContentValues contentValues = new ContentValues(size);

        for (int i = 0; i < size; i++) {
            TableDetails.ColumnDetails columnDetails = columns.get(i);

            if (columnDetails.isAutoIncrement()) continue;

            try {

                columnDetails.setContentValue(contentValues, dataModelObject);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to access protected field, change the access level: " + columnDetails.getColumnName());
            }
        }

        return contentValues;
    }

    public static ContentValues[] deflateAll(TableDetails tableDetails, Object... dataModelObjects) {

        List<TableDetails.ColumnDetails> columns = tableDetails.getColumns();
        ContentValues[] contentValuesArray = new ContentValues[dataModelObjects.length];

        int size = columns.size();
        for (int i = 0; i < dataModelObjects.length; i++) {

            contentValuesArray[i] = new ContentValues(size);
        }

        for (int i = 0; i < size; i++) {

            TableDetails.ColumnDetails columnDetails = columns.get(i);

            if (columnDetails.isAutoIncrement()) continue;

            int length = dataModelObjects.length;
            for (int j = 0; j < length; j++) {

                try {

                    columnDetails.setContentValue(contentValuesArray[j], dataModelObjects[j]);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Unable to access protected field, change the access level: " + columnDetails.getColumnName());
                }
            }
        }

        return contentValuesArray;
    }

    public static <T> T inflate(Bundle bundle, TableDetails tableDetails) {

        T dataModelObject;

        try {
            dataModelObject = (T) tableDetails.createNewModelInstance();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Could not create a new instance of data model object: " + tableDetails.getTableName());
        }

        for (TableDetails.ColumnDetails columnDetails : tableDetails.getColumns()) {

            inflateColumn(bundle, dataModelObject, columnDetails);
        }

        return dataModelObject;
    }

    public static <T> T inflate(Cursor cursor, TableDetails tableDetails) {

        T dataModelObject;

        try {
            dataModelObject = (T) tableDetails.createNewModelInstance();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Could not create a new instance of data model object: " + tableDetails.getTableName());
        }

        int columnCount = cursor.getColumnCount();
        for (int i = 0; i < columnCount; i++) {

            String columnName = cursor.getColumnName(i);
            TableDetails.ColumnDetails columnDetails = tableDetails.findColumn(columnName);
            inflateColumn(cursor, dataModelObject, columnDetails, i);

        }

        return dataModelObject;
    }

    private static <T> void inflateColumn(Cursor cursor, T dataModelObject, TableDetails.ColumnDetails columnDetails, int columnIndex) {

        //If the column details is not required, then check if it is null
        if (!columnDetails.isRequired() && cursor.isNull(columnIndex)) {
            return;
        }

        try {
            columnDetails.setFieldValue(cursor, columnIndex, dataModelObject);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Not allowed to alter the value of the field, please change the access level: " + columnDetails.getColumnName());
        }
    }

    private static <T> void inflateColumn(Bundle bundle, T dataModelObject, TableDetails.ColumnDetails columnDetails) {

        //If the column details is not required, then check if it is null
        if (!bundle.containsKey(columnDetails.getColumnName())) {
            return;
        }

        try {
            columnDetails.setFieldValue(bundle, columnDetails.getColumnName(), dataModelObject);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Not allowed to alter the value of the field, please change the access level: " + columnDetails.getColumnName());
        }
    }
}
