package com.niesen.cporm.model;

import com.niesen.cpo.lib.model.CPOrmConfiguration;
import com.niesen.cporm.model.domain.Role;
import com.niesen.cporm.model.domain.User;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hennie.brink on 2015-03-20.
 */
public class MyCPOrmConfiguration implements CPOrmConfiguration {

    @Override
    public String getDatabaseName() {

        return "example.db";
    }

    @Override
    public int getDatabaseVersion() {

        return 1;
    }

    @Override
    public boolean isQueryLoggingEnabled() {

        return false;
    }

    @Override
    public List<Class<?>> getDataModelObjects() {
        List<Class<?>> domainObjects = new ArrayList<Class<?>>();
        domainObjects.add(User.class);
        domainObjects.add(Role.class);

        return domainObjects;
    }
}
