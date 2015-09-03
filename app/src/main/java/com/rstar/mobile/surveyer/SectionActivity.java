/*
 * Copyright (c) 2015 Annie Hui @ RStar Technology Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rstar.mobile.surveyer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class SectionActivity extends AppCompatActivity {
    private static final String TAG = SectionActivity.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    public static final String EXTRA_SectionNumber = SectionActivity.class.getSimpleName()+".SectionNumber";

    public static final String EXTRA_status = SectionActivity.class.getSimpleName()+".status";
    public static final int status_change = 1;

    private int mSectionNumber;
    private int mSectionSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Savelog.d(TAG, debug, "onCreate()");

        mSectionNumber = getIntent().getIntExtra(EXTRA_SectionNumber, Survey.Default_SectionNumber);
        Survey survey = new Survey(this);
        mSectionSize = survey.getSectionSize(mSectionNumber);

        FragmentManager fm = getSupportFragmentManager();
        setContentView(R.layout.activity_questions);
        ViewPager viewPager = (ViewPager) findViewById(R.id.activityQuestions_viewPager);
        PageAdapter adapter = new PageAdapter(fm, mSectionNumber, mSectionSize);
        viewPager.setAdapter(adapter);


    }



    private static class PageAdapter extends FragmentStatePagerAdapter {
        int sectionSize;
        int sectionNumber;
        public PageAdapter(FragmentManager fm, int sectionNumber, int sectionSize) {
            super(fm);
            this.sectionSize = sectionSize;
            this.sectionNumber = sectionNumber;
        }

        @Override
        public Fragment getItem(int position) {
            int questionNumber = position + 1;
            return QuestionFragment.newInstance(sectionNumber, questionNumber);
        }

        @Override
        public int getCount() {
            return sectionSize;
        }
    }



    public void setReturnIntent(int status) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_status, status);

        if (status==status_change) {
            this.setResult(Activity.RESULT_OK, returnIntent);  // OK: update needed
        }
    }


    @Override
    public void onBackPressed() {
        setReturnIntent(status_change);
        finish();
    }
}
