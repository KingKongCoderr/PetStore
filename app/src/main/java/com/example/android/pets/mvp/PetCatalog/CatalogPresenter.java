package com.example.android.pets.mvp.PetCatalog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.pets.Data.PetsContract;
import com.example.android.pets.mvp.BaseMvp.BasePresenter;

/**
 * Created by ntankasala on 8/24/17.
 */

public class CatalogPresenter extends BasePresenter<CatalogView> {
    Context context;
    Cursor cursor;

    public CatalogPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void attachView(CatalogView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void insertDummyData() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_NAME, "TOTO");
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_BREED, "Terrier");
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_GENDER, 1);
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_WEIGHT, 7);
        try {
            context.getContentResolver().insert(PetsContract.PetEntry.CONTENT_URI, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            contentValues.clear();
            getMvpView().refreshCatalog(getDataBaseInfo());
        }

    }

    public void getCatalog(){
        getMvpView().showCatalog(getDataBaseInfo());
    }

    public Cursor getDataBaseInfo() {
        String projection[] = new String[]{
                PetsContract.PetEntry._ID, PetsContract.PetEntry.COLUMN_PET_NAME, PetsContract.PetEntry.COLUMN_PET_BREED
                , PetsContract.PetEntry.COLUMN_PET_GENDER, PetsContract.PetEntry.COLUMN_PET_WEIGHT
        };
        Uri uri = PetsContract.PetEntry.CONTENT_URI;
        cursor = context.getContentResolver().query(uri, projection, null, null, null);
        return cursor;

        /*
        Shows passing a cursor using petentry table constants.
        int id_index = cursor.getColumnIndex(PetsContract.PetEntry._ID);
        int name_index = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_NAME);
        int breed_index = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_BREED);
        int gender_index = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_GENDER);
        int weight_index = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_WEIGHT);
        getMvpView().displayTableCount("Number of rows in pets database table: " + cursor.getCount());
        getMvpView().displayColumnHeaders("\n" + "_id:  " + "name: " + "breed: " + "gender: " + "weight: ");
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            while (cursor.moveToNext()) {
                int id = cursor.getInt(id_index);
                String name = cursor.getString(name_index);
                String breed = cursor.getString(breed_index);
                int gender = cursor.getInt(gender_index);
                int weight = cursor.getInt(weight_index);
                getMvpView().displayDatabaseInfo(id, name, breed, gender, weight);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }*/

    }

}
