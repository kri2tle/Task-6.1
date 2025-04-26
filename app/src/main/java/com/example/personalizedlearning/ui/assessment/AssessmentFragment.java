package com.example.personalizedlearning.ui.assessment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.db.AssessmentQuestion;
import com.example.personalizedlearning.db.DatabaseHelper;
import com.example.personalizedlearning.utils.LearningProgressManager;
import com.example.personalizedlearning.utils.ProfileManager;

import java.util.ArrayList;
import java.util.List;

public class AssessmentFragment extends Fragment {

    private View profileRequiredMessage;
    private View assessmentContent;
    private TextView assessmentTitleView;
    private TextView questionTextView;
    private RadioGroup optionsRadioGroup;
    private Button nextButton;
    private Button finishButton;
    
    private int topicId = 0;
    private String topicTitle = "";
    private int currentQuestionIndex = 0;
    private int score = 0;
    private List<AssessmentQuestion> questions = new ArrayList<>();
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicId = getArguments().getInt("topic_id", 0);
            topicTitle = getArguments().getString("topic_title", "Assessment");
        }
        
        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_assessment, container, false);
        
        // Initialize views
        profileRequiredMessage = root.findViewById(R.id.profile_required_message);
        assessmentContent = root.findViewById(R.id.assessment_content);
        assessmentTitleView = root.findViewById(R.id.assessment_title);
        questionTextView = root.findViewById(R.id.question_text);
        optionsRadioGroup = root.findViewById(R.id.options_radio_group);
        nextButton = root.findViewById(R.id.next_button);
        finishButton = root.findViewById(R.id.finish_button);
        
        // Set title
        assessmentTitleView.setText(topicTitle + " Assessment");
        
        // Check if user is authenticated
        boolean isAuthenticated = ProfileManager.getInstance(requireContext()).isProfileCreated();
        
        if (!isAuthenticated) {
            // Show message that profile is required
            profileRequiredMessage.setVisibility(View.VISIBLE);
            assessmentContent.setVisibility(View.GONE);
            
            // Set up sign in button
            Button signInButton = root.findViewById(R.id.sign_in_button);
            signInButton.setOnClickListener(v -> 
                    Navigation.findNavController(v).navigate(R.id.navigation_sign_in));
        } else {
            // Show assessment content
            profileRequiredMessage.setVisibility(View.GONE);
            assessmentContent.setVisibility(View.VISIBLE);
            
            // Reset score and current question
            currentQuestionIndex = 0;
            score = 0;
            
            // Prepare assessment questions for this topic
            prepareAssessment(topicId);
            
            // Display first question if available
            if (!questions.isEmpty()) {
                displayQuestion(0);
            }
            
            // Set up buttons
            nextButton.setOnClickListener(v -> handleNextQuestion());
            finishButton.setOnClickListener(v -> finishAssessment());
        }
        
        return root;
    }
    
    private void handleNextQuestion() {
        // Save answer for current question
        int selectedOptionId = optionsRadioGroup.getCheckedRadioButtonId();
        if (selectedOptionId == -1) {
            Toast.makeText(requireContext(), "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Find the selected option index (0-based)
        int selectedOptionIndex = -1;
        for (int i = 0; i < optionsRadioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) optionsRadioGroup.getChildAt(i);
            if (radioButton.getId() == selectedOptionId) {
                selectedOptionIndex = i;
                break;
            }
        }
        
        // Check if answer is correct
        if (selectedOptionIndex == questions.get(currentQuestionIndex).getCorrectAnswerIndex()) {
            score++;
        }
        
        // Move to next question or finish
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            displayQuestion(currentQuestionIndex);
        } else {
            // Show results
            finishAssessment();
        }
    }
    
    private void displayQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;
        
        AssessmentQuestion question = questions.get(index);
        questionTextView.setText((index + 1) + ". " + question.getQuestionText());
        
        // Clear previous options
        optionsRadioGroup.removeAllViews();
        optionsRadioGroup.clearCheck();
        
        // Add options as radio buttons
        for (int i = 0; i < question.getOptions().length; i++) {
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setId(View.generateViewId());
            radioButton.setText(question.getOptions()[i]);
            radioButton.setTextSize(16);
            radioButton.setPadding(0, 8, 0, 8);
            optionsRadioGroup.addView(radioButton);
        }
        
        // Update button visibility
        if (index == questions.size() - 1) {
            nextButton.setVisibility(View.GONE);
            finishButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
            finishButton.setVisibility(View.GONE);
        }
    }
    
    private void finishAssessment() {
        // Handle the last question if needed
        if (currentQuestionIndex < questions.size()) {
            handleNextQuestion();
            return;
        }
        
        // Calculate percentage score
        int percentage = (int) (((float) score / questions.size()) * 100);
        
        // Save assessment results
        LearningProgressManager.getInstance(requireContext())
                .saveAssessmentScore(topicId, percentage);
        
        try {
            // Also save to the database if there is a user profile
            ProfileManager profileManager = ProfileManager.getInstance(requireContext());
            if (profileManager.isProfileCreated()) {
                // In a real app, you would get the user ID from the database
                // Here we'll use 1 as a dummy user ID
                int userId = 1;
                dbHelper.markTopicCompleted(userId, topicId, percentage);
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error saving progress: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
        }
        
        // Show results dialog
        View resultView = getLayoutInflater().inflate(R.layout.dialog_assessment_result, null);
        TextView scoreTextView = resultView.findViewById(R.id.score_text);
        TextView messageTextView = resultView.findViewById(R.id.result_message);
        Button continueButton = resultView.findViewById(R.id.continue_button);
        
        scoreTextView.setText(percentage + "%");
        
        // Set message based on score
        if (percentage >= 80) {
            messageTextView.setText("Excellent work! You've mastered this topic.");
        } else if (percentage >= 60) {
            messageTextView.setText("Good job! You understand most of the material.");
        } else {
            messageTextView.setText("You might want to review this topic again.");
        }
        
        // Show dialog
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Assessment Complete")
                .setView(resultView)
                .setCancelable(false)
                .create();
        
        // Setup continue button
        continueButton.setOnClickListener(v -> {
            dialog.dismiss();
            // Navigate back to learning screen
            Navigation.findNavController(requireView()).navigate(R.id.navigation_learning);
        });
        
        dialog.show();
    }
    
    private void prepareAssessment(int topicId) {
        try {
            // Try to get questions from database
            List<AssessmentQuestion> dbQuestions = dbHelper.getQuestionsForTopic(topicId);
            
            if (dbQuestions != null && !dbQuestions.isEmpty()) {
                questions = dbQuestions;
            } else {
                // Fallback to hardcoded questions if database is empty
                createFallbackQuestions(topicId);
            }
        } catch (Exception e) {
            // Handle error and fallback to hardcoded questions
            Toast.makeText(requireContext(), "Error loading questions: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            createFallbackQuestions(topicId);
        }
    }
    
    // Fallback method for hardcoded questions in case database is empty
    private void createFallbackQuestions(int topicId) {
        questions.clear();
        
        switch (topicId) {
            case 1: // Introduction to Mobile Development
                questions.add(new AssessmentQuestion(
                        1, topicId,
                        "What is the primary programming language for Android development?",
                        new String[]{"Swift", "Java/Kotlin", "C#", "JavaScript"},
                        1
                ));
                questions.add(new AssessmentQuestion(
                        2, topicId,
                        "Which of the following is NOT a mobile app development approach?",
                        new String[]{"Native", "Hybrid", "Web", "Sequential"},
                        3
                ));
                questions.add(new AssessmentQuestion(
                        3, topicId,
                        "What file format is used for Android layouts?",
                        new String[]{"JSON", "XML", "HTML", "CSS"},
                        1
                ));
                break;
                
            case 2: // UI Components
                questions.add(new AssessmentQuestion(
                        4, topicId,
                        "Which component is used to display scrollable lists in Android?",
                        new String[]{"TextView", "ScrollView", "RecyclerView", "ListView"},
                        2
                ));
                questions.add(new AssessmentQuestion(
                        5, topicId,
                        "Which layout positions elements relative to each other?",
                        new String[]{"LinearLayout", "RelativeLayout", "ConstraintLayout", "FrameLayout"},
                        1
                ));
                questions.add(new AssessmentQuestion(
                        6, topicId,
                        "What is the purpose of a CardView?",
                        new String[]{"To display images", "To show notification cards", "To create material design containers with shadows", "To render video content"},
                        2
                ));
                break;
                
            // Add more topic-specific questions for other topics
            default:
                // Generic questions if no specific topic
                questions.add(new AssessmentQuestion(
                        7, topicId,
                        "What does API stand for?",
                        new String[]{"Application Programming Interface", "Android Programming Interface", "Application Process Integration", "Automated Programming Interface"},
                        0
                ));
                questions.add(new AssessmentQuestion(
                        8, topicId,
                        "Which of the following is a way to store data in Android?",
                        new String[]{"SharedPreferences", "DataStorage", "AndroidCache", "MemoryBuffer"},
                        0
                ));
                questions.add(new AssessmentQuestion(
                        9, topicId,
                        "What is the entry point of an Android app?",
                        new String[]{"MainActivity", "StartActivity", "LauncherActivity", "InitActivity"},
                        0
                ));
                break;
        }
    }
} 