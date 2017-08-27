package com.example.android.pets.mvp.PetCatalog;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.Data.PetsContract;
import com.example.android.pets.R;

import static com.example.android.pets.R.id.breed_tv;
import static com.example.android.pets.R.id.name_tv;


/**
 * Created by ntankasala on 8/25/17.
 */

public class PetCursorRecyclerViewAdapter extends RecyclerView.Adapter<PetCursorRecyclerViewAdapter.ViewHolder> {
    CursorAdapter petCursorAdapter;
    Context context;

    public PetCursorRecyclerViewAdapter(Context context, final Cursor cursor) {
        this.context = context;
        petCursorAdapter = new CursorAdapter(context, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                View v = LayoutInflater.from(context).inflate(R.layout.catalog_item, viewGroup, false);
                return v;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                int name_index = cursor.getColumnIndexOrThrow(PetsContract.PetEntry.COLUMN_PET_NAME);
                int breed_index = cursor.getColumnIndexOrThrow(PetsContract.PetEntry.COLUMN_PET_BREED);
                ViewHolder vh = new ViewHolder(view.getRootView());
                vh.name_tv.setText(cursor.getString(name_index));
                vh.breed_tv.setText(cursor.getString(breed_index));
               // onBindViewHolder(vh,cursor.getPosition());
            }

            @Override
            public int getCount() {
                return cursor.getCount();
            }
        };
    }

    public void refreshCursor(Cursor cursor){
        petCursorAdapter.changeCursor(cursor);
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = petCursorAdapter.newView(context, petCursorAdapter.getCursor(),parent);
            return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Passing the binding operation to cursor loader
        petCursorAdapter.getCursor().moveToPosition(position); //EDITED: added this line as suggested in the comments below, thanks :)
        petCursorAdapter.bindView(holder.itemView, context, petCursorAdapter.getCursor());
    }

    @Override
    public int getItemCount() {
        return petCursorAdapter.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name_tv, breed_tv;
        public ViewHolder(View itemView) {
            super(itemView);
            name_tv = itemView.findViewById(R.id.name_tv);
            breed_tv = itemView.findViewById(R.id.breed_tv);
        }
    }
}
