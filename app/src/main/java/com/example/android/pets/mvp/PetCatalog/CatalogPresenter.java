package com.example.android.pets.mvp.PetCatalog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.Toast;

import com.example.android.pets.Data.PetsContract;
import com.example.android.pets.mvp.BaseMvp.BasePresenter;

/**
 * Created by ntankasala on 8/24/17.
 */

public class CatalogPresenter extends BasePresenter<CatalogView> implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CATALOG_LOADER_CONTENTPROVIDER = 1;

    private Context context;
    private Cursor cursor;
    private CursorLoader cursorLoader;
    private LoaderManager catalogLoaderMgr;


    public CatalogPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void attachView(CatalogView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }


    public void setUpLoader(LoaderManager loaderManager) {
        catalogLoaderMgr = loaderManager;
        catalogLoaderMgr.initLoader(CATALOG_LOADER_CONTENTPROVIDER, null, this);
    }

    public void insertDummyData() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_NAME, "TOTO");
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_BREED, "Terrier");
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_GENDER, 1);
        contentValues.put(PetsContract.PetEntry.COLUMN_PET_WEIGHT, 7);
        try {
            context.getContentResolver().insert(PetsContract.PetEntry.CONTENT_URI, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            contentValues.clear();
            /* use the below line or set Notification URI on the cursor object in the content provider and call
            Content resolver.notifychange in CRUD callback methods*/

           // catalogLoaderMgr.restartLoader( CATALOG_LOADER_CONTENTPROVIDER,null,this);
            //getMvpView().refreshCatalog(getDataBaseInfo());
        }
    }

   /* public void getCatalog() {
        getMvpView().showCatalog(getDataBaseInfo());
    }*/

  /*  public Cursor getDataBaseInfo() {
        String projection[] = new String[]{
                PetsContract.PetEntry._ID, PetsContract.PetEntry.COLUMN_PET_NAME, PetsContract.PetEntry.COLUMN_PET_BREED
                , PetsContract.PetEntry.COLUMN_PET_GENDER, PetsContract.PetEntry.COLUMN_PET_WEIGHT
        };
        Uri uri = PetsContract.PetEntry.CONTENT_URI;
        cursor = context.getContentResolver().query(uri, projection, null, null, null);
        return cursor;
    }*/

    private CursorLoader loader_contentProvider() {
        String projection[] = new String[]{
                PetsContract.PetEntry._ID, PetsContract.PetEntry.COLUMN_PET_NAME, PetsContract.PetEntry.COLUMN_PET_BREED
                , PetsContract.PetEntry.COLUMN_PET_GENDER, PetsContract.PetEntry.COLUMN_PET_WEIGHT
        };
        Uri uri = PetsContract.PetEntry.CONTENT_URI;
        cursorLoader = new CursorLoader(context, uri, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            case CATALOG_LOADER_CONTENTPROVIDER:
                return loader_contentProvider();
        }
        return null;
    }

    /* Swap the new cursor in.  (The framework will take care of closing the
     old cursor once we return.)*/
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getMvpView().showCatalog(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        // This method called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.

        // old cursor data is removed so that new cursor data can be inserted
        getMvpView().refreshCatalog(null);
    }

    public void deleteAllpets(Uri tableUri){
        if(tableUri!=null){
        int id = context.getContentResolver().delete(tableUri,null,null);
            Toast.makeText(context, id+" rows deleted", Toast.LENGTH_SHORT).show();
        }

    }

}
