package com.example.woofsyapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.woofsyapp.database.DatabaseHelper;
import com.example.woofsyapp.model.GenericStringModel;

import java.util.LinkedList;
import java.util.List;

public class AllBreedsDao extends BaseDao<GenericStringModel>{

    private static final String TABLE_ALL_BREEDS = "all_breeds";
    private static final String COL_BREED_NAME = "breed_name";
    public DatabaseHelper dbHelper;
    private Context context;

    public AllBreedsDao(Context context){
        super(context);
        this.context = context;
    }

    public static final String TABLE_ALL_BREEDS_CREATE = "create table if not exists " + TABLE_ALL_BREEDS + "("
            + COL_BREED_NAME + " text not null);";

    public void insertBreedsLocal(List<String> data) {
        dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        for(String s: data){
            values.put(COL_BREED_NAME, s);
            db.insert(TABLE_ALL_BREEDS,null,values);
        }
    }

    public List<String> getBreedsList() {
        List<String> breedsList;
        dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE_ALL_BREEDS,null);
        res.moveToFirst();
        breedsList = convertCursorToBreed(res);
        return breedsList;
    }

    private List<String> convertCursorToBreed(Cursor uCur) {
        List<String> breedsList = new LinkedList<>();
        if (uCur.getCount() > 0) {
            do {
                breedsList.add(uCur.getString(uCur.getColumnIndex(COL_BREED_NAME)));
            } while (uCur.moveToNext());
        }
        uCur.close();
        return breedsList;
    }

}

