package com.example.android.pets.mvp.PetAdd;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.android.pets.Data.PetsContract;
import com.example.android.pets.R;
import com.example.android.pets.mvp.BaseMvp.BasePresenter;
import com.example.android.pets.mvp.PetCatalog.CatalogActivity;

import static com.example.android.pets.Data.PetsContract.PetEntry;

/**
 * Created by ntankasala on 8/24/17.
 */

public class AddPetPresenter extends BasePresenter<AddPetView> implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ADDPET_LOADER_CONTENTPROVIDER = 2;

    private Context context;
    private LoaderManager addPetLoaderMgr;
    private CursorLoader addPetCursorLoader;
    private Uri petUri;

    public AddPetPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void attachView(AddPetView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public int getGender(String selection) {
        if (!TextUtils.isEmpty(selection)) {
            if (selection.equals(context.getString(R.string.gender_male))) {
                return PetsContract.PetEntry.GENDER_MALE; // Male
            } else if (selection.equals(context.getString(R.string.gender_female))) {
                return PetsContract.PetEntry.GENDER_FEMALE; // Female
            } else {
                return PetsContract.PetEntry.GENDER_UNKNOWN; // Unknown
            }
        } else {
            return 0;
        }
    }

    public void insertPet(String name, String breed, String weight_string, int mGender) {
        int weight = 0;
        if (!weight_string.isEmpty()) {
            weight = Integer.parseInt(weight_string);
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_NAME, name);
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_BREED, breed);
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_GENDER, mGender);
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_WEIGHT, weight);
        if (name != null && breed != null && weight != 0 && mGender != 0) {
            Uri result_uri = context.getContentResolver().insert(PetsContract.PetEntry.CONTENT_URI, contentValues);
            int id = (int) ContentUris.parseId(result_uri);
            if (!(id == 0)) {
                Toast.makeText(context, "Inserted pet with id: " + id, Toast.LENGTH_SHORT).show();
                Intent mainscreen = new Intent(context, CatalogActivity.class);
                mainscreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(mainscreen);
            } else {
                Toast.makeText(context, "Error saving pet id: ", Toast.LENGTH_SHORT).show();
            }
        }

    }


    public void setUpLoader(LoaderManager loaderManager, Uri contentUris) {

        if (contentUris != null) {
            petUri = contentUris;
            addPetLoaderMgr = loaderManager;
            addPetLoaderMgr.initLoader(ADDPET_LOADER_CONTENTPROVIDER, null, this);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return loader_contentProvider();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (data.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = data.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = data.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = data.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = data.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            // Extract out the value from the Cursor for the given column index
            String name = data.getString(nameColumnIndex);
            String breed = data.getString(breedColumnIndex);
            int gender = data.getInt(genderColumnIndex);
            int weight = data.getInt(weightColumnIndex);

            getMvpView().populatePet(name,breed, gender,weight);

        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // If the loader is invalidated, clear out all the data from the input fields.
        getMvpView().populatePet("","",0,0);
    }

    private CursorLoader loader_contentProvider() {
        String projection[] = new String[]{
                PetsContract.PetEntry._ID, PetsContract.PetEntry.COLUMN_PET_NAME, PetsContract.PetEntry.COLUMN_PET_BREED
                , PetsContract.PetEntry.COLUMN_PET_GENDER, PetsContract.PetEntry.COLUMN_PET_WEIGHT
        };
        addPetCursorLoader = new CursorLoader(context, petUri, projection, null, null, null);
        return addPetCursorLoader;
    }
}
