package com.example.android.pets.mvp.PetEditor;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.android.pets.Data.PetsContract;
import com.example.android.pets.R;
import com.example.android.pets.mvp.BaseMvp.BasePresenter;
import com.example.android.pets.mvp.PetCatalog.CatalogActivity;

/**
 * Created by ntankasala on 8/24/17.
 */

public class EditorPresenter extends BasePresenter<EditorView> {

    private Context context;

    public EditorPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void attachView(EditorView mvpView) {
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
                mainscreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainscreen);
            } else {
                Toast.makeText(context, "Error saving pet id: ", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
