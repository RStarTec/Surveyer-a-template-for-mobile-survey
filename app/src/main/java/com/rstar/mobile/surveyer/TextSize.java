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

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;


public class TextSize {
    private static final String TAG = TextSize.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private static final String PREF_textsize = TextSize.class.getSimpleName()+".textsize";

    // Note: the following default values are not a constant because we need to read them from resource.
    private int MinTextSize;
    private int MaxTextSize;
    private int DefaultTextSize;

    private int textSize;

    public TextSize(Context context) {
        // Must set up all these default values before loading data.
        DisplayMetrics metrics;
        metrics = context.getResources().getDisplayMetrics();
        double density = metrics.density;
        MinTextSize = (int)(context.getResources().getDimension(R.dimen.textsize_min)/density);
        MaxTextSize = (int)(context.getResources().getDimension(R.dimen.textsize_max)/density);
        DefaultTextSize = (MaxTextSize+MinTextSize)/2;
        load(context);
    }

    public void load(Context context) {
        textSize = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .getInt(PREF_textsize, DefaultTextSize);
        Savelog.d(TAG, debug, "Loading textsize=" + textSize);
    }
    public void save(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .edit().putInt(PREF_textsize, textSize).commit();
        Savelog.d(TAG, debug, "Saving textsize=" + textSize);
    }
    public void clear(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .edit().remove(PREF_textsize).commit();
    }


    public int getMax() {
        return MaxTextSize;
    }
    public int getMin() {
        return MinTextSize;
    }

    public void set(int textSize) {
        if (textSize>=MinTextSize && textSize<=MaxTextSize)
            this.textSize = textSize;
    }
    public int get() {
        return textSize;
    }
}
