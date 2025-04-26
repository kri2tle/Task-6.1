package com.example.personalizedlearning.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.db.DatabaseHelper;
import com.example.personalizedlearning.utils.ProfileManager;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class SignUpFragment extends Fragment {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signUpButton;
    private TextView signInLink;
    private NavController navController;
    private DatabaseHelper dbHelper;
    
    // Store topic information for redirecting after sign up
    private int topicId = 0;
    private String topicTitle = "";
    private boolean shouldRedirect = false;
    
    // Store email for pre-filling from sign-in screen
    private String prefillEmail = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get arguments if coming from a topic selection or sign-in screen
        if (getArguments() != null) {
            topicId = getArguments().getInt("topic_id", 0);
            topicTitle = getArguments().getString("topic_title", "");
            shouldRedirect = topicId > 0 && !topicTitle.isEmpty();
            
            // Check if we have an email to pre-fill
            prefillEmail = getArguments().getString("prefill_email", "");
        }
        
        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sign_up, container, false);
        
        try {
            // Get NavController safely
            navController = NavHostFragment.findNavController(this);
            
            // Initialize views
            nameEditText = root.findViewById(R.id.name_edit_text);
            emailEditText = root.findViewById(R.id.email_edit_text);
            passwordEditText = root.findViewById(R.id.password_edit_text);
            confirmPasswordEditText = root.findViewById(R.id.confirm_password_edit_text);
            signUpButton = root.findViewById(R.id.sign_up_button);
            signInLink = root.findViewById(R.id.sign_in_link);
            
            // Pre-fill email if available
            if (!prefillEmail.isEmpty()) {
                emailEditText.setText(prefillEmail);
                // Also set the username field with the email prefix
                if (prefillEmail.contains("@")) {
                    nameEditText.setText(prefillEmail.split("@")[0]);
                }
            }
            
            // Setup click listeners
            signUpButton.setOnClickListener(v -> attemptSignUp());
            
            if (signInLink != null) {
                signInLink.setOnClickListener(v -> {
                    // Pass along any topic arguments to sign in
                    Bundle args = new Bundle();
                    if (shouldRedirect) {
                        args.putInt("topic_id", topicId);
                        args.putString("topic_title", topicTitle);
                    }
                    safeNavigate(R.id.navigation_sign_in, args);
                });
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error initializing sign-up screen: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        
        return root;
    }
    
    private void attemptSignUp() {
        try {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            
            // Validate inputs
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Simple email validation
            if (!email.contains("@") || !email.contains(".")) {
                Toast.makeText(requireContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Password strength validation
            if (password.length() < 6) {
                Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Check if user already exists
            if (dbHelper.getUserByEmail(email) != null) {
                Toast.makeText(requireContext(), "An account with this email already exists. Please sign in instead.", 
                        Toast.LENGTH_LONG).show();
                return;
            }
            
            // Hash the password - in a real app, use a proper hashing library with salt
            String passwordHash = hashPassword(password);
            
            // Create user profile in database
            long userId = dbHelper.createUserProfile(name, email, passwordHash, "");
            
            if (userId > 0) {
                // Also save to SharedPreferences for backward compatibility
                ProfileManager profileManager = ProfileManager.getInstance(requireContext());
                profileManager.saveProfile(name, email, "");
                
                Toast.makeText(requireContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();
                
                // Navigate based on where the user came from
                if (shouldRedirect) {
                    redirectToStudyMaterial();
                } else {
                    navigateToHome();
                }
            } else {
                Toast.makeText(requireContext(), "Failed to create account. Email may already exist.", 
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Sign-up error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    
    // Simple password hashing function - in a real app, use a proper hashing library
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to plain text if hashing fails (not secure, just to avoid crashes)
            return password;
        }
    }
    
    private void redirectToStudyMaterial() {
        Bundle args = new Bundle();
        args.putInt("topic_id", topicId);
        args.putString("topic_title", topicTitle);
        safeNavigate(R.id.navigation_study_material, args);
    }
    
    private void navigateToHome() {
        safeNavigate(R.id.navigation_home, null);
    }
    
    private void safeNavigate(int destinationId, Bundle args) {
        try {
            if (navController != null) {
                if (args != null) {
                    navController.navigate(destinationId, args);
                } else {
                    navController.navigate(destinationId);
                }
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Navigation error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
} 