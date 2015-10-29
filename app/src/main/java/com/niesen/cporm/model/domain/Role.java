package com.niesen.cporm.model.domain;


import com.niesen.cpo.lib.model.CPDefaultRecord;
import com.niesen.cpo.lib.model.annotation.ChangeListeners;
import com.niesen.cpo.lib.model.annotation.Column.Column;
import com.niesen.cpo.lib.model.annotation.Column.Unique;
import com.niesen.cpo.lib.model.annotation.Table;
import com.niesen.cporm.model.domain.view.UserRole;

/**
 * Created by hennie.brink on 2015-03-20.
 */
@Table
@ChangeListeners(changeListeners = UserRole.class)
public class Role extends CPDefaultRecord<Role> {

    @Column
    @Unique
    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {

        return roleName;
    }
}
