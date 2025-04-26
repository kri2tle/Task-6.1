package com.example.personalizedlearning.ui.study;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.db.DatabaseHelper;
import com.example.personalizedlearning.utils.LearningProgressManager;

public class StudyMaterialFragment extends Fragment {

    private TextView titleTextView;
    private TextView contentTextView;
    private Button markCompletedButton;
    private Button takeAssessmentButton;
    
    private int topicId;
    private String topicTitle;
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicId = getArguments().getInt("topic_id", 0);
            topicTitle = getArguments().getString("topic_title", "Study Material");
        }
        
        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_study_material, container, false);

        titleTextView = root.findViewById(R.id.material_title);
        contentTextView = root.findViewById(R.id.material_content);
        markCompletedButton = root.findViewById(R.id.mark_completed_button);
        takeAssessmentButton = root.findViewById(R.id.take_assessment_button);
        
        // Set title
        titleTextView.setText(topicTitle);
        
        // Load content based on topic ID
        loadStudyContent(topicId);
        
        // Check if topic is already completed
        boolean isCompleted = LearningProgressManager.getInstance(requireContext())
                .isTopicCompleted(topicId);
        
        if (isCompleted) {
            markCompletedButton.setVisibility(View.GONE);
            takeAssessmentButton.setVisibility(View.VISIBLE);
        } else {
            markCompletedButton.setVisibility(View.VISIBLE);
            takeAssessmentButton.setVisibility(View.GONE);
        }
        
        // Set button click listeners
        markCompletedButton.setOnClickListener(v -> {
            LearningProgressManager.getInstance(requireContext()).markTopicCompleted(topicId);
            markCompletedButton.setVisibility(View.GONE);
            takeAssessmentButton.setVisibility(View.VISIBLE);
        });
        
        takeAssessmentButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("topic_id", topicId);
            args.putString("topic_title", topicTitle);
            Navigation.findNavController(v).navigate(R.id.navigation_assessment, args);
        });
        
        return root;
    }
    
    private void loadStudyContent(int topicId) {
        try {
            // Load content from database
            String content = dbHelper.getStudyContent(topicId);
            
            // If content is available, set it
            if (content != null && !content.isEmpty()) {
                contentTextView.setText(content);
            } else {
                // Fallback to hardcoded content if database content is not available
                contentTextView.setText(getFallbackContent(topicId));
            }
        } catch (Exception e) {
            // Handle error and fallback to hardcoded content
            Toast.makeText(requireContext(), "Error loading content: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            contentTextView.setText(getFallbackContent(topicId));
        }
    }
    
    // Fallback method for hardcoded content in case database content is not available
    private String getFallbackContent(int topicId) {
        switch (topicId) {
            case 1: // Introduction to Mobile Development
                return "Mobile development is the process of creating applications that run on mobile devices.\n\n" +
                        "Key concepts in mobile development:\n" +
                        "• Native vs hybrid vs web apps\n" +
                        "• Platform-specific considerations\n" +
                        "• User interface design\n" +
                        "• Performance optimization\n" +
                        "• Device feature utilization\n\n" +
                        "Android is an open-source mobile operating system developed by Google. " +
                        "Android applications are primarily written in Java or Kotlin and use XML for layouts.";
                
            case 2: // UI Components
                return "Android UI Components are the building blocks for creating user interfaces.\n\n" +
                        "Essential UI Components include:\n" +
                        "• TextView: Displays text to the user\n" +
                        "• EditText: Allows user input\n" +
                        "• Button: Triggers actions when clicked\n" +
                        "• ImageView: Displays images\n" +
                        "• RecyclerView: Efficiently displays scrollable lists\n" +
                        "• CardView: Material design container with shadow\n\n" +
                        "Layouts are used to arrange UI components on the screen, including LinearLayout, RelativeLayout, ConstraintLayout, etc.";
                
            case 3: // User Authentication
                return "User authentication is the process of verifying a user's identity.\n\n" +
                        "Common authentication methods:\n" +
                        "• Username/password\n" +
                        "• OAuth 2.0\n" +
                        "• Firebase Authentication\n" +
                        "• Biometric authentication\n\n" +
                        "Best practices for authentication:\n" +
                        "• Never store passwords in plain text\n" +
                        "• Use secure tokens for sessions\n" +
                        "• Implement proper error handling\n" +
                        "• Provide account recovery options";
                
            case 4: // Data Storage
                return "Android provides several options for storing data locally on the device.\n\n" +
                        "Data storage options:\n" +
                        "• Shared Preferences: Store primitive data in key-value pairs\n" +
                        "• Internal Storage: Store private data on the device's filesystem\n" +
                        "• External Storage: Store public data on shared external storage\n" +
                        "• SQLite Database: Store structured data in a local database\n" +
                        "• Room: A persistence library providing an abstraction layer over SQLite\n\n" +
                        "The choice of storage method depends on the app's specific requirements.";
                
            case 5: // API Integration
                return "APIs (Application Programming Interfaces) allow your app to communicate with external services.\n\n" +
                        "Key considerations for API integration:\n" +
                        "• RESTful API design principles\n" +
                        "• Authentication and security\n" +
                        "• Handling network requests asynchronously\n" +
                        "• Parsing JSON or XML responses\n\n" +
                        "Common libraries for API integration:\n" +
                        "• Retrofit: Type-safe HTTP client\n" +
                        "• OkHttp: HTTP client for efficient network requests\n" +
                        "• Volley: Google's HTTP library\n" +
                        "• Gson/Jackson: JSON parsing libraries";
                
            default:
                return "Content not available for this topic.";
        }
    }
} 