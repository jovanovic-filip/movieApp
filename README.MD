# Movie App

A modern Android application that displays movie information using the TMDB (The Movie Database) API.

## Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Networking:** Retrofit, OkHttp
- **Build System:** Gradle (Kotlin DSL)
- **Testing:** JUnit, MockK, Turbine

## TODO

- ** [ ] Add more unit tests
- ** [ ] Add more UI tests
- ** [ ] Add pagination support

### Prerequisites

- Android Studio Ladybug (2024.2.2) or newer
- JDK 17 or higher
- Android SDK with minimum API level support

### API Key Setup

This project uses TMDB API which requires an API key. For security reasons, the API key is not committed to the repository.

1. Create a file named `apikey.properties` in the project root directory:
```properties
TMDB_API_KEY=your_api_key_here