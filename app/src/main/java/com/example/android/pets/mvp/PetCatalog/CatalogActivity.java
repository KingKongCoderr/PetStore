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

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.android.pets.Data.PetsContract;
import com.example.android.pets.Injection.MainApplication;
import com.example.android.pets.R;
import com.example.android.pets.mvp.PetAdd.AddPetActivity;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements CatalogView {
    
    @Inject
    CatalogPresenter catalogPresenter;
    
    RecyclerView recyclerView;
    //private ListView listView;
    private View emptyView;
    private ImageView petHomeImageView, petImageView;
    private TextView label1, label2;
    private FloatingActionButton fab;
    RecyclerView.LayoutManager layoutManager;
    PetCursorRecyclerViewAdapter rv_adapter;
    private PetAdapter cursor_adapter;
    private LottieAnimationView fabLottie;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isLandscape = false;
    private float pixel = 0f;
    boolean isFirstLoad = true;
    private ValueAnimator fabAnimator, horizontalTextAnimation, verticalTextAnimation, petx, pety;
    private RelativeLayout mainLayout;
    private ViewTreeObserver.OnGlobalLayoutListener layoutChangeListener;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        mainLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_catalog, null);
        // set a global layout listener which will be called when the layout pass is completed and the view is drawn
        layoutChangeListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                //Remove the listener before proceeding
                removeLayoutListener(this);
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
                
                if (recyclerView.getAdapter().getItemCount()== 0) {
                    emptyViewAnimation();
                } else {
                    emptyView.setVisibility(View.GONE);
                }
            }
        };
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(layoutChangeListener);
        
        setContentView(mainLayout);
        MainApplication.getApplicationComponent().inject(this);
        catalogPresenter.attachView(this, this);
        catalogPresenter.setUpLoader(getSupportLoaderManager());
        sharedPreferences = getPreferences(MODE_PRIVATE);
        editor = sharedPreferences.edit();
        // Setup FAB to open AddPetActivity
        recyclerView = (RecyclerView) findViewById(R.id.catalog_rv);
        layoutManager = new LinearLayoutManager(getBaseContext());
        
        //listView = findViewById(R.id.catalog_lv);
        
        rv_adapter = new PetCursorRecyclerViewAdapter(this);
        recyclerView.setAdapter(rv_adapter);
        recyclerView.setLayoutManager(layoutManager);
        
        emptyView = findViewById(R.id.empty_view);
        fabLottie = (LottieAnimationView) findViewById(R.id.fab_lottie);
        fabLottie.setVisibility(View.INVISIBLE);
        petHomeImageView = (ImageView) findViewById(R.id.empty_shelter_image);
        petImageView = (ImageView) findViewById(R.id.pet_iv);
        label1 = (TextView) findViewById(R.id.empty_title_text);
        label2 = (TextView) findViewById(R.id.empty_subtitle_text);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerView.getAdapter().getItemCount() == 0) {
                    fabAnimation();
                } else {
                    fabLottie.setVisibility(View.VISIBLE);
                    int screen_height = sharedPreferences.getInt("height", 0);
                    int screen_width = sharedPreferences.getInt("width", 0);
                    fabLottie.setX(screen_width / 4);
                    fabLottie.setY(0f);
                    fabAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(2000);
                    fabAnimator.addUpdateListener(new LottieAnimation(fabLottie));
                    fabAnimator.addListener(setAnimationListener());
                    fabAnimator.start();
                }
            }
        });
      /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent addPetIntent = new Intent(CatalogActivity.this, AddPetActivity.class);
                Uri content_uri = ContentUris.withAppendedId(PetsContract.PetEntry.CONTENT_URI, id);
                addPetIntent.setData(content_uri);
                startActivity(addPetIntent);
            }
        });*/
        if (!(sharedPreferences.getBoolean("isFirstLoad", true))) {
            if (recyclerView.getAdapter().getItemCount() == 0) {
                emptyViewAnimation();
            } else {
                emptyView.setVisibility(View.GONE);
            }
        }
    }
    
    private static class LottieAnimation implements ValueAnimator.AnimatorUpdateListener {
        
        private final WeakReference<LottieAnimationView> mfabLottie;
        
        
        public LottieAnimation(LottieAnimationView fabLottie) {
            this.mfabLottie = new WeakReference<>(fabLottie);
        }
        
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            LottieAnimationView view = mfabLottie.get();
            if (view != null) {
                view.setProgress((float) valueAnimator.getAnimatedValue());
            }
        }
    }
    
    private static class HorizontalTextAnimation implements ValueAnimator.AnimatorUpdateListener {
        
        private final WeakReference<TextView> mlabel1, mlabel2;
        
        public HorizontalTextAnimation(TextView mlabel1, TextView mlabel2) {
            this.mlabel1 = new WeakReference<>(mlabel1);
            this.mlabel2 = new WeakReference<>(mlabel2);
        }
        
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            TextView mtv_1, mtv_2;
            mtv_1 = mlabel1.get();
            mtv_2 = mlabel2.get();
            float value = (float) valueAnimator.getAnimatedValue();
            // petHomeImageView.setTranslationY(value-500);
            if (mtv_2 != null && mtv_1 != null) {
                mtv_1.setTranslationX(value);
                mtv_2.setTranslationX(-value);
            }
        }
    }
    
    private static class VerticalTextAnimation implements ValueAnimator.AnimatorUpdateListener {
        
        private final WeakReference<TextView> mlabel1, mlabel2;
        
        public VerticalTextAnimation(TextView mlabel1, TextView mlabel2) {
            this.mlabel1 = new WeakReference<>(mlabel1);
            this.mlabel2 = new WeakReference<>(mlabel2);
        }
        
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            TextView mtv_1, mtv_2;
            mtv_1 = mlabel1.get();
            mtv_2 = mlabel2.get();
            float value = (float) valueAnimator.getAnimatedValue();
            // petHomeImageView.setTranslationY(value-500);
            if (mtv_2 != null && mtv_1 != null) {
                mtv_1.setTranslationY(-value);
                mtv_2.setTranslationY(value);
            }
        }
    }
    
    
    private void removeLayoutListener(ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        } else {
            mainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
        }
    }
    /*private static class AnimationCallbacks implements Animator.AnimatorListener{
        
        
    
        @Override
        public void onAnimationStart(Animator animator) {
        
        }
    
        @Override
        public void onAnimationEnd(Animator animator) {
            petImageView.setVisibility(View.GONE);
            fabLottie.setVisibility(View.GONE);
            clearAnimations();
            Intent intent = new Intent(CatalogActivity.this, AddPetActivity.class);
            startActivity(intent);
        }
    
        @Override
        public void onAnimationCancel(Animator animator) {
        
        }
    
        @Override
        public void onAnimationRepeat(Animator animator) {
        
        }
    }*/
    
    private Animator.AnimatorListener setAnimationListener() {
        Animator.AnimatorListener fabAnimationListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }
            
            @Override
            public void onAnimationEnd(Animator animator) {
                petImageView.setVisibility(View.GONE);
                fabLottie.setVisibility(View.GONE);
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
        petx = ValueAnimator.ofInt(fabx, imagex);
        petx.addUpdateListener(new ImageViewAnimation(petImageView, 1));
        petx.setDuration(3000);
        pety = ValueAnimator.ofInt(faby, imagey);
        pety.addUpdateListener(new ImageViewAnimation(petImageView, 2));
        pety.setDuration(3000);
        pety.addListener(setAnimationListener());
        petx.start();
        pety.start();
    }
    
    private static class ImageViewAnimation implements ValueAnimator.AnimatorUpdateListener {
        
        private final WeakReference<ImageView> mpetImageView;
        private int axis;
        
        public ImageViewAnimation(ImageView mpetImageView, int axis) {
            this.mpetImageView = new WeakReference<>(mpetImageView);
            this.axis = axis;
        }
        
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            ImageView view = mpetImageView.get();
            int value = (int) valueAnimator.getAnimatedValue();
            if (this.axis == 1) {
                view.setTranslationX(value);
            } else if (this.axis == 2) {
                view.setTranslationY(value);
            }
        }
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
            horizontalTextAnimation = ValueAnimator.ofFloat(0f, -(screen_width / 4));
            horizontalTextAnimation.addUpdateListener(new HorizontalTextAnimation(label1, label2));
            horizontalTextAnimation.setInterpolator(new BounceInterpolator());
            horizontalTextAnimation.setDuration(10000);
            horizontalTextAnimation.setRepeatCount(ValueAnimator.INFINITE);
            // valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            horizontalTextAnimation.start();
        } else {
          /*  dpi = sharedPreferences.getInt("dpi", 240);*/
            float pixel = sharedPreferences.getFloat("dpi_pixel", 0f);
            verticalTextAnimation = ValueAnimator.ofFloat(0f, pixel);
            verticalTextAnimation.addUpdateListener(new VerticalTextAnimation(label1, label2));
            verticalTextAnimation.setInterpolator(new BounceInterpolator());
            verticalTextAnimation.setDuration(4000);
            verticalTextAnimation.setRepeatCount(ValueAnimator.INFINITE);
            // valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            verticalTextAnimation.start();
        }
    }
    
    @Override
    protected void onDestroy() {
        removeLayoutListener(layoutChangeListener);
        catalogPresenter.detachView();
        super.onDestroy();
    }
    
    @Override
    protected void onPause() {
        clearAnimations();
        super.onPause();
    }
    
    public void clearAnimations() {
        if (fabAnimator != null) {
            fabAnimator.cancel();
            fabAnimator.removeAllListeners();
            fabAnimator.removeAllUpdateListeners();
        } else if (horizontalTextAnimation != null) {
            horizontalTextAnimation.cancel();
            horizontalTextAnimation.removeAllListeners();
            horizontalTextAnimation.removeAllUpdateListeners();
        } else if (verticalTextAnimation != null) {
            verticalTextAnimation.cancel();
            verticalTextAnimation.removeAllListeners();
            verticalTextAnimation.removeAllUpdateListeners();
        } else if (petx != null) {
            petx.cancel();
            petx.removeAllUpdateListeners();
            petx.removeAllListeners();
        } else if (pety != null) {
            pety.cancel();
            pety.removeAllUpdateListeners();
            pety.removeAllListeners();
        } else if (fabLottie != null) {
            fabLottie.clearAnimation();
            fabLottie = null;
        } else if (petImageView != null) {
            petImageView.clearAnimation();
            petImageView = null;
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
        //cursor_adapter = new PetAdapter(this, cursor, 0);
        /*listView.setAdapter(cursor_adapter);
        listView.setEmptyView(emptyView);*/
        rv_adapter.setCursor(cursor);
        if (recyclerView.getAdapter().getItemCount()!=0){
            emptyView.setVisibility(View.GONE);
        }else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void refreshCatalog(Cursor cursor) {
        rv_adapter.setCursor(cursor);
        //cursor_adapter.changeCursor(cursor);
        if (recyclerView.getAdapter().getItemCount()==0){
            emptyView.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void deleteAllPets() {
        catalogPresenter.deleteAllpets(PetsContract.PetEntry.CONTENT_URI);
    }
    
}
