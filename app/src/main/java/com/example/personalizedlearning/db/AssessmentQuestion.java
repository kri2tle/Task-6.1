package com.example.personalizedlearning.db;

/**
 * Model class representing an assessment question in the database
 */
public class AssessmentQuestion {
    private int id;
    private int topicId;
    private String questionText;
    private String[] options;
    private int correctAnswerIndex;
    
    public AssessmentQuestion() {
        options = new String[4];
    }
    
    public AssessmentQuestion(int id, int topicId, String questionText, String[] options, int correctAnswerIndex) {
        this.id = id;
        this.topicId = topicId;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getTopicId() {
        return topicId;
    }
    
    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public String[] getOptions() {
        return options;
    }
    
    public void setOptions(String[] options) {
        this.options = options;
    }
    
    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
    
    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }
} 