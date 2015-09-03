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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class EarningsFragment extends Fragment {
    private static final String TAG = EarningsFragment.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private int mTotal;
    private Bitmap mBitmap;
    private ImageView mImageView;

    private int imageIds[] = {
            R.mipmap.coins0,
            R.mipmap.coins1,
            R.mipmap.coins2,
            R.mipmap.coins3,
            R.mipmap.coins4,
            R.mipmap.coins5,
            R.mipmap.coins6,
            R.mipmap.coins7,
            R.mipmap.coins8,
            R.mipmap.coins9,
            R.mipmap.coins10
    };

    public static EarningsFragment newInstance() {
        EarningsFragment fragment = new EarningsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getActivity();
        Survey survey = new Survey(context);
        mTotal = survey.countCompletedSections(context);

        int id = 0;
        if (mTotal<imageIds.length) {
            id = imageIds[mTotal];
        }
        else {
            id = imageIds[0];
        }

        mBitmap = BitmapFactory.decodeResource(getResources(), id);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_earnings, container, false);

        mImageView = (ImageView) v.findViewById(R.id.fragmentEarnings_image);

        mImageView.setImageBitmap(mBitmap);

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}
