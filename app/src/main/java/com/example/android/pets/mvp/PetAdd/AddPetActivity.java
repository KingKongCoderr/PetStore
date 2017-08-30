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


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.pets.Injection.MainApplication;
import com.example.android.pets.R;

import javax.inject.Inject;


/**
 * Allows user to create a new pet or edit an existing one.
 */
public class AddPetActivity extends AppCompatActivity implements AddPetView{

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

    Uri content_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);
        MainApplication.getApplicationComponent().inject(this);
        addPetPresenter.attachView(this);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.add_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.add_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.add_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.addpet_spinner_gender);
        setupSpinner();
        content_uri = getIntent().getData();
        addPetPresenter.setUpLoader(getSupportLoaderManager(), content_uri);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        addPetPresenter.detachView();
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
                insertPet();
                // Do nothing for now
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                deletePet();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void deletePet() {

    }

    @Override
    public void populatePet(String name, String breed, int gender, int weight) {
        mNameEditText.setText(name);
        mBreedEditText.setText(breed);
        mWeightEditText.setText(weight+"");
        mGenderSpinner.setSelection(gender);
    }



    @Override
    public void insertPet() {
        String name = mNameEditText.getText().toString(), breed = mBreedEditText.getText().toString();
        String weight_string = mWeightEditText.getText().toString();
        addPetPresenter.insertPet(name, breed, weight_string, mGender);
    }




}