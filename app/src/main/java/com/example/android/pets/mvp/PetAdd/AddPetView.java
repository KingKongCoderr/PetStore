package com.example.android.pets.mvp.PetAdd;

import com.example.android.pets.mvp.BaseMvp.BaseView;

/**
 * Created by ntankasala on 8/24/17.
 */

public interface AddPetView extends BaseView {

    public void setupSpinner();

    public void insertPet();

    public void deletePet();

    public void populatePet(String name, String breed, int gender,int weight);
}
