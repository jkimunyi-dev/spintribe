# Chuka Connect

Chuka Connect is a modern Android application designed to enhance campus life by providing a centralized platform for event management and community engagement at Chuka University.

## 🌟 Features

### Event Management
- Browse and discover campus events
- Filter events by category and status (Upcoming/Past)
- Detailed event information including location, time, and descriptions
- Featured events carousel
- QR code generation for event tickets

### User Experience
- Intuitive onboarding process
- Seamless authentication (Firebase)
- Personalized user profiles
- Real-time notifications for event updates
- Bottom navigation for easy access to key features

## 🚀 Installation

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17 or later
- Android SDK with minimum API level 24 (Android 7.0)
- Gradle 8.7 or later

### Setup Steps

1. Clone the repository:
```bash
git clone [repository-url]
```

2. Open the project in Android Studio

3. Configure Firebase:
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Add your Android app to Firebase project
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Authentication in Firebase Console

4. Build the project:
   - Click "Build > Make Project" in Android Studio
   - Or run `./gradlew build` from the terminal

5. Run the application:
   - Connect an Android device or use an emulator
   - Click "Run > Run 'app'" in Android Studio

## 🏗 Architecture

### Tech Stack
- **UI Framework**: Jetpack Compose
- **Authentication**: Firebase Auth
- **Navigation**: Jetpack Navigation Compose
- **State Management**: Kotlin Flow & StateFlow
- **Dependency Injection**: Manual DI
- **QR Code Generation**: ZXing
- **Data Persistence**: DataStore Preferences

### Key Components

#### Screens
- Onboarding
- Authentication (Sign In/Sign Up)
- Home
- Events
- Notifications
- Profile

#### Features
- Event filtering and categorization
- QR code generation for event tickets
- Push notifications
- User profile management

## 🎯 Use Cases

### Student Organizations
- Publish and manage events
- Track attendance
- Engage with participants

### Academic Departments
- Announce seminars and workshops
- Share academic events
- Coordinate department activities

### Campus Administration
- Broadcast important announcements
- Manage institution-wide events
- Track event participation

### Individual Students
- Discover campus events
- Register for activities
- Receive important updates
- Network with peers

## 🔒 Security

- Firebase Authentication for secure user management
- API key protection
- Proguard rules for release builds
- Secure data storage using DataStore

## 📱 Compatibility

- Minimum SDK: Android 7.0 (API level 24)
- Target SDK: Android 14 (API level 34)
- Supports both light and dark themes
- Optimized for various screen sizes

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## 📞 Support

For support, please contact [support email/contact information]

## 🔄 Version History

- 1.0.0 (Initial Release)
  - Basic event management
  - User authentication
  - QR code generation
  - Push notifications

## 🙏 Acknowledgments

- Firebase for authentication services
- ZXing for QR code generation
- Jetpack Compose for modern UI development
- Material Design 3 for consistent styling