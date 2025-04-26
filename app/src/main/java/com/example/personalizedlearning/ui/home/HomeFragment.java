package com.example.personalizedlearning.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.db.DatabaseHelper;
import com.example.personalizedlearning.db.Topic;
import com.example.personalizedlearning.utils.ProfileManager;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView topicsRecyclerView;
    private TopicsAdapter topicsAdapter;
    private NavController navController;
    private DatabaseHelper dbHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        
        try {
            // Get NavController safely
            navController = NavHostFragment.findNavController(this);
            
            // Initialize database helper
            dbHelper = DatabaseHelper.getInstance(requireContext());
            
            // Initialize RecyclerView
            topicsRecyclerView = root.findViewById(R.id.topics_recycler_view);
            if (topicsRecyclerView != null) {
                topicsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                
                // Get topics from database
                List<Topic> dbTopics = dbHelper.getAllTopics();
                List<LearningTopic> topics = new ArrayList<>();
                
                // Convert database topics to UI topics
                int defaultDrawable = R.drawable.ic_mobile_dev;
                for (Topic dbTopic : dbTopics) {
                    topics.add(new LearningTopic(
                            dbTopic.getId(),
                            dbTopic.getTitle(),
                            dbTopic.getDescription(),
                            dbTopic.getImageResId() > 0 ? dbTopic.getImageResId() : defaultDrawable
                    ));
                }
                
                // If no topics found in database, create sample topics
                if (topics.isEmpty()) {
                    topics = getSampleTopics();
                }
                
                // Set up adapter with topic click listener
                topicsAdapter = new TopicsAdapter(topics, topic -> handleTopicClick(topic));
                topicsRecyclerView.setAdapter(topicsAdapter);
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error initializing home screen: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        
        return root;
    }
    
    private void handleTopicClick(LearningTopic topic) {
        try {
            // Check if user has a profile
            boolean isAuthenticated = false;
            try {
                isAuthenticated = ProfileManager.getInstance(requireContext()).isProfileCreated();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if (!isAuthenticated) {
                // Navigate to sign in screen
                Bundle args = new Bundle();
                args.putString("topic_title", topic.getTitle());
                args.putInt("topic_id", topic.getId());
                safeNavigate(R.id.navigation_sign_in, args);
                
                Toast.makeText(requireContext(), 
                        "Please sign in to access learning materials", 
                        Toast.LENGTH_SHORT).show();
            } else {
                // Navigate to study material
                Bundle args = new Bundle();
                args.putString("topic_title", topic.getTitle());
                args.putInt("topic_id", topic.getId());
                safeNavigate(R.id.navigation_study_material, args);
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error navigating: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    
    private void safeNavigate(int destinationId, Bundle args) {
        try {
            if (navController != null) {
                navController.navigate(destinationId, args);
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Navigation error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    
    // Fallback method for sample topics in case database is empty
    private List<LearningTopic> getSampleTopics() {
        List<LearningTopic> topics = new ArrayList<>();
        
        // Use a fallback drawable for all topics to prevent resource not found errors
        int defaultDrawable = R.drawable.ic_mobile_dev;
        
        topics.add(new LearningTopic(1, "Introduction to Mobile Development", 
                "Learn the basics of mobile app development", defaultDrawable));
        
        topics.add(new LearningTopic(2, "UI Components", 
                "Explore various UI components in Android", defaultDrawable));
        
        topics.add(new LearningTopic(3, "User Authentication", 
                "Implement secure user authentication", defaultDrawable));
        
        topics.add(new LearningTopic(4, "Data Storage", 
                "Discover options for storing data in Android apps", defaultDrawable));
        
        topics.add(new LearningTopic(5, "API Integration", 
                "Connect your app with external APIs", defaultDrawable));
        
        return topics;
    }
    
    // Topic click listener interface
    public interface TopicClickListener {
        void onTopicClick(LearningTopic topic);
    }
    
    // LearningTopic model class
    public static class LearningTopic {
        private int id;
        private String title;
        private String description;
        private int imageResId;
        
        public LearningTopic(int id, String title, String description, int imageResId) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.imageResId = imageResId;
        }
        
        public int getId() {
            return id;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getDescription() {
            return description;
        }
        
        public int getImageResId() {
            return imageResId;
        }
    }
    
    // RecyclerView Adapter
    private class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.TopicViewHolder> {
        private List<LearningTopic> topics;
        private TopicClickListener clickListener;
        
        public TopicsAdapter(List<LearningTopic> topics, TopicClickListener clickListener) {
            this.topics = topics;
            this.clickListener = clickListener;
        }
        
        @NonNull
        @Override
        public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_learning_topic, parent, false);
            return new TopicViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
            LearningTopic topic = topics.get(position);
            holder.titleTextView.setText(topic.getTitle());
            holder.descriptionTextView.setText(topic.getDescription());
            
            // Set click listener
            holder.itemView.setOnClickListener(v -> clickListener.onTopicClick(topic));
        }
        
        @Override
        public int getItemCount() {
            return topics.size();
        }
        
        class TopicViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;
            TextView descriptionTextView;
            
            public TopicViewHolder(@NonNull View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.topic_title);
                descriptionTextView = itemView.findViewById(R.id.topic_description);
            }
        }
    }
} 