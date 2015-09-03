package com.rstar.mobile.surveyer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {
    int fragmentId = R.id.activityContainer_id;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        FragmentManager fm = getSupportFragmentManager();

        fragment = fm.findFragmentById(fragmentId);
        if (fragment == null) {
            fragment = MainListFragment.newInstance();
            fm.beginTransaction().add(fragmentId, fragment).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.menu_eula: {
                FragmentManager fm = getSupportFragmentManager();
                LegalDialogFragment dialogFragment;
                dialogFragment = LegalDialogFragment.newInstance(R.raw.eula);
                dialogFragment.show(fm, LegalDialogFragment.dialogTag);
                return true;
            }
            case R.id.menu_privacy: {
                FragmentManager fm = getSupportFragmentManager();
                LegalDialogFragment dialogFragment;
                dialogFragment = LegalDialogFragment.newInstance(R.raw.privacypolicy);
                dialogFragment.show(fm, LegalDialogFragment.dialogTag);
                return true;
            }
            case R.id.menu_acknowledgment: {
                FragmentManager fm = getSupportFragmentManager();
                LegalDialogFragment dialogFragment;
                dialogFragment = LegalDialogFragment.newInstance(R.raw.acknowledgment);
                dialogFragment.show(fm, LegalDialogFragment.dialogTag);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
