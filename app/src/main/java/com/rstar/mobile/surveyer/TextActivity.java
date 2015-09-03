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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;


public class TextActivity extends AppCompatActivity {

    private static final String TAG = TextActivity.class.getSimpleName() + "_class";
    private static final boolean debug = true;

    public static final String EXTRA_SectionNumber = TextActivity.class.getSimpleName() + ".SectionNumber";
    public static final String EXTRA_QuestionNumber = TextActivity.class.getSimpleName() + ".QuestionNumber";

    private int mSectionNumber;
    private int mQuestionNumber;

    private int fragmentId = R.id.activityContainer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        Savelog.d(TAG, debug, "onCreate()");

        mSectionNumber = getIntent().getIntExtra(EXTRA_SectionNumber, Survey.Default_SectionNumber);
        mQuestionNumber = getIntent().getIntExtra(EXTRA_QuestionNumber, Survey.Default_QuestionNumber);

        // Check if fragment already exists
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;
        fragment = fm.findFragmentById(fragmentId);
        if (fragment == null) {
            fragment = TextFragment.newInstance(mSectionNumber, mQuestionNumber);
            fm.beginTransaction().add(fragmentId, fragment).commit();
        }

    }

}
