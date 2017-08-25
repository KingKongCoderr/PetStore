package com.example.android.pets.Injection;

import android.app.Application;

/**
 * Created by ntankasala on 8/24/17.
 */

public class MainApplication extends Application {

    private static ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder().presenterModule(new PresenterModule(this)).build();
    }

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
