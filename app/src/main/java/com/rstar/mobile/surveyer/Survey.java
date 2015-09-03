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

import java.io.File;
import java.util.ArrayList;

public class Survey {
    private static final String TAG = Survey.class.getSimpleName()+"_class";
    private static final boolean debug = true;

    private static final String ResponseLabel = Survey.class.getSimpleName()+"_class." + "response";

    private static final int resource_fileId = R.raw.questions;
    private static final String SlideName = "slide";
    private static final String SlideDir = "raw";
    private static final String AudioName = "audio";
    private static final String AudioDir = "raw";
    private static final String AudioExtension = ".mp3";

    public static final int response_unavailable = 0;
    public static final int response_negative = -1;
    public static final int response_positive = 1;

    public static final int Default_SectionNumber = 1;
    public static final int Default_QuestionNumber = 1;

    private static int size = 0;
    private static int questions[][] = null;
    private static ArrayList<String> sections[] = null;

    public Survey(Context context) {
        if (size==0 || questions==null || sections==null) {
            try {
                String data = IO.getRawResourceAsString(context, resource_fileId);

                String[] lines = data.split("\\n");
                size = lines.length;
                int numbers[][] = new int[size][2];

                int numberOfSections = 0;
                for (int index=0; index<size; index++) {
                    String line = lines[index];
                    String[] fields = line.split("\t");
                    // First field is section number
                    numbers[index][0] = Integer.parseInt(fields[0]);
                    // Second field is the question number within the section
                    numbers[index][1] = Integer.parseInt(fields[1]);

                    if (numbers[index][0]>numberOfSections)
                        numberOfSections = numbers[index][0];
                }


                sections = new ArrayList[numberOfSections];
                for (int p=0; p<numberOfSections; p++) {
                    sections[p] = new ArrayList();
                }

                for (int index=0; index<size; index++) {
                    String line = lines[index];
                    String[] fields = line.split("\t");

                    // Section numbers start at 1, but ArrayList indices start at 0
                    int sectionNumber = numbers[index][0];
                    sections[sectionNumber-1].add(fields[2]);

                    Savelog.d(TAG, debug, "Section " + sectionNumber + " Q" + numbers[index][1] + ":" + fields[2]);
                }


                questions = new int[numberOfSections][];
                for (int p=0; p<numberOfSections; p++) {
                    questions[p] = new int[sections[p].size()];
                }

                for (int index=0; index<size; index++) {
                    int sectionNumber = numbers[index][0];
                    int questionNumber = numbers[index][1];
                    questions[sectionNumber-1][questionNumber-1] = index+1;
                }

            } catch (Exception e) {
                Savelog.e(TAG, e.getMessage(), e);
                // There is no question.
                size = 0;
                questions = new int[0][2];
                sections = new ArrayList[0];
            }

        }
        else {
            // Data already loaded. Do nothing.
        }
    }

    public String getQuestion(int sectionNumber, int questionNumber) {
        int sectionIndex = sectionNumber-1;
        int questionIndex = questionNumber-1;
        if (sectionIndex<0 || sectionIndex>=sections.length) return "";
        if (questionIndex<0 || questionIndex>=sections[sectionIndex].size()) return "";
        return sections[sectionIndex].get(questionIndex);
    }

    public int getSize() {
        return size;
    }

    public int getNumberOfSections() {
        return sections.length;
    }

    public int getSectionSize(int sectionNumber) {
        int sectionIndex = sectionNumber-1;
        if (sectionIndex<0 || sectionIndex>=sections.length) return 0;
        return sections[sectionIndex].size();
    }

    public int getImageId(Context context, int sectionNumber, int questionNumber) {
        int sectionIndex = sectionNumber-1;
        int questionIndex = questionNumber-1;
        if (sectionIndex<0 || sectionIndex>=sections.length) return 0;
        if (questionIndex<0 || questionIndex>=sections[sectionIndex].size()) return 0;

        int slideNumber = questions[sectionIndex][questionIndex];
        String slide = SlideDir + "/" + SlideName + slideNumber;

        return context.getResources().getIdentifier(slide, SlideDir, context.getPackageName());
    }

    private int getAudioId(Context context, int sectionNumber, int questionNumber) {
        int sectionIndex = sectionNumber-1;
        int questionIndex = questionNumber-1;
        if (sectionIndex<0 || sectionIndex>=sections.length) return 0;
        if (questionIndex<0 || questionIndex>=sections[sectionIndex].size()) return 0;

        int audioNumber = questions[sectionIndex][questionIndex];
        String audio = AudioDir + "/" + AudioName + audioNumber;

        return context.getResources().getIdentifier(audio, AudioDir, context.getPackageName());
    }

    public File getAudioFile(Context context, int sectionNumber, int questionNumber) {
        int sectionIndex = sectionNumber-1;
        int questionIndex = questionNumber-1;
        if (sectionIndex<0 || sectionIndex>=sections.length) return null;
        if (questionIndex<0 || questionIndex>=sections[sectionIndex].size()) return null;

        int audioNumber = questions[sectionIndex][questionIndex];
        String audioSrc = AudioDir + "/" + AudioName + audioNumber;
        String audioDest = AudioName + audioNumber + AudioExtension;

        int audioId = context.getResources().getIdentifier(audioSrc, AudioDir, context.getPackageName());

        File file = IO.getInternalFile(context, audioDest);

        try {
            IO.getRawResourceAsFile(context, audioId, file);
        } catch (Exception e) {
            Savelog.w(TAG, "Cannot copy internal file to " + file.getName());
            return null;
        }
        return file;
    }



    public int loadResponse(Context context, int sectionNumber, int questionNumber) {
        int sectionIndex = sectionNumber-1;
        int questionIndex = questionNumber-1;
        if (sectionIndex<0 || sectionIndex>=sections.length) return response_unavailable;
        if (questionIndex<0 || questionIndex>=sections[sectionIndex].size()) return response_unavailable;

        int position = questions[sectionIndex][questionIndex];
        String label = ResponseLabel + position;
        int response = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getInt(label, response_unavailable);
        return response;
    }

    public void saveResponse(Context context, int sectionNumber, int questionNumber, int response) {
        int sectionIndex = sectionNumber-1;
        int questionIndex = questionNumber-1;
        if (sectionIndex<0 || sectionIndex>=sections.length) return;
        if (questionIndex<0 || questionIndex>=sections[sectionIndex].size()) return;

        // Not a valid response. Do not save.
        if (response!=response_negative && response!=response_positive && response!=response_unavailable) return;

        int position = questions[sectionIndex][questionIndex];
        String label = ResponseLabel + position;
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putInt(label, response).commit();
    }

    public void clearResponses(Context context) {
        for (int sectionNumber=1; sectionNumber<=sections.length; sectionNumber++) {
            int sectionIndex = sectionNumber-1;
            for (int questionNumber=1; questionNumber<=sections[sectionIndex].size(); questionNumber++) {
                saveResponse(context, sectionNumber, questionNumber, response_unavailable);
            }
        }
    }

    public boolean isSectionDone(Context context, int sectionNumber) {
        int sectionIndex = sectionNumber-1;
        for (int questionNumber=1; questionNumber<=sections[sectionIndex].size(); questionNumber++) {
            int response = loadResponse(context, sectionNumber, questionNumber);
            if (response==response_unavailable) return false;
        }
        return true;
    }

    public int countCompletedSections(Context context) {
        int total = 0;
        for (int sectionNumber=1; sectionNumber<=sections.length; sectionNumber++) {
            if (isSectionDone(context, sectionNumber))
                total++;
        }
        return total;
    }
}
