package com.comma.learnabout.greendao;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

/**
 * Created by fanqi on 2018/4/2.
 * Description:
 */

public class DBManager {

    private static final String DB_NAME = "learnapp.db";
    private FileInfoDao fileInfoDao;
    private ThreadInfoDao threadInfoDao;
    private DownLoadInfoDao downLoadInfoDao;

    public synchronized static DBManager get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static DBManager instance = new DBManager();
    }

    public void  init(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        fileInfoDao = daoSession.getFileInfoDao();
        threadInfoDao=daoSession.getThreadInfoDao();
        downLoadInfoDao=daoSession.getDownLoadInfoDao();
    }

    private DBManager() {
    }

    public FileInfoDao getFileInfoDao() {
        return fileInfoDao;
    }

    public synchronized ThreadInfoDao getThreadInfoDao(){
        return threadInfoDao;
    }

    public DownLoadInfoDao getDownLoadInfoDao(){return downLoadInfoDao;}
}
