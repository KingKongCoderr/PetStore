/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets.mvp.PetAdd;


import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.pets.Data.PetsContract;
import com.example.android.pets.Injection.MainApplication;
import com.example.android.pets.R;
import com.example.android.pets.mvp.PetCatalog.CatalogActivity;

import java.util.List;

import javax.inject.Inject;


/**
 * Allows user to create a new pet or edit an existing one.
 */
public class AddPetActivity extends AppCompatActivity implements AddPetView {
    
    @Inject
    AddPetPresenter addPetPresenter;
    
    /**
     * EditText field to enter the pet's name
     */
    private EditText mNameEditText;
    
    /**
     * EditText field to enter the pet's breed
     */
    private EditText mBreedEditText;
    
    /**
     * EditText field to enter the pet's weight
     */
    private EditText mWeightEditText;
    
    /**
     * EditText field to enter the pet's gender
     */
    private Spinner mGenderSpinner;
    
    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;
    
    private boolean mPetHasChanged = false;
    
    public boolean ismPetHasChanged() {
        return mPetHasChanged;
    }
    
    private static String TAG = "AddPetActivity";
    
    Uri content_uri;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_add_pet);
        MainApplication.getApplicationComponent().inject(this);
        Log.d(TAG, "on Create");
        addPetPresenter.attachView(this, this);
        
        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.add_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.add_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.add_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.addpet_spinner_gender);
        
        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);
        setupSpinner();
        content_uri = getIntent().getData();
        if(content_uri!=null) {
            content_uri = deepLinkSetUp(content_uri);
        }
        addPetPresenter.setUpLoader(getSupportLoaderManager(), content_uri);
    }
    private int changeTheme() {
        int colorId = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(AddPetActivity.this).getString(getString(R.string.color_preference_key),getString(R.string.color_default_value)));
        boolean switchValue = PreferenceManager.getDefaultSharedPreferences(AddPetActivity.this)
                .getBoolean(getString(R.string.textColor_preference_key),getResources().getBoolean(R.bool.textColor_switch_default));
        switch (colorId) {
            case 0:
                getTheme().applyStyle(switchValue? R.style.AppTheme_textChange:R.style.AppTheme , true);
                break;
            case 1:
                getTheme().applyStyle(switchValue ? R.style.GreenTheme_textGreen:R.style.GreenTheme,true);
                break;
            case 2:
                getTheme().applyStyle(switchValue ? R.style.BlueTheme_textBlue:R.style.BlueTheme,true);
                break;
            case 3:
                getTheme().applyStyle(switchValue ? R.style.OrangeTheme_textOrange: R.style.OrangeTheme,true);
                break;
            case 4:
                getTheme().applyStyle(switchValue? R.style.BlackTheme_textBlack:R.style.BlackTheme,true);
                break;
            default:
                getTheme().applyStyle(switchValue? R.style.AppTheme_textChange:R.style.AppTheme,true);
        }
        return colorId;
    }
    
    private Uri deepLinkSetUp(Uri content_uri) {
        if (content_uri.getHost().equals("www.nandeesh.com")){
            List<String> pathSegments =  content_uri.getPathSegments();
            Uri uri = ContentUris.withAppendedId(PetsContract.PetEntry.CONTENT_URI, Long.parseLong(pathSegments.get(1)));
            return uri;
        }
        return content_uri;
        
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("touchlistener", mPetHasChanged);
        super.onSaveInstanceState(outState);
    }
    
    // This callback is called only when there is a saved instance previously saved using
// onSaveInstanceState(). We restore some state in onCreate() while we can optionally restore
// other state here, possibly usable after onStart() has completed.
// The savedInstanceState Bundle is same as the one used in onCreate().
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mPetHasChanged = savedInstanceState.getBoolean("touchlistener", false);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        addPetPresenter.detachView();
        Log.d(TAG, "on destroy");
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "on start");
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "on stop");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "on pause");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "on resume");
        
    }
    
    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    @Override
    public void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);
        
        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        
        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);
        
        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                mGender = addPetPresenter.getGender(selection);
            }
            
            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_addpet.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_addpet, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                savePet();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
               /* if(isTaskRoot()){
                    Intent homeIntent = new Intent(AddPetActivity.this, CatalogActivity.class);
                    startActivity(homeIntent);
                    finishAndRemoveTask();
                    return true;
                }else{*/
                return upButtonPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    
    public boolean upButtonPressed() {
        final Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (!mPetHasChanged) {
            navigateToHome(AddPetActivity.this, upIntent);
            return true;
        }
        
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that
        // changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        dialogInterface.dismiss();
                        navigateToHome(AddPetActivity.this, upIntent);
                       // NavUtils.navigateUpTo(AddPetActivity.this , upIntent);
                    }
    
                    
                };
        
        // Show a dialog that notifies the user they have unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
        return true;
    }
    
    private void navigateToHome(AddPetActivity addPetActivity, Intent upIntent) {
        if (NavUtils.shouldUpRecreateTask(addPetActivity, upIntent) || isTaskRoot()) {
            // This activity is NOT part of this app's task, so create a new task
            // when navigating up, with a synthesized back stack.
            TaskStackBuilder.create(this)
                    // Add all of this activity's parents to the back stack
                    .addNextIntentWithParentStack(upIntent)
                    // Navigate up to the closest parent
                    .startActivities();
        } else {
            // This activity is part of this app's task, so simply
            // navigate up to the logical parent activity.
            NavUtils.navigateUpTo(addPetActivity, upIntent);
        }
    }
    
    
    @Override
    public void setActivityTitle(int id) {
        invalidateOptionsMenu();
        setTitle(id);
    }
    
    @Override
    public void showInputErrorMessage(int viewId, int resId, int lenghtId) {
        final Snackbar snackbar = Snackbar.make(findViewById(viewId), resId, lenghtId);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
    
    @Override
    public void populatePet(String name, String breed, int gender, int weight) {
        mNameEditText.setText(name);
        mBreedEditText.setText(breed);
        mWeightEditText.setText(weight + "");
        mGenderSpinner.setSelection(gender);
    }
    
    
    @Override
    public void savePet() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(this.INPUT_METHOD_SERVICE);
        
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        String name = mNameEditText.getText().toString(), breed = mBreedEditText.getText().toString();
        String weight_string = mWeightEditText.getText().toString();
        addPetPresenter.savePet(name, breed, weight_string, mGender);
    }
    
    
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (content_uri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        
        return true;
    }
    
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }
        
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        dialogInterface.dismiss();
                        finish();
                    }
                };
        
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    
    /**
     * Perform the deletion of the pet in the database.
     */
    @Override
    public void deletePet() {
        addPetPresenter.deletePet(content_uri);
    }
    
    
}