package com.example.android.pets.mvp.PetCatalog;

import com.example.android.pets.mvp.BaseMvp.BaseView;

/**
 * Created by ntankasala on 8/24/17.
 */

public interface CatalogView extends BaseView {
    void displayDatabaseInfo(int id, String name, String breed, int gender, int weight);

    void displayTableCount(String count);

    void displayColumnHeaders(String header);

}
