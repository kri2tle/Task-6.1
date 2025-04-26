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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.db.DatabaseHelper;
import com.example.personalizedlearning.db.UserProfile;
import com.example.personalizedlearning.utils.ProfileManager;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class SignInFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private Button signUpButton;
    private TextView signUpLink;
    private NavController navController;
    private DatabaseHelper dbHelper;
    
    // Store topic information for redirecting after sign in
    private int topicId = 0;
    private String topicTitle = "";
    private boolean shouldRedirect = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get arguments if coming from a topic selection
        if (getArguments() != null) {
            topicId = getArguments().getInt("topic_id", 0);
            topicTitle = getArguments().getString("topic_title", "");
            shouldRedirect = topicId > 0 && !topicTitle.isEmpty();
        }
        
        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sign_in, container, false);
        
        try {
            // Get the NavController safely
            navController = NavHostFragment.findNavController(this);
            
            // Initialize views
            emailEditText = root.findViewById(R.id.email_edit_text);
            passwordEditText = root.findViewById(R.id.password_edit_text);
            signInButton = root.findViewById(R.id.sign_in_button);
            signUpButton = root.findViewById(R.id.sign_up_button);
            signUpLink = root.findViewById(R.id.sign_up_link);
            
            // Auto-redirect if already signed in
            boolean isProfileCreated = false;
            try {
                isProfileCreated = ProfileManager.getInstance(requireContext()).isProfileCreated();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if (isProfileCreated) {
                if (shouldRedirect) {
                    redirectToStudyMaterial();
                } else {
                    navigateToHome();
                }
            }
            
            signInButton.setOnClickListener(v -> attemptSignIn());
            
            // Both sign up button and link navigate to sign up
            View.OnClickListener signUpListener = v -> {
                // Pass along any topic arguments to sign up
                Bundle args = new Bundle();
                if (shouldRedirect) {
                    args.putInt("topic_id", topicId);
                    args.putString("topic_title", topicTitle);
                }
                safeNavigate(R.id.navigation_sign_up, args);
            };
            
            signUpButton.setOnClickListener(signUpListener);
            if (signUpLink != null) {
                signUpLink.setOnClickListener(signUpListener);
            }
            
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error initializing sign-in screen: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        
        return root;
    }
    
    private void attemptSignIn() {
        try {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            
            // Validate inputs
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Hash the password to compare with stored hash
            String passwordHash = hashPassword(password);
            
            // Check if user exists in database
            UserProfile userProfile = dbHelper.getUserByEmail(email);
            
            if (userProfile != null && userProfile.getPasswordHash().equals(passwordHash)) {
                // Valid user found, update SharedPreferences for backward compatibility
                ProfileManager profileManager = ProfileManager.getInstance(requireContext());
                profileManager.saveProfile(userProfile.getUsername(), email, userProfile.getInterests());
                
                Toast.makeText(requireContext(), "Signed in successfully!", Toast.LENGTH_SHORT).show();
                
                // Navigate based on where the user came from
                if (shouldRedirect) {
                    redirectToStudyMaterial();
                } else {
                    navigateToHome();
                }
            } else {
                if (userProfile == null) {
                    // User doesn't exist - show signup required dialog
                    showSignUpRequiredDialog();
                } else {
                    // User exists but password is incorrect
                    Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Sign-in error: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    
    private void showSignUpRequiredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Account Not Found");
        builder.setMessage("No account exists with this email address. Would you like to create a new account?");
        
        // Add buttons
        builder.setPositiveButton("Sign Up", (dialog, id) -> {
            // Navigate to sign up screen with the email pre-filled
            Bundle args = new Bundle();
            args.putString("prefill_email", emailEditText.getText().toString().trim());
            
            // Also pass along any topic information
            if (shouldRedirect) {
                args.putInt("topic_id", topicId);
                args.putString("topic_title", topicTitle);
            }
            
            safeNavigate(R.id.navigation_sign_up, args);
        });
        
        builder.setNegativeButton("Cancel", (dialog, id) -> {
            dialog.dismiss();
        });
        
        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
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