package com.example.personalizedlearning.ui.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.db.DatabaseHelper;
import com.example.personalizedlearning.utils.DataClearUtil;
import com.example.personalizedlearning.utils.ProfileManager;

public class ProfileFragment extends Fragment {

    private ProfileManager profileManager;
    private EditText nameEditText, emailEditText, interestsEditText;
    private Button saveButton;
    private View profileFormLayout, profileInfoLayout;
    private TextView nameTextView, emailTextView, interestsTextView;
    private Button editButton, deleteProfileButton, signOutButton;
    
    // Store topic information for redirecting after profile creation
    private int topicId = 0;
    private String topicTitle = "";
    private boolean shouldRedirect = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get arguments if coming from a topic selection
        if (getArguments() != null) {
            topicId = getArguments().getInt("topic_id", 0);
            topicTitle = getArguments().getString("topic_title", "");
            shouldRedirect = topicId > 0 && !topicTitle.isEmpty();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // Initialize profile manager
        profileManager = ProfileManager.getInstance(requireContext());
        
        // Initialize views
        profileFormLayout = root.findViewById(R.id.profile_form_layout);
        profileInfoLayout = root.findViewById(R.id.profile_info_layout);
        
        nameEditText = root.findViewById(R.id.name_edit_text);
        emailEditText = root.findViewById(R.id.email_edit_text);
        interestsEditText = root.findViewById(R.id.interests_edit_text);
        saveButton = root.findViewById(R.id.save_button);
        
        nameTextView = root.findViewById(R.id.name_text_view);
        emailTextView = root.findViewById(R.id.email_text_view);
        interestsTextView = root.findViewById(R.id.interests_text_view);
        editButton = root.findViewById(R.id.edit_button);
        deleteProfileButton = root.findViewById(R.id.delete_profile_button);
        signOutButton = root.findViewById(R.id.sign_out_button);
        
        // Set up listeners
        saveButton.setOnClickListener(v -> saveProfile());
        editButton.setOnClickListener(v -> showEditForm());
        deleteProfileButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        signOutButton.setOnClickListener(v -> signOut());
        
        // Check if profile already exists
        if (profileManager.isProfileCreated()) {
            // Show profile info
            showProfileInfo();
        } else {
            // Show profile form
            showEditForm();
        }
        
        return root;
    }
    
    private void saveProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String interests = interestsEditText.getText().toString().trim();
        
        // Validate fields
        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(requireContext(), "Name and email are required", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Save profile data
        profileManager.saveProfile(name, email, interests);
        
        // Show profile info
        showProfileInfo();
        
        Toast.makeText(requireContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show();
        
        // If we should redirect to a specific topic's study material
        if (shouldRedirect) {
            redirectToStudyMaterial();
        }
    }
    
    private void redirectToStudyMaterial() {
        if (topicId > 0) {
            Bundle args = new Bundle();
            args.putInt("topic_id", topicId);
            args.putString("topic_title", topicTitle);
            Navigation.findNavController(requireView()).navigate(R.id.navigation_study_material, args);
        }
    }
    
    private void showProfileInfo() {
        // Update profile info views
        nameTextView.setText(profileManager.getUsername());
        emailTextView.setText(profileManager.getEmail());
        interestsTextView.setText(profileManager.getInterests());
        
        // Show profile info layout and hide form layout
        profileInfoLayout.setVisibility(View.VISIBLE);
        profileFormLayout.setVisibility(View.GONE);
        
        // If we have a topic and just created the profile, redirect
        if (shouldRedirect && profileManager.isProfileCreated()) {
            // Small delay to show the success message
            requireView().postDelayed(this::redirectToStudyMaterial, 1000);
        }
    }
    
    private void showEditForm() {
        // Pre-fill form if profile exists
        if (profileManager.isProfileCreated()) {
            nameEditText.setText(profileManager.getUsername());
            emailEditText.setText(profileManager.getEmail());
            interestsEditText.setText(profileManager.getInterests());
        }
        
        // Show form layout and hide profile info layout
        profileFormLayout.setVisibility(View.VISIBLE);
        profileInfoLayout.setVisibility(View.GONE);
    }
    
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Profile");
        builder.setMessage("Are you sure you want to delete your profile and all associated data? This action cannot be undone.");
        
        // Add the buttons
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Delete button
                deleteProfile();
            }
        });
        
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });
        
        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    private void deleteProfile() {
        try {
            // Get the email before deleting
            String email = ProfileManager.getInstance(requireContext()).getEmail();
            
            // Call the utility method to clear all user data
            DataClearUtil.clearAllData(requireContext());
            
            // Delete user from database
            DatabaseHelper.getInstance(requireContext()).deleteUserByEmail(email);
            
            // Show success message
            Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
            
            // Navigate to sign in screen
            Navigation.findNavController(requireView())
                    .navigate(R.id.navigation_sign_in);
            
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error deleting account: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure you want to sign out?");
        
        builder.setPositiveButton("Sign Out", (dialog, id) -> {
            // Clear session data
            ProfileManager profileManager = ProfileManager.getInstance(requireContext());
            profileManager.clearSession();
            profileManager.setProfileCreated(false); // Mark profile as not created to force login
            
            // Show success message
            Toast.makeText(requireContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
            
            // Navigate to sign in screen and clear back stack
            Navigation.findNavController(requireView())
                    .navigate(R.id.navigation_sign_in);
        });
        
        builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }
} 