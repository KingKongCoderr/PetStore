package com.example.android.pets.mvp.PetCatalog;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.Data.PetsContract;
import com.example.android.pets.R;

/**
 * Created by ntankasala on 8/25/17.
 */

public class PetAdapter extends CursorAdapter {

    public PetAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.catalog_item, viewGroup, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView name_tv, breed_tv;
        name_tv = view.findViewById(R.id.name_tv);
        breed_tv = view.findViewById(R.id.breed_tv);
        int name_index = cursor.getColumnIndexOrThrow(PetsContract.PetEntry.COLUMN_PET_NAME);
        int breed_index = cursor.getColumnIndexOrThrow(PetsContract.PetEntry.COLUMN_PET_BREED);
        PetCursorRecyclerViewAdapter.ViewHolder vh = new PetCursorRecyclerViewAdapter.ViewHolder(view.getRootView());
        name_tv.setText(cursor.getString(name_index));
        breed_tv.setText(cursor.getString(breed_index));
    }
}
