package com.example.personalizedlearning.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "learning_db";

    // Table Names
    private static final String TABLE_TOPICS = "topics";
    private static final String TABLE_STUDY_MATERIALS = "study_materials";
    private static final String TABLE_ASSESSMENT_QUESTIONS = "assessment_questions";
    private static final String TABLE_USER_PROGRESS = "user_progress";
    private static final String TABLE_USER_PROFILES = "user_profiles";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_TOPIC_ID = "topic_id";

    // TOPICS Table - column names
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE_RES_ID = "image_res_id";

    // STUDY_MATERIALS Table - column names
    private static final String KEY_CONTENT = "content";

    // ASSESSMENT_QUESTIONS Table - column names
    private static final String KEY_QUESTION_TEXT = "question_text";
    private static final String KEY_OPTION_A = "option_a";
    private static final String KEY_OPTION_B = "option_b";
    private static final String KEY_OPTION_C = "option_c";
    private static final String KEY_OPTION_D = "option_d";
    private static final String KEY_CORRECT_OPTION = "correct_option";

    // USER_PROGRESS Table - column names
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_COMPLETED = "completed";
    private static final String KEY_SCORE = "score";

    // USER_PROFILES Table - column names
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_INTERESTS = "interests";
    private static final String KEY_PASSWORD_HASH = "password_hash";

    // Create Table statements
    private static final String CREATE_TABLE_TOPICS = "CREATE TABLE " + TABLE_TOPICS +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_TITLE + " TEXT," +
            KEY_DESCRIPTION + " TEXT," +
            KEY_IMAGE_RES_ID + " INTEGER" +
            ")";

    private static final String CREATE_TABLE_STUDY_MATERIALS = "CREATE TABLE " + TABLE_STUDY_MATERIALS +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_TOPIC_ID + " INTEGER," +
            KEY_CONTENT + " TEXT" +
            ")";

    private static final String CREATE_TABLE_ASSESSMENT_QUESTIONS = "CREATE TABLE " + TABLE_ASSESSMENT_QUESTIONS +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_TOPIC_ID + " INTEGER," +
            KEY_QUESTION_TEXT + " TEXT," +
            KEY_OPTION_A + " TEXT," +
            KEY_OPTION_B + " TEXT," +
            KEY_OPTION_C + " TEXT," +
            KEY_OPTION_D + " TEXT," +
            KEY_CORRECT_OPTION + " INTEGER" +
            ")";

    private static final String CREATE_TABLE_USER_PROGRESS = "CREATE TABLE " + TABLE_USER_PROGRESS +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_USER_ID + " INTEGER," +
            KEY_TOPIC_ID + " INTEGER," +
            KEY_COMPLETED + " INTEGER," +
            KEY_SCORE + " INTEGER" +
            ")";

    private static final String CREATE_TABLE_USER_PROFILES = "CREATE TABLE " + TABLE_USER_PROFILES +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_USERNAME + " TEXT," +
            KEY_EMAIL + " TEXT UNIQUE," +
            KEY_PASSWORD_HASH + " TEXT," +
            KEY_INTERESTS + " TEXT" +
            ")";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating required tables
        db.execSQL(CREATE_TABLE_TOPICS);
        db.execSQL(CREATE_TABLE_STUDY_MATERIALS);
        db.execSQL(CREATE_TABLE_ASSESSMENT_QUESTIONS);
        db.execSQL(CREATE_TABLE_USER_PROGRESS);
        db.execSQL(CREATE_TABLE_USER_PROFILES);
        
        // Insert initial data
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOPICS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDY_MATERIALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENT_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROFILES);

        // Create tables again
        onCreate(db);
    }

    // Helper method to insert initial data into the database
    private void insertInitialData(SQLiteDatabase db) {
        // Insert initial topics
        insertInitialTopics(db);
        
        // Insert study materials
        insertInitialStudyMaterials(db);
        
        // Insert assessment questions
        insertInitialAssessmentQuestions(db);
    }
    
    // Insert initial topics
    private void insertInitialTopics(SQLiteDatabase db) {
        // Sample topics from the HomeFragment
        ContentValues values = new ContentValues();
        
        // Topic 1
        values.put(KEY_ID, 1);
        values.put(KEY_TITLE, "Introduction to Mobile Development");
        values.put(KEY_DESCRIPTION, "Learn the basics of mobile app development");
        values.put(KEY_IMAGE_RES_ID, 0); // Will be replaced with actual resource ID
        db.insert(TABLE_TOPICS, null, values);
        
        // Topic 2
        values.clear();
        values.put(KEY_ID, 2);
        values.put(KEY_TITLE, "UI Components");
        values.put(KEY_DESCRIPTION, "Explore various UI components in Android");
        values.put(KEY_IMAGE_RES_ID, 0);
        db.insert(TABLE_TOPICS, null, values);
        
        // Topic 3
        values.clear();
        values.put(KEY_ID, 3);
        values.put(KEY_TITLE, "User Authentication");
        values.put(KEY_DESCRIPTION, "Implement secure user authentication");
        values.put(KEY_IMAGE_RES_ID, 0);
        db.insert(TABLE_TOPICS, null, values);
        
        // Topic 4
        values.clear();
        values.put(KEY_ID, 4);
        values.put(KEY_TITLE, "Data Storage");
        values.put(KEY_DESCRIPTION, "Discover options for storing data in Android apps");
        values.put(KEY_IMAGE_RES_ID, 0);
        db.insert(TABLE_TOPICS, null, values);
        
        // Topic 5
        values.clear();
        values.put(KEY_ID, 5);
        values.put(KEY_TITLE, "API Integration");
        values.put(KEY_DESCRIPTION, "Connect your app with external APIs");
        values.put(KEY_IMAGE_RES_ID, 0);
        db.insert(TABLE_TOPICS, null, values);
    }
    
    // Insert initial study materials
    private void insertInitialStudyMaterials(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        
        // Study material for Topic 1
        values.put(KEY_TOPIC_ID, 1);
        values.put(KEY_CONTENT, "Mobile development is the process of creating applications that run on mobile devices.\n\n" +
                "Key concepts in mobile development:\n" +
                "• Native vs hybrid vs web apps\n" +
                "• Platform-specific considerations\n" +
                "• User interface design\n" +
                "• Performance optimization\n" +
                "• Device feature utilization\n\n" +
                "Android is an open-source mobile operating system developed by Google. " +
                "Android applications are primarily written in Java or Kotlin and use XML for layouts.");
        db.insert(TABLE_STUDY_MATERIALS, null, values);
        
        // Study material for Topic 2
        values.clear();
        values.put(KEY_TOPIC_ID, 2);
        values.put(KEY_CONTENT, "Android UI Components are the building blocks for creating user interfaces.\n\n" +
                "Essential UI Components include:\n" +
                "• TextView: Displays text to the user\n" +
                "• EditText: Allows user input\n" +
                "• Button: Triggers actions when clicked\n" +
                "• ImageView: Displays images\n" +
                "• RecyclerView: Efficiently displays scrollable lists\n" +
                "• CardView: Material design container with shadow\n\n" +
                "Layouts are used to arrange UI components on the screen, including LinearLayout, RelativeLayout, ConstraintLayout, etc.");
        db.insert(TABLE_STUDY_MATERIALS, null, values);
        
        // Study material for Topic 3
        values.clear();
        values.put(KEY_TOPIC_ID, 3);
        values.put(KEY_CONTENT, "User authentication is the process of verifying a user's identity.\n\n" +
                "Common authentication methods:\n" +
                "• Username/password\n" +
                "• OAuth 2.0\n" +
                "• Firebase Authentication\n" +
                "• Biometric authentication\n\n" +
                "Best practices for authentication:\n" +
                "• Never store passwords in plain text\n" +
                "• Use secure tokens for sessions\n" +
                "• Implement proper error handling\n" +
                "• Provide account recovery options");
        db.insert(TABLE_STUDY_MATERIALS, null, values);
        
        // Study material for Topic 4
        values.clear();
        values.put(KEY_TOPIC_ID, 4);
        values.put(KEY_CONTENT, "Android provides several options for storing data locally on the device.\n\n" +
                "Data storage options:\n" +
                "• Shared Preferences: Store primitive data in key-value pairs\n" +
                "• Internal Storage: Store private data on the device's filesystem\n" +
                "• External Storage: Store public data on shared external storage\n" +
                "• SQLite Database: Store structured data in a local database\n" +
                "• Room: A persistence library providing an abstraction layer over SQLite\n\n" +
                "The choice of storage method depends on the app's specific requirements.");
        db.insert(TABLE_STUDY_MATERIALS, null, values);
        
        // Study material for Topic 5
        values.clear();
        values.put(KEY_TOPIC_ID, 5);
        values.put(KEY_CONTENT, "API Integration allows your app to communicate with external services.\n\n" +
                "Key concepts in API Integration:\n" +
                "• RESTful APIs: HTTP-based communication with standard methods\n" +
                "• JSON/XML parsing: Converting structured data to objects\n" +
                "• Authentication: Securing API requests\n" +
                "• Error handling: Managing network issues and API errors\n\n" +
                "Common libraries for API integration in Android:\n" +
                "• Retrofit: Type-safe HTTP client\n" +
                "• OkHttp: HTTP client for efficient requests\n" +
                "• Volley: Google's HTTP library\n" +
                "• Gson/Jackson: JSON parsing libraries");
        db.insert(TABLE_STUDY_MATERIALS, null, values);
    }
    
    // Insert initial assessment questions
    private void insertInitialAssessmentQuestions(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        
        // Questions for Topic 1
        values.put(KEY_TOPIC_ID, 1);
        values.put(KEY_QUESTION_TEXT, "What is the primary programming language for Android development?");
        values.put(KEY_OPTION_A, "Swift");
        values.put(KEY_OPTION_B, "Java/Kotlin");
        values.put(KEY_OPTION_C, "C#");
        values.put(KEY_OPTION_D, "JavaScript");
        values.put(KEY_CORRECT_OPTION, 1); // B is correct (index 1)
        db.insert(TABLE_ASSESSMENT_QUESTIONS, null, values);
        
        values.clear();
        values.put(KEY_TOPIC_ID, 1);
        values.put(KEY_QUESTION_TEXT, "Which of the following is NOT a mobile app development approach?");
        values.put(KEY_OPTION_A, "Native");
        values.put(KEY_OPTION_B, "Hybrid");
        values.put(KEY_OPTION_C, "Web");
        values.put(KEY_OPTION_D, "Sequential");
        values.put(KEY_CORRECT_OPTION, 3); // D is correct (index 3)
        db.insert(TABLE_ASSESSMENT_QUESTIONS, null, values);
        
        values.clear();
        values.put(KEY_TOPIC_ID, 1);
        values.put(KEY_QUESTION_TEXT, "What file format is used for Android layouts?");
        values.put(KEY_OPTION_A, "JSON");
        values.put(KEY_OPTION_B, "XML");
        values.put(KEY_OPTION_C, "HTML");
        values.put(KEY_OPTION_D, "CSS");
        values.put(KEY_CORRECT_OPTION, 1); // B is correct (index 1)
        db.insert(TABLE_ASSESSMENT_QUESTIONS, null, values);
        
        // Questions for Topic 2
        values.clear();
        values.put(KEY_TOPIC_ID, 2);
        values.put(KEY_QUESTION_TEXT, "Which component is used to display scrollable lists in Android?");
        values.put(KEY_OPTION_A, "TextView");
        values.put(KEY_OPTION_B, "ScrollView");
        values.put(KEY_OPTION_C, "RecyclerView");
        values.put(KEY_OPTION_D, "ListView");
        values.put(KEY_CORRECT_OPTION, 2); // C is correct (index 2)
        db.insert(TABLE_ASSESSMENT_QUESTIONS, null, values);
        
        values.clear();
        values.put(KEY_TOPIC_ID, 2);
        values.put(KEY_QUESTION_TEXT, "Which layout positions elements relative to each other?");
        values.put(KEY_OPTION_A, "LinearLayout");
        values.put(KEY_OPTION_B, "RelativeLayout");
        values.put(KEY_OPTION_C, "ConstraintLayout");
        values.put(KEY_OPTION_D, "FrameLayout");
        values.put(KEY_CORRECT_OPTION, 1); // B is correct (index 1)
        db.insert(TABLE_ASSESSMENT_QUESTIONS, null, values);
        
        values.clear();
        values.put(KEY_TOPIC_ID, 2);
        values.put(KEY_QUESTION_TEXT, "What is the purpose of a CardView?");
        values.put(KEY_OPTION_A, "To display images");
        values.put(KEY_OPTION_B, "To show notification cards");
        values.put(KEY_OPTION_C, "To create material design containers with shadows");
        values.put(KEY_OPTION_D, "To render video content");
        values.put(KEY_CORRECT_OPTION, 2); // C is correct (index 2)
        db.insert(TABLE_ASSESSMENT_QUESTIONS, null, values);
        
        // Add more questions for other topics as needed
    }

    /*
     * CRUD Operations for each table
     */
    
    // Topics operations
    public List<Topic> getAllTopics() {
        List<Topic> topics = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TOPICS;
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                Topic topic = new Topic();
                topic.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                topic.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                topic.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                topic.setImageResId(cursor.getInt(cursor.getColumnIndex(KEY_IMAGE_RES_ID)));
                
                topics.add(topic);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return topics;
    }
    
    public Topic getTopic(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_TOPICS, 
                new String[] { KEY_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_IMAGE_RES_ID },
                KEY_ID + "=?",
                new String[] { String.valueOf(id) },
                null, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            Topic topic = new Topic();
            topic.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            topic.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
            topic.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
            topic.setImageResId(cursor.getInt(cursor.getColumnIndex(KEY_IMAGE_RES_ID)));
            
            cursor.close();
            return topic;
        }
        
        if (cursor != null) {
            cursor.close();
        }
        
        return null;
    }
    
    // Study Materials operations
    public String getStudyContent(int topicId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_STUDY_MATERIALS,
                new String[] { KEY_CONTENT },
                KEY_TOPIC_ID + "=?",
                new String[] { String.valueOf(topicId) },
                null, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            String content = cursor.getString(cursor.getColumnIndex(KEY_CONTENT));
            cursor.close();
            return content;
        }
        
        if (cursor != null) {
            cursor.close();
        }
        
        return "No content available for this topic.";
    }
    
    // Assessment Questions operations
    public List<AssessmentQuestion> getQuestionsForTopic(int topicId) {
        List<AssessmentQuestion> questions = new ArrayList<>();
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ASSESSMENT_QUESTIONS,
                null,
                KEY_TOPIC_ID + "=?",
                new String[] { String.valueOf(topicId) },
                null, null, null);
        
        if (cursor.moveToFirst()) {
            do {
                AssessmentQuestion question = new AssessmentQuestion();
                question.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                question.setTopicId(cursor.getInt(cursor.getColumnIndex(KEY_TOPIC_ID)));
                question.setQuestionText(cursor.getString(cursor.getColumnIndex(KEY_QUESTION_TEXT)));
                
                String[] options = new String[4];
                options[0] = cursor.getString(cursor.getColumnIndex(KEY_OPTION_A));
                options[1] = cursor.getString(cursor.getColumnIndex(KEY_OPTION_B));
                options[2] = cursor.getString(cursor.getColumnIndex(KEY_OPTION_C));
                options[3] = cursor.getString(cursor.getColumnIndex(KEY_OPTION_D));
                question.setOptions(options);
                
                question.setCorrectAnswerIndex(cursor.getInt(cursor.getColumnIndex(KEY_CORRECT_OPTION)));
                
                questions.add(question);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return questions;
    }
    
    // User Profile operations
    public long createUserProfile(String username, String email, String passwordHash, String interests) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PASSWORD_HASH, passwordHash);
        values.put(KEY_INTERESTS, interests);
        
        return db.insert(TABLE_USER_PROFILES, null, values);
    }
    
    public UserProfile getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_USER_PROFILES,
                null,
                KEY_EMAIL + "=?",
                new String[] { email },
                null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            UserProfile userProfile = new UserProfile();
            userProfile.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            userProfile.setUsername(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
            userProfile.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            userProfile.setPasswordHash(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD_HASH)));
            userProfile.setInterests(cursor.getString(cursor.getColumnIndex(KEY_INTERESTS)));
            
            cursor.close();
            return userProfile;
        }
        
        if (cursor != null) {
            cursor.close();
        }
        
        return null;
    }
    
    // User Progress operations
    public void markTopicCompleted(int userId, int topicId, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userId);
        values.put(KEY_TOPIC_ID, topicId);
        values.put(KEY_COMPLETED, 1); // 1 for completed
        values.put(KEY_SCORE, score);
        
        // Check if record exists
        Cursor cursor = db.query(TABLE_USER_PROGRESS,
                null,
                KEY_USER_ID + "=? AND " + KEY_TOPIC_ID + "=?",
                new String[] { String.valueOf(userId), String.valueOf(topicId) },
                null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            // Update existing record
            db.update(TABLE_USER_PROGRESS, values,
                    KEY_USER_ID + "=? AND " + KEY_TOPIC_ID + "=?",
                    new String[] { String.valueOf(userId), String.valueOf(topicId) });
        } else {
            // Insert new record
            db.insert(TABLE_USER_PROGRESS, null, values);
        }
        
        if (cursor != null) {
            cursor.close();
        }
    }
    
    public boolean isTopicCompleted(int userId, int topicId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_USER_PROGRESS,
                new String[] { KEY_COMPLETED },
                KEY_USER_ID + "=? AND " + KEY_TOPIC_ID + "=?",
                new String[] { String.valueOf(userId), String.valueOf(topicId) },
                null, null, null);
        
        boolean completed = false;
        if (cursor != null && cursor.moveToFirst()) {
            completed = cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED)) == 1;
        }
        
        if (cursor != null) {
            cursor.close();
        }
        
        return completed;
    }
    
    public int getAssessmentScore(int userId, int topicId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_USER_PROGRESS,
                new String[] { KEY_SCORE },
                KEY_USER_ID + "=? AND " + KEY_TOPIC_ID + "=?",
                new String[] { String.valueOf(userId), String.valueOf(topicId) },
                null, null, null);
        
        int score = 0;
        if (cursor != null && cursor.moveToFirst()) {
            score = cursor.getInt(cursor.getColumnIndex(KEY_SCORE));
        }
        
        if (cursor != null) {
            cursor.close();
        }
        
        return score;
    }
    
    public int getCompletedTopicsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_USER_PROGRESS,
                new String[] { "COUNT(*)" },
                KEY_USER_ID + "=? AND " + KEY_COMPLETED + "=?",
                new String[] { String.valueOf(userId), String.valueOf(1) },
                null, null, null);
        
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        if (cursor != null) {
            cursor.close();
        }
        
        return count;
    }
    
    public float getOverallProgress(int userId, int totalTopics) {
        if (totalTopics == 0) return 0;
        return (float) getCompletedTopicsCount(userId) / totalTopics * 100;
    }
    
    /**
     * Deletes a user's profile and all associated data
     * 
     * @param email The email of the user to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteUserByEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;
        db.beginTransaction();
        
        try {
            // First, get the user ID
            Cursor cursor = db.query(TABLE_USER_PROFILES,
                    new String[] { KEY_ID },
                    KEY_EMAIL + "=?",
                    new String[] { email },
                    null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                int userId = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                
                // Delete user progress data
                db.delete(TABLE_USER_PROGRESS, KEY_USER_ID + "=?", 
                        new String[] { String.valueOf(userId) });
                
                // Delete user profile
                db.delete(TABLE_USER_PROFILES, KEY_ID + "=?", 
                        new String[] { String.valueOf(userId) });
                
                success = true;
            }
            
            if (cursor != null) {
                cursor.close();
            }
            
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            db.endTransaction();
        }
        
        return success;
    }
} 