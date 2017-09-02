package com.example.android.pets.mvp.BaseMvp;

import android.content.Context;

/**
 * Created by ntankasala on 8/17/17.
 */

public interface Presenter<T extends BaseView> {
    void attachView(T mvpView, Context context);

    void detachView();

}
