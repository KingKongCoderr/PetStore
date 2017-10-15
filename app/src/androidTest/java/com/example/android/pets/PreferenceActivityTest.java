package com.example.android.pets;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by nande on 10/7/2017.
 */
@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {
    @Rule
    ActivityTestRule<PreferenceActivity> activityActivityTestRule
            = new ActivityTestRule<PreferenceActivity>(PreferenceActivity.class);
    
    
    
}
