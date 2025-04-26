package com.example.personalizedlearning.ui.learning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalizedlearning.R;

import java.util.ArrayList;
import java.util.List;

public class LearningFragment extends Fragment {

    private RecyclerView coursesRecyclerView;
    private LearningCoursesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_learning, container, false);
        
        // Initialize RecyclerView
        coursesRecyclerView = root.findViewById(R.id.courses_recycler_view);
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        // Create sample courses data
        List<LearningCourse> courses = getSampleCourses();
        
        // Set up adapter
        adapter = new LearningCoursesAdapter(courses);
        coursesRecyclerView.setAdapter(adapter);
        
        // Set the overall progress
        ProgressBar overallProgress = root.findViewById(R.id.overall_progress);
        TextView progressText = root.findViewById(R.id.progress_text);
        
        int progress = calculateOverallProgress(courses);
        overallProgress.setProgress(progress);
        progressText.setText(progress + "% Complete");
        
        return root;
    }
    
    private int calculateOverallProgress(List<LearningCourse> courses) {
        if (courses.isEmpty()) return 0;
        
        int totalProgress = 0;
        for (LearningCourse course : courses) {
            totalProgress += course.getProgress();
        }
        
        return totalProgress / courses.size();
    }
    
    private List<LearningCourse> getSampleCourses() {
        List<LearningCourse> courses = new ArrayList<>();
        courses.add(new LearningCourse("Introduction to Mobile Development", 90));
        courses.add(new LearningCourse("UI Components", 60));
        courses.add(new LearningCourse("User Authentication", 30));
        courses.add(new LearningCourse("Data Storage", 15));
        courses.add(new LearningCourse("API Integration", 0));
        return courses;
    }
    
    // LearningCourse model class
    public static class LearningCourse {
        private String title;
        private int progress;
        
        public LearningCourse(String title, int progress) {
            this.title = title;
            this.progress = progress;
        }
        
        public String getTitle() {
            return title;
        }
        
        public int getProgress() {
            return progress;
        }
    }
    
    // RecyclerView Adapter
    private class LearningCoursesAdapter extends RecyclerView.Adapter<LearningCoursesAdapter.CourseViewHolder> {
        private List<LearningCourse> courses;
        
        public LearningCoursesAdapter(List<LearningCourse> courses) {
            this.courses = courses;
        }
        
        @NonNull
        @Override
        public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_learning_course, parent, false);
            return new CourseViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
            LearningCourse course = courses.get(position);
            holder.titleTextView.setText(course.getTitle());
            holder.progressBar.setProgress(course.getProgress());
            holder.progressTextView.setText(course.getProgress() + "% Complete");
        }
        
        @Override
        public int getItemCount() {
            return courses.size();
        }
        
        class CourseViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;
            ProgressBar progressBar;
            TextView progressTextView;
            
            public CourseViewHolder(@NonNull View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.course_title);
                progressBar = itemView.findViewById(R.id.course_progress);
                progressTextView = itemView.findViewById(R.id.course_progress_text);
            }
        }
    }
} 