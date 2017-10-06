package com.example.android.pets;

import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.pets.mvp.PetCatalog.CatalogActivity;

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
    
    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
    
    private int changeTheme() {
        int colorId = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(PreferenceActivity.this).getString(getString(R.string.color_preference_key),getString(R.string.color_default_value)));
        boolean switchValue = PreferenceManager.getDefaultSharedPreferences(PreferenceActivity.this)
                .getBoolean(getString(R.string.textColor_preference_key),getResources().getBoolean(R.bool.textColor_switch_default));
        switch (colorId) {
            case 0:
                getTheme().applyStyle(switchValue? R.style.AppTheme_textChange:R.style.AppTheme , true);
                break;
            case 1:
                getTheme().applyStyle(switchValue ? R.style.GreenTheme_textGreen:R.style.GreenTheme,true);
                break;
            case 2:
                getTheme().applyStyle(switchValue ? R.style.BlueTheme_textBlue:R.style.BlueTheme,true);
                break;
            case 3:
                getTheme().applyStyle(switchValue ? R.style.OrangeTheme_textOrange: R.style.OrangeTheme,true);
                break;
            case 4:
                getTheme().applyStyle(switchValue? R.style.BlackTheme_textBlack:R.style.BlackTheme,true);
                break;
            default:
                getTheme().applyStyle(switchValue? R.style.AppTheme_textChange:R.style.AppTheme,true);
        }
        return colorId;
    }
    
    public static class MyPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{
        
        
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
        
            p.setOnPreferenceChangeListener(this);
            
            if(p instanceof SwitchPreference){
                onPreferenceChange(p,PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(p.getKey(), getResources().getBoolean(R.bool.textColor_switch_default)));
            }else{
                onPreferenceChange(p,PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(p.getKey(),getString(R.string.color_default_value)));
            }
        }
        
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_preferences);
            for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++){
                pickPreferenceObject(getPreferenceScreen().getPreference(i));
            }
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        
        @Override
        public void onResume() {
            super.onResume();
          //  getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(prefListener);
        }
        
        @Override
        public void onPause() {
            super.onPause();
            //getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(prefListener);
        }
        
    
    
        @Override
        public boolean onPreferenceChange(Preference p, Object newValue) {
            String stringValue = newValue.toString();
            if(p instanceof ListPreference){
                ListPreference listPreference = (ListPreference)p;
                int index= listPreference.findIndexOfValue(stringValue);
                if(index>=0){
                    p.setSummary(listPreference.getEntries()[index]);
                }
            }else {
                p.setSummary(stringValue);
            }
            return true;
        }
    }
}
