package com.example.woofsyapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.woofsyapp.database.DatabaseHelper;
import com.example.woofsyapp.model.GenericStringModel;

import java.util.LinkedList;
import java.util.List;

public class LikesDao extends BaseDao<GenericStringModel>{

    private static final String TABLE_LIKES = "tbl_likes";
    private static final String COL_BREED_URL = "breed_url";
    public DatabaseHelper dbHelper;
    private Context context;

    public LikesDao(Context context){
        super(context);
        this.context = context;
    }

    public static final String TABLE_LIKES_CREATE = "create table if not exists " + TABLE_LIKES + "("
            + COL_BREED_URL + " text not null);";

    public boolean insertPhotoLocal(String likedPhoto) {
        DatabaseHelper dbHelper2 = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper2.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BREED_URL, likedPhoto);
        long result = db.insert(TABLE_LIKES,null,values);

        if(result == -1)
            return false;
        else
            return true;
    }

    public void deleteData (String photo) {
        dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

       // final String query = "DELETE FROM " + TABLE_LIKES + " WHERE " + COL_BREED_URL + " =  ?;";
    //    String query = "DELETE FROM " + TABLE_LIKES + " WHERE " + COL_BREED_URL + '" + url + "';";
        String query = "DELETE FROM " + TABLE_LIKES + " WHERE breed_url = '" + photo + "';";
        //"SELECT  * FROM " + DATABASE_TABLE_URLS_ITEMS + " WHERE title = '" + title + "' AND url = '" + url + "';";
        db.execSQL(query);
    }

    public List<String> getLikesList() {
        List<String> likesList;
        dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE_LIKES,null);
        res.moveToFirst();
        likesList = convertCursorToBreed(res);
        return likesList;
    }

    private List<String> convertCursorToBreed(Cursor uCur) {
        List<String> breedsList = new LinkedList<>();
        if (uCur.getCount() > 0) {
            do {
                breedsList.add(uCur.getString(uCur.getColumnIndex(COL_BREED_URL)));
            } while (uCur.moveToNext());
        }
        uCur.close();
        return breedsList;
    }

}