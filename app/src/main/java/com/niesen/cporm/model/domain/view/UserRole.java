package com.niesen.cporm.model.domain.view;


import com.niesen.cpo.lib.model.annotation.Column.Column;
import com.niesen.cpo.lib.model.annotation.Column.PrimaryKey;
import com.niesen.cpo.lib.model.annotation.Table;
import com.niesen.cpo.lib.model.generate.TableView;


@Table
public class UserRole implements TableView {

    @Override
    public String getTableViewSql() {

        return "SELECT U._ID, U.USER_NAME, R.ROLE_NAME FROM USER U INNER JOIN ROLE R ON U.ROLE_ID = R._ID";
    }

    @Column
    @PrimaryKey
    private int _id;
    @Column
    private String userName;
    @Column
    private String roleName;


}
