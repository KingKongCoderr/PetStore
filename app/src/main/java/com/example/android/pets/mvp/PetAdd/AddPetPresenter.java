package com.example.android.pets.mvp.PetAdd;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
    private  Intent mainscreen;

    public AddPetPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void attachView(AddPetView mvpView) {
        super.attachView(mvpView);
        mainscreen = new Intent(context, CatalogActivity.class);
        mainscreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    public void savePet(String name, String breed, String weight_string, int mGender) {
        int weight = 0;
        if (!weight_string.isEmpty()) {
            weight = Integer.parseInt(weight_string);
        }
        if ( TextUtils.isEmpty(name) == false && mGender != 0) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(PetsContract.PetEntry.COLUMN_PET_NAME, name);
            contentValues.put(PetsContract.PetEntry.COLUMN_PET_BREED, breed);
            contentValues.put(PetsContract.PetEntry.COLUMN_PET_GENDER, mGender);
            contentValues.put(PetsContract.PetEntry.COLUMN_PET_WEIGHT, weight);
            if (petUri != null) {
                long petId = ContentUris.parseId(petUri);
                String selection = PetEntry._ID + "=?";
                String selectionArgs[] = new String[]{String.valueOf(petId)};
                int updatedColumnNumber = context
                        .getContentResolver()
                        .update(petUri,contentValues,selection,selectionArgs);
                if(updatedColumnNumber == 1){
                    Toast.makeText(context,"Updated pet with id: " + petId, Toast.LENGTH_SHORT).show();
                    context.startActivity(mainscreen);
                }else {
                    Toast.makeText(context,"error updating pet with id: " + petId, Toast.LENGTH_SHORT).show();

                }
            } else {
                Uri result_uri = context
                        .getContentResolver()
                        .insert(PetsContract.PetEntry.CONTENT_URI, contentValues);
                int id = (int) ContentUris.parseId(result_uri);
                if (!(id == 0)) {
                    Toast.makeText(context, "Inserted pet with id: " + id, Toast.LENGTH_SHORT).show();
                    context.startActivity(mainscreen);
                } else {
                    Toast.makeText(context, "Error saving pet id: ", Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            getMvpView().showInputErrorMessage(R.id.addpet_container,R.string.input_fields_empty_error_message , Snackbar.LENGTH_INDEFINITE );
        }

    }


    public void setUpLoader(LoaderManager loaderManager, Uri contentUris) {

        if (contentUris != null) {
            getMvpView().setActivityTitle(R.string.editor_activity_title_edit_pet);
            petUri = contentUris;
            addPetLoaderMgr = loaderManager;
            addPetLoaderMgr.initLoader(ADDPET_LOADER_CONTENTPROVIDER, null, this);
        } else {
            petUri = null;
            getMvpView().setActivityTitle(R.string.editor_activity_title_new_pet);
        }


    }

    public void deletePet(Uri contenUri){
        if(contenUri != null){
            String selection = PetEntry._ID +"=?";
            long id = ContentUris.parseId(contenUri);
            String selectionArgs[] = new String[]{String.valueOf(id)};
            int deletedColumns = context.getContentResolver().delete(contenUri,selection,selectionArgs);
            if (deletedColumns > 0 ){
                Toast.makeText(context, "Deleted pet with id:" + id, Toast.LENGTH_SHORT).show();
                context.startActivity(mainscreen);
            }
            else{
                Toast.makeText(context, "Error deleting pet", Toast.LENGTH_SHORT).show();
            }
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
            getMvpView().populatePet(name, breed, gender, weight);

        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // If the loader is invalidated, clear out all the data from the input fields.
        getMvpView().populatePet("", "", 0, 0);
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
