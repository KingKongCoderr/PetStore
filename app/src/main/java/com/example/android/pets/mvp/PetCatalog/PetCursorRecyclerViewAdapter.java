package com.example.android.pets.mvp.PetCatalog;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.Data.PetsContract;
import com.example.android.pets.R;
import com.example.android.pets.mvp.PetAdd.AddPetActivity;


/**
 * Created by ntankasala on 8/25/17.
 */

public class PetCursorRecyclerViewAdapter extends RecyclerView.Adapter<PetCursorRecyclerViewAdapter.ViewHolder>
{
    Cursor cursor = null;
    Context context;
    
    public PetCursorRecyclerViewAdapter(Context context) {
        this.context = context;
        
    }
    
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.catalog_item, parent, false);
        return new ViewHolder(v);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        this.cursor.moveToPosition(position);
        holder.bindModel(this.cursor);
    }
    
    /*   getItemCount() returns the count of videos from the Cursor, or 0 if the
       Cursor is null (mimicking the behavior of CursorAdapter, which also treats
       a null Cursor as merely being one that has no rows)*/
    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        } else {
            return cursor.getCount();
        }
    }
    
  
    
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name_tv, breed_tv;
        
        public ViewHolder(View itemView) {
            super(itemView);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            breed_tv = (TextView) itemView.findViewById(R.id.breed_tv);
            itemView.setOnClickListener(this);
        }
        
        public void bindModel(Cursor cursor) {
            //int position = cursor.get
            int name_index = cursor.getColumnIndexOrThrow(PetsContract.PetEntry.COLUMN_PET_NAME);
            int breed_index = cursor.getColumnIndexOrThrow(PetsContract.PetEntry.COLUMN_PET_BREED);
            name_tv.setText(cursor.getString(name_index));
            String breed = cursor.getString(breed_index);
            if (TextUtils.isEmpty(breed)) {
                breed = "Unknown Breed";
            }
            breed_tv.setText(breed);
        }
        @Override
        public void onClick(View v) {
            Intent addPetIntent = new Intent(name_tv.getContext() , AddPetActivity.class);
            int position = getLayoutPosition(), position2 = getAdapterPosition();
            Uri content_uri = ContentUris.withAppendedId(PetsContract.PetEntry.CONTENT_URI, getLayoutPosition()+1);
            addPetIntent.setData(content_uri);
            name_tv.getContext().startActivity(addPetIntent);
        }
    }


    /*public class CursorAdapter extends android.widget.CursorAdapter{
        
        public CursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }
    
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View v = LayoutInflater.from(context).inflate(R.layout.catalog_item, viewGroup, false);
            return v;
        }
    
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            int cursor_count = cursor.getCount();
            int position = cursor.getPosition();
            TextView name_tv, breed_tv;
            name_tv = (TextView) view.findViewById(R.id.name_tv);
            breed_tv = (TextView) view.findViewById(R.id.breed_tv);
            int name_index = cursor.getColumnIndexOrThrow(PetsContract.PetEntry.COLUMN_PET_NAME);
            int breed_index = cursor.getColumnIndexOrThrow(PetsContract.PetEntry.COLUMN_PET_BREED);
           //ViewHolder vh = new ViewHolder(view);
            String name = cursor.getString(name_index);
            name_tv.setText(cursor.getString(name_index));
            breed_tv.setText(cursor.getString(breed_index));
            //onBindViewHolder(vh,cursor.getPosition());
        }
    
        @Override
        public Cursor getCursor() {
            return super.getCursor();
        }
    }*/
    
}

