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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;

public class QuestionFragment extends Fragment {
    private static final String TAG = QuestionFragment.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private static final String EXTRA_SectionNumber = QuestionFragment.class.getSimpleName() + ".SectionNumber";
    private static final String EXTRA_QuestionNumber = QuestionFragment.class.getSimpleName() + ".QuestionNumber";

    private static final int Button_text = 0;
    private static final int Button_audio = 1;
    private static final int Button_yes = 2;
    private static final int Button_no = 3;
    private static final int Button_delete = 4;

    private static final int[] ButtonType = {
            Button_text,
            Button_audio,
            Button_yes,
            Button_no,
            Button_delete
    };

    private static final int[] ButtonIds = {
            R.id.fragmentQuestion_showText,
            R.id.fragmentQuestion_read,
            R.id.fragmentQuestion_yes,
            R.id.fragmentQuestion_no,
            R.id.fragmentQuestion_delete
    };

    private int mSectionNumber;
    private int mQuestionNumber;
    private Survey mSurvey;
    private File mAudioFile;
    private int mResponse;

    private MediaPlayer mAudioPlayer;

    private ImageButton mButtons[] = new ImageButton[ButtonIds.length];
    private OnButtonClickListener mButtonListeners[] = new OnButtonClickListener[ButtonIds.length];


    public static QuestionFragment newInstance(int sectionNumber, int questionNumber) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_SectionNumber, sectionNumber);
        args.putInt(EXTRA_QuestionNumber, questionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(EXTRA_SectionNumber, Survey.Default_SectionNumber);
            mQuestionNumber = getArguments().getInt(EXTRA_QuestionNumber, 1);
        }

        mSurvey = new Survey(getActivity());
        mAudioFile = mSurvey.getAudioFile(getActivity(), mSectionNumber, mQuestionNumber);
        mResponse = mSurvey.loadResponse(getActivity(), mSectionNumber, mQuestionNumber);

        // Make sure to retain the fragment so that data retrieval is
        // not restarted at every rotation
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_question, container, false);

        ImageView imageView = (ImageView) v.findViewById(R.id.fragmentQuestion_image);
        int imageId = mSurvey.getImageId(getActivity(), mSectionNumber, mQuestionNumber);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageId);
        imageView.setImageBitmap(bitmap);

        for (int index=0; index<ButtonIds.length; index++) {
            mButtons[index] = (ImageButton) v.findViewById(ButtonIds[index]);
            if (mButtonListeners[index]==null)
                mButtonListeners[index] = new OnButtonClickListener(this, ButtonType[index]);
            mButtons[index].setOnClickListener(mButtonListeners[index]);
        }

        showResponse();
        return v;
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showResponse() {
        if (mButtons[Button_yes]!=null) {

            int id = (mResponse==Survey.response_positive) ? R.drawable.color_selected : R.drawable.color_unselected;

            Drawable drawable;
            if (AppSettings.isNewVersion())
                drawable = getResources().getDrawable(id, getActivity().getTheme());
            else
                drawable = getResources().getDrawable(id);
            mButtons[Button_yes].setBackground(drawable);
        }

        if (mButtons[Button_no]!=null) {
            int id = (mResponse==Survey.response_negative) ? R.drawable.color_selected : R.drawable.color_unselected;
            Drawable drawable;
            if (AppSettings.isNewVersion())
                drawable = getResources().getDrawable(id, getActivity().getTheme());
            else
                drawable = getResources().getDrawable(id);
            mButtons[Button_no].setBackground(drawable);
        }
    }

    private static class OnButtonClickListener implements View.OnClickListener {
        // This class of objects does not outlive its host, so no need to use weak references
        Context appContext;
        QuestionFragment hostFragment;
        int buttonType;
        public OnButtonClickListener(QuestionFragment hostFragment, int buttonType) {
            super();
            appContext = hostFragment.getActivity().getApplicationContext();
            this.hostFragment = hostFragment;
            this.buttonType = buttonType;
        }

        @Override
        public void onClick(View view) {
            if (buttonType==Button_audio) {
                hostFragment.play();
            }
            else if (buttonType==Button_text) {
                Intent intent = new Intent(hostFragment.getActivity(), TextActivity.class);
                intent.putExtra(TextActivity.EXTRA_SectionNumber, hostFragment.mSectionNumber);
                intent.putExtra(TextActivity.EXTRA_QuestionNumber, hostFragment.mQuestionNumber);
                hostFragment.startActivity(intent);
            }
            else if (buttonType==Button_yes) {
                hostFragment.mResponse = Survey.response_positive;
                hostFragment.mSurvey.saveResponse(appContext, hostFragment.mSectionNumber, hostFragment.mQuestionNumber, hostFragment.mResponse);
                hostFragment.showResponse();
            }
            else if (buttonType==Button_no) {
                hostFragment.mResponse = Survey.response_negative;
                hostFragment.mSurvey.saveResponse(appContext, hostFragment.mSectionNumber, hostFragment.mQuestionNumber, hostFragment.mResponse);
                hostFragment.showResponse();
            }
            else if (buttonType==Button_delete) {
                hostFragment.mResponse = Survey.response_unavailable;
                hostFragment.mSurvey.saveResponse(appContext, hostFragment.mSectionNumber, hostFragment.mQuestionNumber, hostFragment.mResponse);
                hostFragment.showResponse();
            }
        }
        public void cleanup() { hostFragment = null; }
    }




    // Action Play
    private boolean play() {
        boolean success = false;
        if (mAudioPlayer==null) {  // start anew

            // Assume media file is already loaded and everything is ready.
            // If there is any problem with the media file, an exception will be caught

            mAudioPlayer = new MediaPlayer();
            try {
                File mediaFile = mAudioFile;

                Savelog.d(TAG, debug, "media file " + mediaFile.getPath() + " size=" + mediaFile.length());

                mAudioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                // Note that mediaplay requires the file to have world-readable permission. So the
                // media file cannot be stored in internal storage
                mAudioPlayer.setDataSource(mediaFile.getAbsolutePath());
                mAudioPlayer.prepare();

                // Once finished, call stop to release the player.
                mAudioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    // Event  5. Media player called back
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stop();
                    }
                });


            } catch (Exception e) {
                Savelog.e(TAG, "media file not available or problem with media player.");
                mAudioPlayer.release();
                mAudioPlayer = null;
                // TODO: do we need to clear existing file?
            }
        }

        if (mAudioPlayer!=null && !mAudioPlayer.isPlaying()) {
            // Resume playing
            mAudioPlayer.start();
            success = true;
        }

        return success;
    }



    // Action Pause
    private void pause() {
        if (mAudioPlayer!=null) {
            if (mAudioPlayer.isPlaying()) {
                mAudioPlayer.pause();
            }
        }
    }

    // Action Stop
    private void stop() {
        if (mAudioPlayer != null) {
            mAudioPlayer.release();
            mAudioPlayer.setOnCompletionListener(null);
            mAudioPlayer = null;
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
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        stop();  // Must stop audio before the fragment is destroyed
        for (int index=0; index<mButtonListeners.length; index++) {
            if (mButtonListeners[index]!=null) {
                mButtonListeners[index].cleanup();
                mButtonListeners[index] = null;
            }
        }
        mSurvey = null;
        super.onDestroy();
    }

}