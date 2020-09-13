package com.example.woofsyapp.database;

import android.database.sqlite.SQLiteDatabase;
import com.example.woofsyapp.dao.AllBreedsDao;
import com.example.woofsyapp.dao.LikesDao;

public class RMSMngr {
    private static final String TAG = RMSMngr.class.getSimpleName();

    public RMSMngr(){

    }

    public static void createTables(SQLiteDatabase sqLiteDatabase) {
        try{
            sqLiteDatabase.execSQL(AllBreedsDao.TABLE_ALL_BREEDS_CREATE);
            sqLiteDatabase.execSQL(LikesDao.TABLE_LIKES_CREATE);
        }catch (Exception ex) {
        ex.printStackTrace();
        }
    }

}
