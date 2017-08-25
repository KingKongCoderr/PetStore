package com.example.android.pets.Injection;

import com.example.android.pets.mvp.PetCatalog.CatalogActivity;
import com.example.android.pets.mvp.PetEditor.EditorActivity;

import javax.inject.Singleton;

/**
 * Created by ntankasala on 8/24/17.
 */
@dagger.Component(modules = PresenterModule.class)
@Singleton
public interface ApplicationComponent {
    void inject(CatalogActivity catalogActivity);

    void inject(EditorActivity editorActivity);
}
