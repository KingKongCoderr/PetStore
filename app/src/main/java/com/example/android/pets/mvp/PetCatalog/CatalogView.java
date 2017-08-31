package com.example.android.pets.mvp.PetCatalog;

import android.database.Cursor;

import com.example.android.pets.mvp.BaseMvp.BaseView;

/**
 * Created by ntankasala on 8/24/17.
 */

public interface CatalogView extends BaseView {
    void showCatalog(Cursor cursor);

    void refreshCatalog(Cursor cursor);

    void deleteAllPets();
}
