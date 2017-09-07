package com.example.android.pets;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.pets.mvp.PetAdd.AddPetActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by nande on 9/5/2017.
 */
@RunWith(AndroidJUnit4.class)
public class AddPetActivityTest {
    
    @Rule
    public ActivityTestRule<AddPetActivity> addPetActivityActivityTestRule =
            new ActivityTestRule<>(AddPetActivity.class);
    
    
    @Test
    public void afterStartedEditing_dialogDisplayed_when_home_or_back_pressed() {
        //find view
        onView(withId(R.id.add_pet_breed))
                .perform(click())
                .perform(closeSoftKeyboard());
        onView(isRoot()).perform(pressBack());
        
        //check assertion
        onView(withText(R.string.discard))
                .check(matches(isDisplayed()));
    }
    
    
    @Test
    public void view_retained_for_activityRecreation() {
        
        assertEquals(false, addPetActivityActivityTestRule.getActivity().ismPetHasChanged());
        
        //find view
        onView(withId(R.id.add_pet_name)).perform(click());
        
        //perform action
        recreate();
        
        //check assertions
        assertEquals(true, addPetActivityActivityTestRule.getActivity().ismPetHasChanged());
    }
    
    public void recreate() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                addPetActivityActivityTestRule.getActivity().recreate();
            }
        });
    }
}
