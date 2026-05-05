# Aranyani

Aranyani is an advanced Android application tailored for plant enthusiasts, gardeners, and sustainability advocates. Built with modern Android development practices using Jetpack Compose and Kotlin, it empowers users with AI-driven plant identification, disease diagnosis, smart garden planning, and reliable care reminders to foster a greener lifestyle.

## 🌟 Key Features

*   **🌱 Plant Identification:** Instantly identify various species of plants using image recognition and view specialized care recommendations.
*   **🩺 Disease Detection & Cure:** Scan affected plants to identify diseases. Aranyani provides actionable cures and treatments to restore your plant's health.
*   **📐 Garden Planner:** A comprehensive tool to design and optimize your garden space. It includes photo markup, measurement inputs, and personalized plant recommendations based on your layout.
*   **⏰ Care Reminders:** Never forget to water your plants or turn your compost! Set up high-priority custom notifications to keep your garden thriving.
*   **🌍 Sustainable Growing:** Access built-in guides, tips, and sustainable practices to reduce waste and grow organically.
*   **🔐 Secure Authentication:** Seamless and secure user login and session management powered by Auth0.
*   **📜 Scan History:** Keep a persistent record of your past plant and disease scans to easily monitor the progress of your greenery.

## 🛠 Tech Stack

*   **Language:** Kotlin
*   **UI Toolkit:** Jetpack Compose & Material Design 3
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Navigation:** Jetpack Navigation Compose
*   **Authentication:** Auth0 for Android
*   **Networking:** Retrofit & OkHttp
*   **Image Loading:** Coil
*   **Asynchronous Tasks:** Kotlin Coroutines
*   **Background Processing:** Android WorkManager
*   **Permissions:** Accompanist Permissions

## 🚀 Getting Started

### Prerequisites

*   **Android Studio:** Latest stable version (Ladybug or newer recommended).
*   **JDK:** Version 11 or higher.
*   **Android SDK:** Minimum SDK 24, Target SDK 36.

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/Aranyani_AndroidApp.git
    ```
2.  **Open in Android Studio:**
    Navigate to the cloned directory and open the project in Android Studio.
3.  **Sync Gradle:**
    Let Android Studio sync the Gradle dependencies automatically.
4.  **Configure API Keys:**
    *   Set up your `Auth0` domain and scheme in `app/build.gradle.kts` (or verify the existing ones).
    *   If using external ML or Care APIs, ensure your base URLs are properly configured in the networking module.
5.  **Run the App:**
    Select an emulator or connect a physical Android device and click the **Run** button.

## 📂 Project Structure Overview

```text
app/src/main/java/com/example/aranyani3/
│
├── auth/                   # Auth0 logic and Session Management
├── models/                 # Data classes and payloads
├── network/                # Retrofit API interfaces and clients
├── notification/           # Reminder channels and WorkManager workers
├── repository/             # Data repositories abstracting network/local sources
├── screens/                # Jetpack Compose UI Screens (Home, Login, Identifier, etc.)
├── ui/                     # Theme, Typography, and shared UI components
├── utils/                  # Helper classes and utility functions
└── viewmodel/              # State management across the app features
```

## 📜 Permissions Required

*   **Camera:** Used for identifying plants, diagnosing diseases, and taking pictures for the Garden Planner.
*   **Notifications (Android 13+):** Used for delivering timely plant and compost reminders.
*   **Battery Optimization (Optional):** Ensure reminders trigger precisely on time by exempting the app from battery-saving modes.

