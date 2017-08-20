package com.example.android.pets.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.pets.Data.PetsContract.PetEntry;

/**
 * Created by ntankasala on 8/17/17.
 */

public class PetDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_DATABASE_QUERY = "CREATE TABLE "  + PetEntry.TABLE_NAME  + "("
            + PetEntry._ID + " INTEGER PRIMARY KEY ," + PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL ,"
            + PetEntry.COLUMN_PET_BREED + " TEXT ," + PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL,"
            + PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0"+ ")";

    public PetDbHelper(Context context){
            super(context, DATABASE_NAME, null ,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DATABASE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
