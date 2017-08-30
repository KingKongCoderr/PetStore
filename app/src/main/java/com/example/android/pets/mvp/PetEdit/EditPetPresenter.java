package com.example.android.pets.mvp.PetEdit;

import android.content.Context;

import com.example.android.pets.mvp.BaseMvp.BasePresenter;

/**
 * Created by ntankasala on 8/29/17.
 */

public class EditPetPresenter extends BasePresenter<EditPetView> {
    private Context context;

    public EditPetPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void attachView(EditPetView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    @Override
    public void checkViewAttached() {
        super.checkViewAttached();
    }
}
