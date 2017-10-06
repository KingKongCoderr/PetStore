package com.example.android.pets;

import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.prefs.PreferenceChangeListener;

public class PreferenceActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        changeTheme();
        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            getFragmentManager().beginTransaction().add(android.R.id.content, new MyPreferenceFragment()).commit();
        }
    }
    private int changeTheme() {
        int colorId = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(PreferenceActivity.this).getString(getString(R.string.color_preference_key),getString(R.string.color_default_value)));
        switch (colorId) {
            case 0:
                getTheme().applyStyle(R.style.AppTheme,true);
                break;
            case 1:
                getTheme().applyStyle(R.style.GreenTheme,true);
                break;
            case 2:
                getTheme().applyStyle(R.style.BlueTheme,true);
                break;
            case 3:
                getTheme().applyStyle(R.style.OrangeTheme,true);
                break;
            case 4:
                getTheme().applyStyle(R.style.BlackTheme,true);
                break;
            default:
                getTheme().applyStyle(R.style.AppTheme,true);
        }
        return colorId;
    }
    
    public static class MyPreferenceFragment extends PreferenceFragment {
        
        private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    
        private void pickPreferenceObject(Preference p) {
            if (p instanceof PreferenceCategory) {
                PreferenceCategory cat = (PreferenceCategory) p;
                for (int i = 0; i < cat.getPreferenceCount(); i++) {
                    pickPreferenceObject(cat.getPreference(i));
                }
            } else {
                initSummary(p);
            }
        }
    
        private void initSummary(Preference p) {
        
            if (p instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) p;
                p.setSummary(editTextPref.getText());
            }
        
            if(p instanceof ListPreference){
                Object value= ((ListPreference) p).getValue();
                String stringValue= value.toString();
                int index= ((ListPreference) p).findIndexOfValue(stringValue);
                if(index>=0){
                    p.setSummary(((ListPreference) p).getEntries()[index]);
                }
            }
            // More logic for ListPreference, etc...
        }
        
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_preferences);
            for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++){
                pickPreferenceObject(getPreferenceScreen().getPreference(i));
            }
            prefListener= new SharedPreferences.OnSharedPreferenceChangeListener(){
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                   
                    if (key.equals(R.string.color_preference_key)){
                        String stringValue= sharedPreferences.getString(key, String.valueOf(R.string.color_default_value));
                        Preference p=findPreference(key);
                        int index= ((ListPreference) p).findIndexOfValue(stringValue);
                        if(index>=0){
                            p.setSummary(((ListPreference) p).getEntries()[index]);
                        }
                    }
            
                    /*if(key.equals("location")){
                        String val=sharedPreferences.getString(key, String.valueOf(R.string.pref_location_default));
                        Preference location_pref= findPreference(key);
                        location_pref.setSummary(sharedPreferences.getString(key, String.valueOf(R.string.pref_location_default)));
                        Log.d("inside","inside the listener with summary value:"+val);
                    }
                    if(key.equals("units")){
                        String stringValue= sharedPreferences.getString(key, String.valueOf(R.string.pref_units_metric));
                        Preference p=findPreference(key);
                        int index= ((ListPreference) p).findIndexOfValue(stringValue);
                        if(index>=0){
                            p.setSummary(((ListPreference) p).getEntries()[index]);
                        }
                    }*/
            
            
            
                }
            };
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        
        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(prefListener);
        }
        
        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(prefListener);
        }
    }
}
