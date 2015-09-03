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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;



public class MainListFragment extends Fragment implements AbsListView.OnItemClickListener {
    private static final String TAG = MainListFragment.class.getSimpleName()+"_class";
    private static final boolean debug = true;

    private static final int ListUpdateRequestCode = 101;  //must be unique!


    private static final int[] drawableIds = {
            R.drawable.color_button0,
            R.drawable.color_button1,
            R.drawable.color_button2,
            R.drawable.color_button3,
            R.drawable.color_button4,
            R.drawable.color_button5,
            R.drawable.color_button6,
            R.drawable.color_button7,
            R.drawable.color_button8,
            R.drawable.color_button9
    };


    private static final int Button_earnings = 0;
    private static final int Button_delete = 1;

    private static final int[] ButtonType = {
            Button_earnings,
            Button_delete
    };

    private static final int[] ButtonIds = {
            R.id.fragmentMainList_saved,
            R.id.fragmentMainList_deleteAll
    };



    private AbsListView mListView;
    private SectionListAdapter mAdapter;
    private Survey mSurvey;
    private Bitmap bitmap_done;
    private Bitmap bitmap_undone;
    private ImageButton mButtons[] = new ImageButton[ButtonIds.length];
    private OnButtonClickListener mButtonListeners[] = new OnButtonClickListener[ButtonIds.length];


    public static MainListFragment newInstance() {
        MainListFragment fragment = new MainListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Savelog.d(TAG, debug, "onCreate()");

        mSurvey = new Survey(getActivity());

        Savelog.d(TAG, debug, "number of sections = " + mSurvey.getNumberOfSections());
        mAdapter = new SectionListAdapter(this, mSurvey.getNumberOfSections());

        bitmap_done = BitmapFactory.decodeResource(getResources(), R.mipmap.thumbup);
        bitmap_undone = BitmapFactory.decodeResource(getResources(), R.mipmap.thumbup_outline);

        // Make sure to retain the fragment so that data retrieval is
        // not restarted at every rotation
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mainlist, container, false);

        // Set the adapter
        mListView = (AbsListView) v.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        for (int index=0; index<ButtonIds.length; index++) {
            mButtons[index] = (ImageButton) v.findViewById(ButtonIds[index]);
            if (mButtonListeners[index]==null)
                mButtonListeners[index] = new OnButtonClickListener(this, ButtonType[index]);
            mButtons[index].setOnClickListener(mButtonListeners[index]);
        }

        return v;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int sectionNumber = mAdapter.getItem(position);

        Intent intent = new Intent(getActivity(), SectionActivity.class);
        intent.putExtra(SectionActivity.EXTRA_SectionNumber, sectionNumber);
        startActivityForResult(intent, ListUpdateRequestCode);
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static class SectionListAdapter extends ArrayAdapter<Integer> {
        MainListFragment hostFragment;
        Context appContext;
        Integer[] labels;

        public SectionListAdapter(MainListFragment hostFragment, int size) {
            super(hostFragment.getActivity().getApplicationContext(), android.R.layout.simple_list_item_1);
            appContext = hostFragment.getActivity().getApplicationContext();
            this.hostFragment = hostFragment;
            labels = new Integer[size];
            // The labels start from 1.
            for (int index=0; index<size; index++)
                labels[index] = Integer.valueOf(index)+1;
        }
        @Override
        public int getCount() {
            return labels.length;
        }
        @Override
        public Integer getItem(int position) {
            return labels[position];
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Integer item = getItem(position);

            if (convertView==null) {
                LayoutInflater inflater = (LayoutInflater) appContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                convertView = inflater.inflate(R.layout.list_item_section, parent, false);
            }

            TextView itemView = (TextView) convertView.findViewById(R.id.listItem_section_number_id);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.listItem_section_thumbup);

            if (hostFragment.mSurvey.isSectionDone(appContext, item))
                imageView.setImageBitmap(hostFragment.bitmap_done);
            else
                imageView.setImageBitmap(hostFragment.bitmap_undone);

            // If we need more buttons than the available colors, recycle some.
            int maxDrawables = hostFragment.drawableIds.length;
            Drawable drawable;
            if (AppSettings.isNewVersion())
                drawable = appContext.getResources().getDrawable(hostFragment.drawableIds[position%maxDrawables], appContext.getTheme());
            else
                drawable = appContext.getResources().getDrawable(hostFragment.drawableIds[position%maxDrawables]);

            itemView.setBackground(drawable);
            itemView.setText(item.toString());
            convertView.setTag(item); // use value as a tag

            return convertView;
        }
    }


    @Override
    public void onDestroyView() {
        for (int index=0; index<mButtons.length; index++) {
            if (mButtons[index]!=null) {
                mButtons[index].setOnClickListener(null);
                mButtons[index] = null;
            }
        }

        if (mListView!=null) {
            mListView.setAdapter(null);
            mListView = null;
        }

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        for (int index=0; index<mButtonListeners.length; index++) {
            if (mButtonListeners[index]!=null) {
                mButtonListeners[index].cleanup();
                mButtonListeners[index] = null;
            }
        }
        if (mAdapter!=null) {
            mAdapter = null;
        }
        mSurvey = null;
        bitmap_done = null;
        bitmap_undone = null;

        super.onDestroy();
    }


    private void updateChanges() {
        if (mAdapter!=null) {
            mAdapter.notifyDataSetChanged();
            // TODO: also send updates to server if exists.
        }
    }

    private static class OnButtonClickListener implements View.OnClickListener {
        // This class of objects does not outlive its host, so no need to use weak references
        Context appContext;
        MainListFragment hostFragment;
        int buttonType;
        public OnButtonClickListener(MainListFragment hostFragment, int buttonType) {
            super();
            appContext = hostFragment.getActivity().getApplicationContext();
            this.hostFragment = hostFragment;
            this.buttonType = buttonType;
        }

        @Override
        public void onClick(View view) {
            if (buttonType==Button_earnings) {
                Intent intent = new Intent(hostFragment.getActivity(), EarningsActivity.class);
                hostFragment.startActivity(intent);
            }
            else if (buttonType==Button_delete) {
                hostFragment.mSurvey.clearResponses(appContext);
                hostFragment.updateChanges();
            }
        }
        public void cleanup() { hostFragment = null; }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Savelog.d(TAG, debug, "onActivityResult()");

        if (requestCode==ListUpdateRequestCode) {
            if (resultCode== Activity.RESULT_OK && data!=null) {
                updateChanges();
            }
        }
    }
}
