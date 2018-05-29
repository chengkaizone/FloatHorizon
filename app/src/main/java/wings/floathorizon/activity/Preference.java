package wings.floathorizon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import wings.floathorizon.MainActivity;
import wings.floathorizon.R;
import wings.floathorizon.part.Gradienter;

/**
 * 设置
 */
public class Preference extends AppCompatActivity
{
    /*成员*/
    PreferencePage preferencePage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);      //隐藏标题栏
        setContentView(R.layout.activity_preference);
        if(getSupportActionBar()!=null)     //隐藏标题栏
            getSupportActionBar().hide();

        preferencePage=new PreferencePage();
        getFragmentManager().beginTransaction().add(R.id.activityPreference_fragment, preferencePage).commit();

        ImageButton back=(ImageButton)findViewById(R.id.activityPreference_back);
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Preference.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume()
    {
        super.onResume();

        Intent intent=getIntent();
        int mode=intent.getIntExtra(MainActivity.Mode,Gradienter.Mode_Aspect);
        if(mode == Gradienter.Mode_Aspect)
            preferencePage.aspect.setEnabled(true);
        else
            preferencePage.aspect.setEnabled(false);
    }

    /**
     * 设置页
     */
    public static class PreferencePage extends PreferenceFragment implements android.preference.Preference.OnPreferenceChangeListener
    {
        /*成员*/
        private ListPreference aspect_range;
        public PreferenceCategory aspect;

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preference);

            aspect_range =((ListPreference)findPreference(getResources().getString(R.string.preference_range)));
            aspect_range.setOnPreferenceChangeListener(this);
            aspect_range.setSummary(aspect_range.getValue()+getResources().getString(R.string.preference_range_unit));

            aspect=(PreferenceCategory)findPreference(getResources().getString(R.string.preference_aspect));
        }
        @Override
        public boolean onPreferenceChange(android.preference.Preference preference, Object o)
        {
             if(preference== aspect_range)
                 aspect_range.setSummary(o.toString());

            return true;        //true：将新值保存
        }
    }
}
