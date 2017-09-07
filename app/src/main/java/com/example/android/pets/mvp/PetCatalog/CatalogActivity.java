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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
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
    ImageView petHomeImageView, petImageView;
    TextView label1, label2;
    FloatingActionButton fab;
    //RecyclerView.LayoutManager layoutManager;
    //PetCursorRecyclerViewAdapter rv_adapter;
    PetAdapter cursor_adapter;
    LottieAnimationView fabLottie;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private boolean isLandscape = false;
    private float pixel = 0f;
    
    boolean isFirstLoad = true;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final RelativeLayout mainLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_catalog, null);
        // set a global layout listener which will be called when the layout pass is completed and the view is drawn
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        //Remove the listener before proceeding
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            mainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        // measure your views here
                        int labelOnScreenLocation[] = new int[2];
                        int labelInWindowLocation[] = new int[2];
                        int imageOnScreenLocation[] = new int[2];
                        int fabOnScreenLocation[] = new int[2];
                        petHomeImageView.getLocationOnScreen(imageOnScreenLocation);
                        fab.getLocationOnScreen(fabOnScreenLocation);
                        label1.getLocationInWindow(labelInWindowLocation);
                        label1.getLocationOnScreen(labelOnScreenLocation);
                        editor.putInt("imagex", imageOnScreenLocation[0]);
                        editor.putInt("imagey", imageOnScreenLocation[1]);
                        editor.putInt("fabx", fabOnScreenLocation[0]);
                        editor.putInt("faby", fabOnScreenLocation[1]);
                        editor.putInt("labelx", labelOnScreenLocation[0]);
                        editor.putInt("labely", labelOnScreenLocation[1]);
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        editor.putInt("height", displayMetrics.heightPixels);
                        editor.putInt("width", displayMetrics.widthPixels);
                        pixel = 16 * (displayMetrics.densityDpi / 160);
                        editor.putInt("dpi", displayMetrics.densityDpi);
                        editor.putFloat("dpi_pixel", pixel);
                        editor.putBoolean("isFirstLoad", false);
                        editor.commit();
                        if (listView.getCount() == 0) {
                            emptyViewAnimation();
                        }
                    }
                }
        );
        
        setContentView(mainLayout);
        MainApplication.getApplicationComponent().
                
                inject(this);
        catalogPresenter.attachView(this, this);
        catalogPresenter.setUpLoader(
                
                getSupportLoaderManager());
        
        sharedPreferences =
                
                getPreferences(MODE_PRIVATE);
        
        editor = sharedPreferences.edit();
        // Setup FAB to open AddPetActivity
        //recyclerView = findViewById(R.id.catalog_rv);
        listView =
                
                findViewById(R.id.catalog_lv);
        
        emptyView =
                
                findViewById(R.id.empty_view);
        
        fabLottie =
                
                findViewById(R.id.fab_lottie);
        fabLottie.setVisibility(View.INVISIBLE);
        petHomeImageView =
                
                findViewById(R.id.empty_shelter_image);
        
        petImageView =
                
                findViewById(R.id.pet_iv);
        
        label1 =
                
                findViewById(R.id.empty_title_text);
        
        label2 =
                
                findViewById(R.id.empty_subtitle_text);
        // layoutManager = new LinearLayoutManager(getBaseContext());
        fab = (FloatingActionButton)
                
                findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        
        {
            @Override
            public void onClick(View view) {
                if (listView.getCount() == 0) {
                    fabAnimation();
                } else {
                    fabLottie.setVisibility(View.VISIBLE);
                    int screen_height = sharedPreferences.getInt("height", 0);
                    int screen_width = sharedPreferences.getInt("width", 0);
                    fabLottie.setX(screen_width / 4);
                    fabLottie.setY(0f);
                    /*fabLottie.setX(width/5);
                    fabLottie.setY(height/10);*/
                    ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(2000);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            fabLottie.setProgress((float) valueAnimator.getAnimatedValue());
                        }
                    });
                    animator.start();
                    animator.addListener(setAnimationListener());
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent addPetIntent = new Intent(CatalogActivity.this, AddPetActivity.class);
                Uri content_uri = ContentUris.withAppendedId(PetsContract.PetEntry.CONTENT_URI, id);
                addPetIntent.setData(content_uri);
                startActivity(addPetIntent);
            }
        });
        if (!(sharedPreferences.getBoolean("isFirstLoad", true))) {
            if (listView.getCount() == 0) {
                emptyViewAnimation();
            }
        }
    }
    
    private Animator.AnimatorListener setAnimationListener() {
        Animator.AnimatorListener fabAnimationListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }
            
            @Override
            public void onAnimationEnd(Animator animator) {
                petImageView.setVisibility(View.INVISIBLE);
                fabLottie.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(CatalogActivity.this, AddPetActivity.class);
                startActivity(intent);
            }
            
            @Override
            public void onAnimationCancel(Animator animator) {
            }
            
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        };
        return fabAnimationListener;
    }
    
    private void fabAnimation() {
        petImageView.setVisibility(View.VISIBLE);
        int fabx = sharedPreferences.getInt("fabx", 0) + 50, faby = sharedPreferences.getInt("faby", 0) - 100;
        int imagex = sharedPreferences.getInt("imagex", 0) + 50, imagey = sharedPreferences.getInt("imagey", 0) - 20;
        ValueAnimator petx = ValueAnimator.ofInt(fabx, imagex);
        petx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                petImageView.setTranslationX(value);
            }
        });
        petx.setDuration(3000);
        ValueAnimator pety = ValueAnimator.ofInt(faby, imagey);
        pety.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                petImageView.setTranslationY(value);
            }
        });
        pety.setDuration(3000);
        petx.start();
        pety.start();
        pety.addListener(setAnimationListener());
        
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        //catalogPresenter.getCatalog();
    }
    
    public void emptyViewAnimation() {
        
        isLandscape = catalogPresenter.isLandscape();
        /*
        using object animator which is subclass of value animator
         */
       /* ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(emptyView,"translationY",-400f);
        objectAnimator.setDuration(5000);
        objectAnimator.start();
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);*/
        
        if (isLandscape == false) {
            //screen width retrieved at runtime through display metrics
            int screen_width = sharedPreferences.getInt("width", 100);
            
            //Value Animator has more features than Object Animator
            ValueAnimator horizontalTextAnimation = ValueAnimator.ofFloat(0f, -(screen_width / 4));
            horizontalTextAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value = (float) valueAnimator.getAnimatedValue();
                    // petHomeImageView.setTranslationY(value-500);
                    label1.setTranslationX(value);
                    label2.setTranslationX(-value);
                }
            });
            horizontalTextAnimation.setInterpolator(new BounceInterpolator());
            horizontalTextAnimation.setDuration(10000);
            horizontalTextAnimation.setRepeatCount(ValueAnimator.INFINITE);
            // valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            horizontalTextAnimation.start();
        } else {
          /*  dpi = sharedPreferences.getInt("dpi", 240);*/
            float pixel = sharedPreferences.getFloat("dpi_pixel", 0f);
            ValueAnimator verticalTextAnimation = ValueAnimator.ofFloat(0f, pixel);
            verticalTextAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value = (float) valueAnimator.getAnimatedValue();
                    // petHomeImageView.setTranslationY(value-500);
                    label1.setTranslationY(-value);
                    label2.setTranslationY(value);
                }
            });
            verticalTextAnimation.setInterpolator(new BounceInterpolator());
            verticalTextAnimation.setDuration(4000);
            verticalTextAnimation.setRepeatCount(ValueAnimator.INFINITE);
            // valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            verticalTextAnimation.start();
        }
        
        
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
