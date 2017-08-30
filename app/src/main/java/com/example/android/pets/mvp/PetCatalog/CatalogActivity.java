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
package com.example.android.pets.mvp.PetCatalog;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.pets.Data.PetsContract;
import com.example.android.pets.Injection.MainApplication;
import com.example.android.pets.R;
import com.example.android.pets.mvp.PetAdd.AddPetActivity;

import javax.inject.Inject;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements CatalogView {

    @Inject
    CatalogPresenter catalogPresenter;

    //RecyclerView recyclerView;
    ListView listView;
    View emptyView;
    //RecyclerView.LayoutManager layoutManager;
    //PetCursorRecyclerViewAdapter rv_adapter;
    PetAdapter cursor_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        MainApplication.getApplicationComponent().inject(this);
        catalogPresenter.attachView(this);
        catalogPresenter.setUpLoader(getSupportLoaderManager());
        // Setup FAB to open AddPetActivity
        //recyclerView = findViewById(R.id.catalog_rv);
        listView = findViewById(R.id.catalog_lv);
        emptyView = findViewById(R.id.empty_view);
        // layoutManager = new LinearLayoutManager(getBaseContext());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, AddPetActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent addPetIntent = new Intent(CatalogActivity.this, AddPetActivity.class);
                Uri content_uri = ContentUris.withAppendedId(PetsContract.PetEntry.CONTENT_URI,id);
                addPetIntent.setData(content_uri);
                startActivity(addPetIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //catalogPresenter.getCatalog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        catalogPresenter.detachView();
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
                catalogPresenter.insertDummyData();
                // Do nothing for now
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showCatalog(Cursor cursor) {
        cursor_adapter = new PetAdapter(this, cursor, 0);
        listView.setAdapter(cursor_adapter);
        listView.setEmptyView(emptyView);

        /* if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
        }
        rv_adapter = new PetCursorRecyclerViewAdapter(this,cursor);
        recyclerView.setAdapter(rv_adapter);
        recyclerView.setLayoutManager(layoutManager);*/
    }

    @Override
    public void refreshCatalog(Cursor cursor) {
        // rv_adapter.refreshCursor(cursor);
        cursor_adapter.changeCursor(cursor);
    }

    @Override
    public void deleteAllPets() {
        catalogPresenter.deleteAllpets(PetsContract.PetEntry.CONTENT_URI);
    }

}
