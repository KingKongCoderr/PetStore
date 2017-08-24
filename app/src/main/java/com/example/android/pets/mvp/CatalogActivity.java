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
package com.example.android.pets.mvp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.Data.PetDbHelper;
import com.example.android.pets.Data.PetsContract;
import com.example.android.pets.R;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
      //  displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        //PetDbHelper mDbHelper = new PetDbHelper(this);

        // Create and/or open a database to read from it
        //SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
       // Cursor cursor = db.rawQuery("SELECT * FROM " + PetsContract.PetEntry.TABLE_NAME, null);



       // Cursor cursor = db.query(PetsContract.PetEntry.TABLE_NAME,null,null,null,null,null,null);

        String projection[] = new String[]{
                PetsContract.PetEntry._ID, PetsContract.PetEntry.COLUMN_PET_NAME, PetsContract.PetEntry.COLUMN_PET_BREED
                , PetsContract.PetEntry.COLUMN_PET_GENDER, PetsContract.PetEntry.COLUMN_PET_WEIGHT
        };
        Uri uri = PetsContract.PetEntry.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri,projection,null,null,null);
        int id_index = cursor.getColumnIndex(PetsContract.PetEntry._ID);
        int name_index = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_NAME);
        int breed_index = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_BREED);
        int gender_index = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_GENDER);
        int weight_index = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_WEIGHT);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("Number of rows in pets database table: " + cursor.getCount());
            displayView.append("\n"+ "_id:  "+"name: "+ "breed: "+"gender: "+"weight: ");
            while (cursor.moveToNext()){
                int id =  cursor.getInt(id_index);
                String name = cursor.getString(name_index);
                String breed = cursor.getString(breed_index);
                int gender = cursor.getInt(gender_index);
                int weight = cursor.getInt(weight_index);
                displayView.append("\n"+ id +" "+ name +" " +breed+" "+gender+" "+weight+" ");
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummy_data();
                // Do nothing for now
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void insertDummy_data(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_NAME, "TOTO");
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_BREED,"Terrier");
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_GENDER, 1);
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_WEIGHT,7);
        try {
            getApplicationContext().getContentResolver().insert(PetsContract.PetEntry.CONTENT_URI, contentValues);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            contentValues.clear();
            displayDatabaseInfo();
        }


    }
}
