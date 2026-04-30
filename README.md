# FitLife

A native Android fitness tracking application built with **Jetpack Compose** and **Clean Architecture**. FitLife helps users track daily nutrition, log workouts, and monitor progress toward personalized fitness goals, all powered by real food and exercise APIs.

---

## Screenshots
![fitlife](Screenshots/fitlife%20(1).jpeg)

![fitlife](Screenshots/fitlife%20(2).jpeg)
---

## Features

###  User Profile & Onboarding
- First-launch setup screen collects name, age, gender, height, weight, activity level, and goal
- **BMR** calculated via the **Mifflin-St Jeor** equation
- **TDEE** derived from BMR × activity multiplier (1.2 – 1.9)
- Calorie target adjusted per goal: −500 kcal (lose), +300 kcal (gain), maintenance
- Macro split automatically calculated: **30% protein / 40% carbs / 30% fat**

###  Meal Logging
- Create meals by type: **Breakfast, Lunch, Dinner, Snack, Pre-Workout, Post-Workout**
- Search foods by name via **USDA FoodData Central API** (1,000 req/hr, no rate-limit issues)
- Scan product barcodes via **OpenFoodFacts API**
- Adjust quantity per food item : macros scale live in the UI
- Add multiple foods to a single meal before saving
- Delete individual food entries from a meal
- Daily macro summary auto-recalculates on every change

### Home Dashboard
- Animated **calorie ring** showing daily progress vs. target
- **Macro progress bars** (Protein / Carbs / Fat) with live fill animation
- Today's meals grouped by type with per-meal calorie totals
- Recent workout sessions
- Quick-action buttons: Log Meal, Log Workout

### Workout Logging
- Select target **muscle group** to start a session
- Search exercises from your **local library** (offline)
- Discover new exercises via **API Ninjas Exercises API** (search by name or muscle)
- Save API exercises to your local library (deduplication by name)
- Log **sets × reps × weight** per exercise with optional notes
- Review and remove exercises mid-session
- Session saved to Room DB on completion

### Meal History
- Browse past meals by date
- View per-meal food breakdown and macro totals

---

## Architecture

FitLife follows **Clean Architecture** with a unidirectional data flow:

```
UI (Compose Screens)
        ↓ observes
   ViewModels (LiveData)
        ↓ calls
   Repositories
        ↓ reads/writes
   Room DAOs  ←→  Retrofit APIs
```

### Layer breakdown

| Layer | Contents |
|---|---|
| `ui/screens` | Composable screens: Home, Setup, MealLogging, WorkoutLogging, MealHistory |
| `ui/components` | Reusable composables: CalorieRing, MacroProgressRow, FoodResultCard, ExerciseCard… |
| `viewModel` | `UserViewModel`, `MealViewModel`, `FoodViewModel`, `WorkoutViewModel`, `ExerciseViewModel` |
| `data/repository` | `UserRepository`, `MealRepository`, `FoodRepository`, `WorkoutRepository`, `ExerciseRepository` |
| `data/database` | Room DAOs: `UserDao`, `MealDao`, `FoodDao`, `WorkoutDao`, `ExerciseDao` |
| `data/api` | Retrofit interfaces: `OpenFoodAPI`, `UsdaFoodAPI`, `NinjasAPI` |
| `data/dto` | API response DTOs with Gson `@SerializedName` mappings |
| `domain/model` | Room entities: `User`, `Meal`, `FoodEntry`, `Exercise`, `WorkoutSession`, `WorkoutExercise`, `DailySummary`, `MealWithFood` |
| `utils` | Mappers (`toFoodEntry`, `toExercise`), `todayAsString()` |

---

## Tech Stack

| Category | Library / Tool |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| Local DB | Room (KSP annotation processor) |
| Networking | Retrofit 2 + OkHttp 3 |
| JSON parsing | Gson |
| Async | Kotlin Coroutines + `viewModelScope` |
| State | `LiveData` + `observeAsState()` |
| DI | Manual (constructor injection) |
| Min SDK | 24 (API 24+) |
| Target SDK | 36 |

---

## External APIs

