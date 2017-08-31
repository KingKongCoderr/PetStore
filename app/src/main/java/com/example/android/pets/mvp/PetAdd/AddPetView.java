package com.example.android.pets.mvp.PetAdd;

import com.example.android.pets.mvp.BaseMvp.BaseView;

/**
 * Created by ntankasala on 8/24/17.
 */

public interface AddPetView extends BaseView {

    public void setupSpinner();

    public void savePet();

    public void deletePet();

    public void setActivityTitle(int id);

    public void showInputErrorMessage(int viewId, int resId, int lenghtId);

    public void populatePet(String name, String breed, int gender,int weight);
}
