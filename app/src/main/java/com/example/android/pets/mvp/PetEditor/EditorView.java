package com.example.android.pets.mvp.PetEditor;

import com.example.android.pets.mvp.BaseMvp.BaseView;

/**
 * Created by ntankasala on 8/24/17.
 */

public interface EditorView extends BaseView {

    public void setupSpinner();

    public void insertPet();

    public void deletePet();
}
