package com.niesen.cporm;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.niesen.cpo.lib.model.CPOrm;

/**
 * 项目名称：ContentProviderOrm
 * 类描述：
 * 创建人：N.Sun
 * 创建时间：15/10/28 下午12:03
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CPOrm.initialize(this);

        //stecho
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());

    }
}
