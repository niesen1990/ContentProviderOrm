package com.niesen.cpo.lib.model.map;

import com.niesen.cpo.lib.model.map.types.BooleanType;
import com.niesen.cpo.lib.model.map.types.CalendarType;
import com.niesen.cpo.lib.model.map.types.DateType;
import com.niesen.cpo.lib.model.map.types.DoubleType;
import com.niesen.cpo.lib.model.map.types.FloatType;
import com.niesen.cpo.lib.model.map.types.IntegerType;
import com.niesen.cpo.lib.model.map.types.LongType;
import com.niesen.cpo.lib.model.map.types.ShortType;
import com.niesen.cpo.lib.model.map.types.StringType;
import com.niesen.cpo.lib.model.map.types.UUIDType;

import java.util.ArrayList;
import java.util.List;


/**
 * The factory that will contain all of the available column conversion for the system.
 * This class can be extended, and the class name provided as part of the meta information to load the
 * extended class instead.
 */
public class SqlColumnMappingFactory {

    private final List<SqlColumnMapping> columnMappings;

    public SqlColumnMappingFactory(){

        columnMappings = new ArrayList<SqlColumnMapping>();
        columnMappings.add(new BooleanType());
        columnMappings.add(new CalendarType());
        columnMappings.add(new DateType());
        columnMappings.add(new DoubleType());
        columnMappings.add(new FloatType());
        columnMappings.add(new IntegerType());
        columnMappings.add(new LongType());
        columnMappings.add(new ShortType());
        columnMappings.add(new StringType());
        columnMappings.add(new UUIDType());
    }

    public SqlColumnMapping findColumnMapping(Class<?> fieldType){

        Class<?> fieldTypeWrapped = wrapPrimitives(fieldType);

        for (SqlColumnMapping columnMapping : columnMappings) {

            Class<?> columnType = columnMapping.getJavaType();
            if(columnType.equals(fieldTypeWrapped) || columnType.isAssignableFrom(fieldType))
                return columnMapping;
        }

        throw new IllegalArgumentException("No valid SQL mapping found for type " + fieldType);
    }

    private Class<?> wrapPrimitives(Class fieldType){

        if(!fieldType.isPrimitive()) return fieldType;

        if(long.class.equals(fieldType)) return Long.class;
        if(int.class.equals(fieldType)) return Integer.class;
        if(double.class.equals(fieldType)) return Double.class;
        if(float.class.equals(fieldType)) return Float.class;
        if(short.class.equals(fieldType)) return Short.class;
        if(boolean.class.equals(fieldType)) return Boolean.class;
        if(byte.class.equals(fieldType)) return Byte.class;
        if(void.class.equals(fieldType)) return Void.class;
        if(char.class.equals(fieldType)) return Character.class;

        throw new IllegalArgumentException("No primitive type registered for type " + fieldType);
    }
}
