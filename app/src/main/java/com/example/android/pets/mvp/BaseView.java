package com.example.android.pets.mvp;

/**
 * Created by ntankasala on 8/17/17.
 */

public interface BaseView<T> {
     void setPresenter(T presenter);
}