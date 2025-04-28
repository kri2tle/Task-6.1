# Personalized Learning Mobile Application

## Project Overview
This Android application provides a personalized learning experience for users, allowing them to access study materials, take assessments, and track their learning progress.

## Features
1. User Authentication
   - Sign Up
   - Sign In
   - Sign Out
   - Account Deletion

2. Learning Management
   - View learning courses
   - Track completion percentage
   - Access study materials
   - Take assessments

3. Progress Tracking
   - Overall progress tracking
   - Individual course progress
   - Assessment scores

## Technical Architecture

### Database Structure
The application uses SQLite database with the following tables:

1. `user_profiles`
   - id (INTEGER PRIMARY KEY)
   - username (TEXT)
   - email (TEXT UNIQUE)
   - password_hash (TEXT)
   - interests (TEXT)

2. `topics`
   - id (INTEGER PRIMARY KEY)
   - title (TEXT)
   - description (TEXT)
   - image_res_id (INTEGER)

3. `study_materials`
   - id (INTEGER PRIMARY KEY)
   - topic_id (INTEGER)
   - content (TEXT)

4. `assessment_questions`
   - id (INTEGER PRIMARY KEY)
   - topic_id (INTEGER)
   - question_text (TEXT)
   - option_a (TEXT)
   - option_b (TEXT)
   - option_c (TEXT)
   - option_d (TEXT)
   - correct_option (INTEGER)

5. `user_progress`
   - id (INTEGER PRIMARY KEY)
   - user_id (INTEGER)
   - topic_id (INTEGER)
   - completed (INTEGER)
   - score (INTEGER)

### Key Components

1. DatabaseHelper
   - Singleton class managing database operations
   - Handles CRUD operations for all tables
   - Manages database version and upgrades
   - Provides methods for user authentication and progress tracking

2. ProfileManager
   - Manages user session data
   - Handles user profile information
   - Provides methods for session management

3. LearningProgressManager
   - Tracks learning progress
   - Manages completion status
   - Calculates progress percentages

## API Documentation

### User Authentication

1. Sign Up
```java
// Create new user profile
long userId = dbHelper.createUserProfile(username, email, passwordHash, interests);
```
Response:
- Returns user ID if successful
- Throws exception if email already exists

2. Sign In
```java
// Get user by email
UserProfile userProfile = dbHelper.getUserByEmail(email);
```
Response:
- Returns UserProfile object if found
- Returns null if user doesn't exist

3. Sign Out
```java
// Clear session data
profileManager.clearSession();
profileManager.setProfileCreated(false);
```
Response:
- No return value
- Clears current session data

4. Delete Account
```java
// Delete user and associated data
boolean success = dbHelper.deleteUserByEmail(email);
```
Response:
- Returns true if deletion successful
- Returns false if deletion fails

### Learning Progress

1. Get Course Progress
```java
// Get overall progress
float progress = dbHelper.getOverallProgress(userId, totalTopics);
```
Response:
- Returns progress percentage (0-100)

2. Mark Topic Completed
```java
// Mark topic as completed with score
dbHelper.markTopicCompleted(userId, topicId, score);
```
Response:
- No return value
- Updates or creates progress record

3. Check Topic Completion
```java
// Check if topic is completed
boolean completed = dbHelper.isTopicCompleted(userId, topicId);
```
Response:
- Returns true if topic is completed
- Returns false if topic is not completed

### Study Materials

1. Get Study Content
```java
// Get content for topic
String content = dbHelper.getStudyContent(topicId);
```
Response:
- Returns study material content as String
- Returns default message if no content found

2. Get Assessment Questions
```java
// Get questions for topic
List<AssessmentQuestion> questions = dbHelper.getQuestionsForTopic(topicId);
```
Response:
- Returns list of AssessmentQuestion objects
- Returns empty list if no questions found

## Error Handling

The application implements comprehensive error handling:

1. Database Operations
   - All database operations are wrapped in try-catch blocks
   - Errors are logged and displayed to users
   - Transaction management for critical operations

2. User Input Validation
   - Email format validation
   - Password strength requirements
   - Required field validation

3. Session Management
   - Checks for valid session before operations
   - Handles session expiration
   - Manages navigation based on authentication state

## Security Features

1. Password Security
   - Passwords are hashed using SHA-256
   - No plain text password storage
   - Password confirmation during sign up

2. Data Protection
   - User data is stored securely in SQLite
   - Session data is managed through SharedPreferences
   - Critical operations require authentication

## Navigation Flow

1. Authentication Flow
   - App starts at sign-in screen
   - New users can navigate to sign-up
   - Successful authentication leads to home screen

2. Learning Flow
   - Users can view available courses
   - Select topics to study
   - Take assessments
   - Track progress

3. Profile Management
   - Users can view and edit profile
   - Sign out option available
   - Account deletion with confirmation

## Dependencies

The project uses the following key dependencies:
- AndroidX Navigation Component
- Material Design Components
- SQLite Database
- SharedPreferences for session management

## Setup and Installation

1. Clone the repository
2. Open project in Android Studio
3. Sync Gradle files
4. Run the application

## Testing

The application can be tested using:
1. Unit tests for database operations
2. Integration tests for user flows
3. UI tests for navigation and user interface

## Future Improvements

1. Enhanced Security
   - Implement biometric authentication
   - Add two-factor authentication
   - Improve password hashing

2. Feature Additions
   - Offline mode support
   - Progress analytics
   - Social learning features

3. Performance Optimization
   - Implement caching
   - Optimize database queries
   - Improve UI responsiveness 