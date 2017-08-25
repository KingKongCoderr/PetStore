package com.example.android.pets.Injection;

import android.content.Context;

import com.example.android.pets.mvp.PetCatalog.CatalogPresenter;
import com.example.android.pets.mvp.PetEditor.EditorPresenter;

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
    EditorPresenter getEditorPresenter() {
        return new EditorPresenter(context);
    }

    @Provides
    Context getContext() {
        return this.context;
    }

}

