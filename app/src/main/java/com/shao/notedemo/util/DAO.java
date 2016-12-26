package com.shao.notedemo.util;

import android.content.Context;

import com.shao.notedemo.model.DaoMaster;
import com.shao.notedemo.model.DaoSession;

/**
 * Created by root on 16-12-26.
 */

public class DAO {
    private static DAO instance;
    private DaoSession daoSession;

    private DAO(Context context) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "note.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSession = daoMaster.newSession();
    }

    public static DAO getInstance(Context context) {
        if(instance == null) {
            instance = new DAO(context);
        }
        return instance;
    }

    public DaoSession getSession() {
        return daoSession;
    }
}
