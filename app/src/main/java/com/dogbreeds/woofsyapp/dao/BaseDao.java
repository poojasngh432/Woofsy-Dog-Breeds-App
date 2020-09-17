package com.dogbreeds.woofsyapp.dao;

import android.content.Context;

import com.dogbreeds.woofsyapp.database.RMSMngr;

public abstract class BaseDao<T> {

    static public RMSMngr objDatabase;
    protected Context mContext;

    static {
        objDatabase = new RMSMngr();
    }

    public BaseDao(final Context context){
        mContext = context;
    }

    protected BaseDao() {
        objDatabase = new RMSMngr();
    }

}
