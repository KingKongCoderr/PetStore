package com.example.android.pets.Injection;

import android.content.Context;

import com.example.android.pets.mvp.PetAdd.AddPetPresenter;
import com.example.android.pets.mvp.PetCatalog.CatalogPresenter;

import dagger.Provides;

/**
 * Created by ntankasala on 8/24/17.
 */
@dagger.Module
public class PresenterModule {
    private Context context;

    public PresenterModule(Context context) {
        this.context = context;
    }

    @Provides
    CatalogPresenter getCatalogPresenter() {
        return new CatalogPresenter(context);
    }

    @Provides
    AddPetPresenter getAddPetPresenter() {
        return new AddPetPresenter();
    }


    @Provides
    Context getContext() {
        return this.context;
    }

}

