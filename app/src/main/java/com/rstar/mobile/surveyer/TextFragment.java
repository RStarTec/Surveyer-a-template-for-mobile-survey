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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


public class TextFragment extends Fragment {
    private static final String TAG = TextFragment.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    // the fragment initialization parameters
    private static final String EXTRA_SectionNumber = TextFragment.class.getSimpleName()+".SectionNumber";
    private static final String EXTRA_QuestionNumber = TextFragment.class.getSimpleName()+".QuestionNumber";


    private int mSectionNumber;
    private int mQuestionNumber;

    private String mText;
    private TextView mTextView;
    private SeekBar mTextSizeSeekbar;
    private SeekBarOnChangedListener mSeekbarChangedListener;
    private TextSize mTextSize;

    public static TextFragment newInstance(int moduleNumber, int pageNumber) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_SectionNumber, moduleNumber);
        args.putInt(EXTRA_QuestionNumber, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(EXTRA_SectionNumber, Survey.Default_SectionNumber);
            mQuestionNumber = getArguments().getInt(EXTRA_QuestionNumber, Survey.Default_QuestionNumber);

            Context context = getActivity();
            Survey survey = new Survey(context);

            mText = survey.getQuestion(mSectionNumber, mQuestionNumber);
            mTextSize = new TextSize(getActivity());
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_text, container, false);

        mTextView = (TextView) v.findViewById(R.id.fragmentText_content);
        mTextView.setText(mText);
        mTextView.setVisibility(View.VISIBLE);

        mTextSizeSeekbar = (SeekBar) v.findViewById(R.id.fragmentText_seekbar_id);
        mSeekbarChangedListener = new SeekBarOnChangedListener(this, mTextSize.getMin(), mTextSize.getMax());
        mTextSizeSeekbar.setOnSeekBarChangeListener(mSeekbarChangedListener);
        mTextSizeSeekbar.setProgress(textSizeToProgress(mTextSize.get(), mTextSize.getMin(), mTextSize.getMax()));

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();

        mTextSize.save(getActivity());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mTextSizeSeekbar!=null) {
            mTextSizeSeekbar.setOnSeekBarChangeListener(null);
            mTextSizeSeekbar = null;
        }
        if (mSeekbarChangedListener!=null) {
            mSeekbarChangedListener.cleanup();
            mSeekbarChangedListener = null;
        }
    }


    private static class SeekBarOnChangedListener implements SeekBar.OnSeekBarChangeListener {
        Context appContext;
        TextFragment hostFragment;
        int min;
        int max;
        public SeekBarOnChangedListener(TextFragment hostFragment, int min, int max) {
            this.hostFragment = hostFragment;
            this.min = min;
            this.max = max;
            this.appContext = hostFragment.getActivity().getApplicationContext();
        }
        public void cleanup() {
            hostFragment = null;
        }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            hostFragment.mTextSize.set(progressToTextSize(progress, min, max));
            hostFragment.mTextView.setTextSize(hostFragment.mTextSize.get());
            Savelog.d(TAG, debug, "Textsize=" + hostFragment.mTextSize.get());
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }



    private int textSizeToProgress(int textSize, int min, int max) {
        double percentage = (double)(textSize-min)/(max-min)*100;
        return (int)Math.round(percentage);
    }

    private static int progressToTextSize(int progress, int min, int max) {
        double value = (double)(progress)*0.01*(max-min) + min;
        return (int) Math.round(value);
    }


}
