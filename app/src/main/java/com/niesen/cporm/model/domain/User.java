package com.niesen.cporm.model.domain;

import com.niesen.cpo.lib.model.CPDefaultRecord;
import com.niesen.cpo.lib.model.annotation.Column.Column;
import com.niesen.cpo.lib.model.annotation.Column.Unique;
import com.niesen.cpo.lib.model.annotation.Index;
import com.niesen.cpo.lib.model.annotation.Indices;
import com.niesen.cpo.lib.model.annotation.References;
import com.niesen.cpo.lib.model.annotation.Table;

import java.util.List;


/**
 * Created by hennie.brink on 2015-03-20.
 */
@Table
@Indices(indices = {
        @Index(indexName = "IDX_USERNAME", indexColumns = "user_name")
})
public class User extends CPDefaultRecord<User> {

    @Column
    @Unique
    private String userName;

    @Column
    private String givenName;

    @Column
    private String familyName;

    @Column
    @References(Role.class)
    private long roleId;

    @Column(required = false)
    private List<String> mobileNumbers;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public List<String> getMobileNumbers() {

        return mobileNumbers;
    }

    public void setMobileNumbers(List<String> mobileNumbers) {

        this.mobileNumbers = mobileNumbers;
    }
}
