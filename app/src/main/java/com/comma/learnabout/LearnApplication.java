package com.comma.learnabout;

import android.app.Application;

import com.comma.learnabout.greendao.DBManager;

/**
 * Created by fanqi on 2018/6/21.
 * Description:
 */

public class LearnApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DBManager.get().init(this);
    }

}
