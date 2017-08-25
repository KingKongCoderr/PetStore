package com.example.android.pets.mvp.BaseMvp;

/**
 * Created by ntankasala on 8/17/17.
 */

public interface Presenter<T extends BaseView> {
    void attachView(T mvpView);

    void detachView();

}
