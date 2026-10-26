# BlogApp 📱

A modern, full-featured native Android blogging platform built with **Kotlin**, **Android Jetpack**, and **Firebase**. BlogApp allows users to explore trending articles, publish their own blogs with cover images, interact via comments, bookmark articles, and explore user profiles.

---

## 🌟 Key Features

- **🔐 Authentication & Security**: Complete user registration and sign-in powered by Firebase Authentication and Firebase App Check.
- **📰 Dynamic Feed**: Real-time stream of articles with rich metadata (author name, timestamp, category, and cover image).
- **✍️ Create & Publish**: Easy-to-use editor for drafting and publishing articles with image uploads to Firebase Storage.
- **🛠️ Edit & Manage**: Authors have full control to edit or update their published articles anytime.
- **💬 Real-Time Comments**: Interactive comment bottom sheet enabling readers to engage with content seamlessly.
- **🔖 Saved Articles**: Bookmark favorite articles for quick access anytime from the saved articles section.
- **👤 User Profiles & Community**: Browse community members, view author profiles, and explore their authored stories.
- **🎨 Modern Material UI**: Elegant interface featuring custom typography, responsive card layouts, and smooth animations.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Architecture**: Android Jetpack & ViewBinding
- **Backend Services**:
  - **Firebase Authentication**: Email & password authentication
  - **Firebase Realtime Database**: Real-time posts, comments, and user state sync
  - **Firebase Storage**: Cloud storage for article cover images and profile pictures
  - **Firebase App Check**: Play Integrity & SafetyNet app verification
- **Image Loading**: [Glide](https://github.com/bumptech/glide) for efficient image caching and loading
- **Design**: Material Components for Android (MDC) with custom Google Fonts

---

## 📁 Project Structure

```
BlogApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/blogapp/
│   │   │   ├── Model/                # Data models (BlogItemModel, User, Comment, UserData)
│   │   │   ├── adapter/              # RecyclerView adapters (BlogAdapter, ArticleAdapter, etc.)
│   │   │   ├── register/             # Onboarding & Welcome screens
│   │   │   ├── MainActivity.kt       # Home feed & navigation
│   │   │   ├── AddArticleActivity.kt # New article publishing
│   │   │   ├── ArticleActivity.kt    # User articles management
│   │   │   ├── EditBlogActivity.kt   # Article editor
│   │   │   ├── ProfileActivity.kt    # User profile view
│   │   │   ├── ReadMoreActivity.kt   # Article reader view
│   │   │   ├── SavedArticlesActivity.kt # Bookmarks
│   │   │   └── SplashActivity.kt     # Splash screen
│   │   ├── res/                      # Drawables, layouts, fonts, themes, and values
│   │   └── AndroidManifest.xml       # App manifest
│   └── build.gradle.kts              # App-level build configurations & dependencies
├── gradle/                           # Gradle wrapper & version catalogs
├── build.gradle.kts                  # Root build configuration
├── settings.gradle.kts               # Project settings & repositories
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Iguana (2023.2.1) or newer
- **JDK**: Java 17 or Java 21
- **Android SDK**: Min SDK 24, Target SDK 34

### Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Yuvraj9198/BlogApp.git
   cd BlogApp
   ```

2. **Configure Firebase**:
   - Create a project on the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android App with package name `com.example.blogapp`.
   - Enable **Firebase Authentication**, **Realtime Database**, and **Storage**.
   - Download `google-services.json` and place it in the `app/` folder.

3. **Build and Run**:
   ```bash
   ./gradlew assembleDebug
   ```
   Or open the project in **Android Studio** and click **Run**.

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
