package com.example.personalizedlearning.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class LearningProgressManager {
    private static final String PREF_NAME = "learning_progress_prefs";
    private static final String KEY_COMPLETED_TOPICS = "completed_topics";
    private static final String KEY_ASSESSMENT_SCORES = "assessment_scores";

    private static LearningProgressManager instance;
    private final SharedPreferences preferences;

    private LearningProgressManager(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized LearningProgressManager getInstance(Context context) {
        if (instance == null) {
            instance = new LearningProgressManager(context);
        }
        return instance;
    }

    public boolean isTopicCompleted(int topicId) {
        Set<String> completedTopics = getCompletedTopics();
        return completedTopics.contains(String.valueOf(topicId));
    }

    public void markTopicCompleted(int topicId) {
        Set<String> completedTopics = getCompletedTopics();
        completedTopics.add(String.valueOf(topicId));
        saveCompletedTopics(completedTopics);
    }

    public void markTopicUncompleted(int topicId) {
        Set<String> completedTopics = getCompletedTopics();
        completedTopics.remove(String.valueOf(topicId));
        saveCompletedTopics(completedTopics);
    }

    public int getCompletedTopicsCount() {
        return getCompletedTopics().size();
    }

    public void saveAssessmentScore(int topicId, int score) {
        preferences.edit().putInt(KEY_ASSESSMENT_SCORES + "_" + topicId, score).apply();
    }

    public int getAssessmentScore(int topicId) {
        return preferences.getInt(KEY_ASSESSMENT_SCORES + "_" + topicId, 0);
    }

    public float getOverallProgress(int totalTopics) {
        if (totalTopics == 0) return 0;
        return (float) getCompletedTopicsCount() / totalTopics * 100;
    }

    private Set<String> getCompletedTopics() {
        return preferences.getStringSet(KEY_COMPLETED_TOPICS, new HashSet<>());
    }

    private void saveCompletedTopics(Set<String> completedTopics) {
        preferences.edit().putStringSet(KEY_COMPLETED_TOPICS, completedTopics).apply();
    }

    public void clearProgress() {
        preferences.edit().clear().apply();
    }
} 