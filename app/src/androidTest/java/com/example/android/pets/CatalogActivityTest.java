package com.example.android.pets;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.AdapterView;

import com.example.android.pets.Data.PetsContract;
import com.example.android.pets.mvp.PetAdd.AddPetActivity;
import com.example.android.pets.mvp.PetCatalog.CatalogActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasHost;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasPath;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

/**
 * Created by nande on 9/5/2017.
 */
@RunWith(AndroidJUnit4.class)
public class CatalogActivityTest {
    
    /*@Rule
    public ActivityTestRule<CatalogActivity> catalogActivityActivityTestRule
            = new ActivityTestRule<>(CatalogActivity.class);*/
    @Rule
    public IntentsTestRule<CatalogActivity> catalogActivityIntentsTestRule
            = new IntentsTestRule<>(CatalogActivity.class);
    
    
    @Before
    public void setupIntentStub() {
        
    }
    
    @Test
    public void listView_empty_if_deletedAllPets() {
        
        deleteAllEntries();
        
        //check assertion with custom assertion
        onView(withId(R.id.catalog_lv)).check(new AdapterCountAssertion(0));
        
    }
    
    @Test
    public void emptyView_displayed_for_emptyListView() {
        //find view  onData(object_matcher).dataoptions.perform(viewaction).check(viewassertion)
        //onView(withId(R.id.action_delete_all_entries)).perform(click());
        
        // Open the overflow menu from contextual action mode.
        // openContextualActionModeOverflowMenu();
        
        deleteAllEntries();
        
        //check assertions
        onView(withId(R.id.catalog_lv)).check(matches(not(isCompletelyDisplayed())));
        onView(withId(R.id.empty_view)).check(matches(isCompletelyDisplayed()));
        
        //use this assertion when view is not present in the view hierarchy
        //onView(withId(R.id.catalog_lv)).check(doesNotExist());
    }
    
    public void deleteAllEntries() {
        // Open the options menu OR open the overflow menu, depending on whether
        // the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        
        onView(withText(R.string.action_delete_all_entries)).perform(click());
    }
    
    @Test
    public void listView_displayed_for_dummyData() {
        // Open the overflow menu from contextual action mode.
        //openContextualActionModeOverflowMenu();
        
        insertDummyData();
        
        //check assertions
        onView(withId(R.id.catalog_lv)).check(matches((isCompletelyDisplayed())));
        onView(withId(R.id.empty_view)).check(matches(not(isCompletelyDisplayed())));
        
        //use this assertion when view is not present in the view hierarchy
        //onView(withId(R.id.empty_view)).check(doesNotExist());
    }
    
    @Test
    public void AddPetActivity_called_when_fabClicked() {
        
        insertDummyData();
        
        //find view
        onView(withId(R.id.fab)).perform(click());
        
        //check assertions
        intended(allOf(hasComponent(hasClassName(containsString("AddPetActivity")))));
    }
    
    public void insertDummyData() {
        
        // Open the options menu OR open the overflow menu, depending on whether
        // the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        
        onView(withText(R.string.action_insert_dummy_data)).perform(click());
    }
    
    @Test
    public void AddPetActivity_called_withURI_when_listItemClicked() {
        
        insertDummyData();
        
        //find view
        onData(anything()).inAdapterView(withId(R.id.catalog_lv)).atPosition(0).perform(click());
        
        //check assertions
        intended(
                hasData(allOf(hasHost(containsString(PetsContract.CONTENT_AUTHORITY)),
                        hasPath(containsString(PetsContract.PATH_PETS + "/1")))));
    }
    
    
    static class AdapterCountAssertion implements ViewAssertion {
        private int count;
        
        public AdapterCountAssertion(int i) {
            this.count = i;
        }
        
        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            assertTrue(view instanceof AdapterView);
            assertEquals(count,
                    ((AdapterView) view).getAdapter().getCount());
        }
    }
}