| API | Used for | Auth | Rate limit |
|---|---|---|---|
| [USDA FoodData Central](https://fdc.nal.usda.gov/) | Food search by name | Free API key | 1,000 req/hr |
| [OpenFoodFacts](https://world.openfoodfacts.org/) | Barcode product lookup | None | Soft limit |
| [API Ninjas Exercises](https://api-ninjas.com/api/exercises) | Exercise search by name/muscle | `X-Api-Key` header | Per plan |

---

## Project Structure

```
app/
└── src/main/
    ├── java/com/fitlife/app/
    │   ├── MainActivity.kt
    │   ├── data/
    │   │   ├── api/
    │   │   │   ├── OpenFoodAPI.kt
    │   │   │   ├── UsdaFoodAPI.kt
    │   │   │   └── NinjasAPI.kt
    │   │   ├── database/
    │   │   │   ├── AppDatabase.kt
    │   │   │   ├── UserDao.kt
    │   │   │   ├── MealDao.kt
    │   │   │   ├── FoodDao.kt
    │   │   │   ├── WorkoutDao.kt
    │   │   │   └── ExerciseDao.kt
    │   │   ├── dto/
    │   │   │   ├── ProductDto.kt
    │   │   │   ├── ProductResponseDto.kt
    │   │   │   ├── NutrimentsDto.kt
    │   │   │   ├── UsdaFoodDto.kt
    │   │   │   ├── UsdaSearchResponseDto.kt
    │   │   │   ├── UsdaNutrientDto.kt
    │   │   │   └── ExerciseDto.kt
    │   │   └── repository/
    │   │       ├── UserRepository.kt
    │   │       ├── MealRepository.kt
    │   │       ├── FoodRepository.kt
    │   │       ├── WorkoutRepository.kt
    │   │       └── ExerciseRepository.kt
    │   ├── domain/model/
    │   │   ├── User.kt
    │   │   ├── Meal.kt
    │   │   ├── MealWithFood.kt
    │   │   ├── FoodEntry.kt
    │   │   ├── DailySummary.kt
    │   │   ├── Exercise.kt
    │   │   ├── WorkoutSession.kt
    │   │   └── WorkoutExercise.kt
    │   ├── ui/
    │   │   ├── screens/
    │   │   │   ├── HomeScreen.kt
    │   │   │   ├── SetupScreen.kt
    │   │   │   ├── MealLoggingScreen.kt
    │   │   │   ├── FoodSearchScreen.kt
    │   │   │   ├── MealHistoryScreen.kt
    │   │   │   └── WorkoutLoggingScreen.kt
    │   │   └── components/
    │   │       ├── CalorieSummaryCard.kt
    │   │       ├── MacroProgressCard.kt
    │   │       ├── FoodResultCard.kt
    │   │       ├── ExerciseCard.kt
    │   │       └── ...
    │   ├── utils/
    │   │   ├── Mapper.kt
    │   │   └── DateUtils.kt
    │   └── viewModel/
    │       ├── UserViewModel.kt
    │       ├── MealViewModel.kt
    │       ├── FoodViewModel.kt
    │       ├── WorkoutViewModel.kt
    │       └── ExerciseViewModel.kt
    └── res/
```

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 11+
- Android device or emulator (API 24+)

### Setup

**1. Clone the repository**
```bash
git clone https://github.com/yourusername/fitlife.git
cd fitlife
```

**2. Add your API keys**

Open `MainActivity.kt` and replace the placeholder values:
```kotlin
val USDA_API_KEY   = "USDA KEY"
val NINJAS_API_KEY = "NINJAS KEY"
```

Get your free keys here:
- USDA: https://fdc.nal.usda.gov/api-key-signup.html
- API Ninjas: https://api-ninjas.com/register

**3. Add internet permission**

Ensure `AndroidManifest.xml` contains:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```


## Key Dependencies 

```kotlin
// Jetpack Compose BOM
implementation(platform(libs.androidx.compose.bom))
implementation(libs.androidx.compose.material3)
implementation(libs.androidx.compose.runtime.livedata)

// Room
implementation("androidx.room:room-runtime:2.8.4")
implementation("androidx.room:room-ktx:2.8.4")
ksp("androidx.room:room-compiler:2.8.4")

// Retrofit + OkHttp
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

// Icons
implementation("androidx.compose.material:material-icons-extended")
```

---

## Data Flow : Meal Logging

```
User taps "Log Meal"
        ↓
MealLoggingScreen opens
        ↓
Step 1: MealTypeSelector (Breakfast / Lunch / Dinner / Snack / Pre-Workout / Post-Workout)
        ↓
MealViewModel.getOrCreateMeal(type, date)
→ checks Room for existing meal of that type today
→ creates new Meal row if none found
→ posts real mealId to activeMealId LiveData
        ↓
Step 2: Food Search
→ Name search  → USDA FoodData Central API
→ Barcode scan → OpenFoodFacts API
→ User adjusts quantity (macros scale live)
→ Taps "Log" → FoodEntry saved with correct mealId FK
→ MealViewModel.updateDailyMacros(date) recalculates DailySummary
        ↓
User taps "Done" → HomeScreen refreshes with updated macros
```

## Data Flow : Workout Logging

```
User taps "Log Workout"
        ↓
WorkoutLoggingScreen opens
        ↓
Step 1: MuscleGroupSelector
→ WorkoutViewModel.startSession(muscleGroup, date)
→ WorkoutSession saved to Room, real id posted to activeSession
        ↓
Step 2: Exercise Search
→ "My Library" tab  → ExerciseDao local search (offline)
→ "Discover" tab    → API Ninjas search by name or muscle
→ Save to library   → ExerciseRepository.saveExercise() (deduplicates by name)
        ↓
Step 3: Log Sets
→ User enters sets / reps / weight / notes
→ WorkoutViewModel.logExercise() → WorkoutExercise saved to Room
→ Session summary updates live
        ↓
User taps "Done" → WorkoutViewModel.finishSession() → history refreshes
```

---

## Macro Calculation Reference

| Step | Formula |
|---|---|
| BMR (Male) | `(10 × weight) + (6.25 × height) − (5 × age) + 5` |
| BMR (Female) | `(10 × weight) + (6.25 × height) − (5 × age) − 161` |
| TDEE | `BMR × activity multiplier` |
| Calorie target (lose) | `TDEE − 500 kcal` |
| Calorie target (gain) | `TDEE + 300 kcal` |
| Protein | `(calories × 0.30) ÷ 4` g |
| Carbs | `(calories × 0.40) ÷ 4` g |
| Fat | `(calories × 0.30) ÷ 9` g |

Activity multipliers: Sedentary `1.2` · Lightly Active `1.375` · Moderately Active `1.55` · Very Active `1.725` · Extra Active `1.9`

---

## Roadmap

- [ ] Navigation component / Compose NavHost
- [ ] Move API keys to `local.properties` / BuildConfig
- [ ] Hilt dependency injection
- [ ] Weekly progress charts
- [ ] Water intake tracking
- [ ] Body weight log
- [ ] Push notification reminders
- [ ] Dark / light theme toggle
- [ ] Export data as CSV
- [ ] Unit tests for ViewModels and Repositories

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m 'Add your feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

## License

```
MIT License

Copyright (c) 2025 FitLife

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

> Built & Developped by Youssef Aitbouddroub
