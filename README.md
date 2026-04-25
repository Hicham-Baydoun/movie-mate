# 🎬 MovieMate — Android Application

> A native Android movie tracking app built with Java. Users can browse movies, mark them as watched, rate them, view their watch history, and track personal viewing statistics.

---

## 👥 Group Members

| # | Name | Student ID | Responsibility |
|---|------|-----------|----------------|
| 1 | **Karim Tello** | 202205126 | Foundation & Authentication |
| 2 | **Mohamad Al Masri** | 202203314 | Core Screens & Data Models |
| 3 | **Hicham Baydoun** | 202202338 | Adapters, Network & Reference |

---

## 📋 Project Overview

**MovieMate** is a native Android application that allows users to:
- Register and log in to a personal account
- Browse a full movie catalog displayed in a 2-column grid
- Mark movies as watched with one tap
- Rate watched movies on a scale of 1–10
- View complete watch history with the ability to delete entries
- Track personal statistics: total watch time, movies watched, and a viewer rank

**Backend:** PHP REST API running on a local XAMPP server (`http://10.0.2.2/MovieMateAPI/`)

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java |
| Platform | Android (MinSDK 24 / TargetSDK 36) |
| Networking | Volley |
| Image Loading | Glide |
| Backend | PHP + MySQL (XAMPP) |
| Build System | Gradle (Kotlin DSL) |

---

## 📁 Project Structure

```
app/src/main/java/com/example/moviemate/
├── SplashActivity.java
├── LoginActivity.java
├── SignUpActivity.java
├── MainActivity.java
├── HomeActivity.java
├── WatchHistoryListActivity.java
├── RateMovieActivity.java
├── ChangePasswordActivity.java
├── MovieItem.java
├── MovieHistoryItem.java
├── MovieAdapter.java
├── WatchHistoryAdapter.java
└── VolleySingleton.java
```

---

## 👤 Member 1 — Karim Tello `202205126`
**Responsibility: Foundation & Authentication**

Covers the project architecture, folder structure, and all authentication-related screens.

### Architecture
The app follows **MVC (Model-View-Controller)**:
- **Model** → `MovieItem.java`, `MovieHistoryItem.java`
- **View** → XML layout files in `res/layout/`
- **Controller** → All `*Activity.java` files

### Activities

#### `SplashActivity.java`
- Layout: `activity_splash.xml` — **RelativeLayout** root
- Shows the app logo for **5 seconds** using a `Handler` delay
- Calls `finish()` after redirecting to `LoginActivity` so the user cannot go back

#### `LoginActivity.java`
- Layout: `activity_login.xml` — **LinearLayout** root
- Validates username and password fields (non-empty check)
- HTTP POST to `login_user.php` via Volley
- On success: saves `username` and `user_id` to `SharedPreferences ("MovieMatePrefs")`, navigates to `MainActivity`
- On failure: shows error Toast from server response

#### `SignUpActivity.java`
- Layout: `activity_sign_up.xml` — **LinearLayout** root
- HTTP POST to `register_user.php`
- On success: shows Toast and navigates to `LoginActivity`
- Creates its own `RequestQueue` (inconsistent with rest of app — should use `VolleySingleton`)

#### `ChangePasswordActivity.java`
- Layout: `activity_change_password.xml` — **LinearLayout** root
- Launched from the profile popup menu; receives `username` via Intent extra
- HTTP POST to `change_password.php` with `username`, `old_password`, `new_password`
- Finishes the activity on success

---

## 👤 Member 2 — Mohamad Al Masri `202203314`
**Responsibility: Core Screens & Data Models**

Covers the three main tab screens, rating screen, and the Java data model classes.

### Activities

#### `MainActivity.java`
- Layout: `activity_main.xml` — **RelativeLayout** root (LinearLayouts inside for toolbar)
- **Movies tab** — displays all movies in a 2-column `RecyclerView` grid (`GridLayoutManager`)
- HTTP GET to `get_movies.php`, builds `List<MovieItem>`, notifies `MovieAdapter`
- Profile popup menu: Change Password / Logout (clears `SharedPreferences` + wipes back stack)

#### `HomeActivity.java`
- Layout: `activity_home.xml` — **RelativeLayout** root (multiple nested LinearLayouts for stat cards)
- **Home tab** — personal stats dashboard
- HTTP GET to `get_user_history.php` → computes total minutes and movie count
- Converts minutes to `Xh Ym` format
- Assigns viewer rank:
  - 🥉 Bronze — fewer than 5 movies
  - 🥈 Silver — 5 to 9 movies
  - 🎬 Cinephile — 10 to 19 movies
  - 🏆 Movie Buff — 20 or more movies
- `onResume()` re-fetches stats every time the screen comes back to the foreground

#### `WatchHistoryListActivity.java`
- Layout: `activity_watch_history_list.xml` — **RelativeLayout** root (LinearLayouts inside)
- **History tab** — vertical scrollable list using `LinearLayoutManager`
- HTTP GET to `get_user_history.php`, builds `List<MovieHistoryItem>`, notifies `WatchHistoryAdapter`
- `onResume()` refreshes the list after rating a movie

#### `RateMovieActivity.java`
- Layout: `activity_rate_movie.xml` — **LinearLayout** root (inner LinearLayout for button row)
- Launched when user taps a history card; receives `movie_id` via Intent extra
- HTTP POST to `add_rating.php` with `user_id`, `movie_id`, `rating`
- Calls `finish()` on success to return to the history list

#### `WatchHistoryActivity.java` ⚠️ Legacy / Unused
- Layout: `activity_watch_history.xml` — **LinearLayout** root
- Superseded by automatic tracking in `MovieAdapter`
- Uses hardcoded `userId = 1` and a hardcoded date — should be removed

### Data Models

#### `MovieItem.java`
Represents one movie from the catalog.

| Field | Type | Description |
|-------|------|-------------|
| `movie_id` | int | Database primary key |
| `title` | String | Movie title |
| `imageUrl` | String | Local drawable resource name |
| `duration` | String | Duration in minutes (as text) |
| `genre` | String | Genre label |
| `releaseYear` | String | Release year |

Key method: `getDurationMinutes()` — parses the duration string to int, returns `0` on failure.

#### `MovieHistoryItem.java`
Represents one watch history record.

| Field | Type | Description |
|-------|------|-------------|
| `history_id` | int | History record primary key |
| `movie_id` | int | Foreign key to movie |
| `title` | String | Movie title |
| `imageUrl` | String | Drawable resource name |
| `duration` | String | Duration in minutes |
| `genre` | String | Genre label |
| `releaseYear` | String | Year string |
| `rating` | int | User rating 1–10 (0 = unrated) |
| `watchDate` | String | Date in `YYYY-MM-DD` format |

---

## 👤 Member 3 — Hicham Baydoun `202202338`
**Responsibility: Adapters, Network & Reference**

Covers the RecyclerView adapters, HTTP singleton, layouts, API endpoints, and all reference material.

### Adapters

#### `MovieAdapter.java`
- Item layout: `movie_item.xml` — CardView → **RelativeLayout** root (nested LinearLayouts inside)
- Binds `List<MovieItem>` to the 2-column grid in `MainActivity`
- Inner class `MovieViewHolder` caches: `titleView`, `posterView`, `genreView`, `yearView`, `markAsWatchedButton`
- `onCreateViewHolder()` — inflates `movie_item.xml`
- `onBindViewHolder()` — sets text fields, loads poster via Glide using `getResId()`
- `getResId(String name)` — transforms movie title to drawable resource ID (lowercase, replace spaces/hyphens/apostrophes with underscores; special case: `"schindler's list"` → `"schindler_s_list"`)
- `markMovieAsWatched(MovieItem)` — HTTP POST to `add_watch_history.php` with `user_id`, `movie_id`, `watch_date` (today), `minutes_watched`

#### `WatchHistoryAdapter.java`
- Item layout: `history_movie_item.xml` — CardView → **LinearLayout** root (nested LinearLayouts inside)
- Binds `List<MovieHistoryItem>` to the vertical list in `WatchHistoryListActivity`
- Inner class `HistoryViewHolder` caches: `movieImageView`, `movieTitleTextView`, `movieDurationTextView`, `ratingTextView`, `watchDateTextView`, `deleteBtn`
- Smart image loading: if `imageUrl` starts with `"http"` loads from network via Glide, otherwise resolves local drawable via `getResId()`
- Item tap → launches `RateMovieActivity` passing `movie_id`
- `deleteHistoryItem()` — HTTP POST to `delete_history.php`; on success removes item from list, calls `notifyItemRemoved()` + `notifyItemRangeChanged()` for animated removal

### Utilities

#### `VolleySingleton.java`
Singleton class providing one shared `RequestQueue` for the entire app.

| Method | Description |
|--------|-------------|
| `getInstance(Context)` | Thread-safe (synchronized) singleton accessor |
| `getRequestQueue()` | Lazy initialization of the shared queue |
| `addToRequestQueue(Request)` | Adds any Volley request to the queue |

- Private constructor sets up a `CookieManager` with `ACCEPT_ALL` policy — all PHP session cookies are automatically sent and stored with every request
- Uses `getApplicationContext()` to avoid memory leaks

### API Endpoints

Base URL: `http://10.0.2.2/MovieMateAPI/`

| Method | Endpoint | Called By | Parameters |
|--------|----------|-----------|------------|
| POST | `login_user.php` | LoginActivity | `username`, `password` |
| POST | `register_user.php` | SignUpActivity | `username`, `password` |
| GET | `get_movies.php` | MainActivity | — |
| GET | `get_user_history.php` | HomeActivity, WatchHistoryListActivity | `user_id` |
| POST | `add_watch_history.php` | MovieAdapter | `user_id`, `movie_id`, `watch_date`, `minutes_watched` |
| POST | `add_rating.php` | RateMovieActivity | `user_id`, `movie_id`, `rating` |
| POST | `delete_history.php` | WatchHistoryAdapter | `history_id` |
| POST | `change_password.php` | ChangePasswordActivity | `username`, `old_password`, `new_password` |

### Layout Files

| File | Used By | Root Layout | Key Components |
|------|---------|-------------|----------------|
| `activity_splash.xml` | SplashActivity | RelativeLayout | Logo, ProgressBar |
| `activity_login.xml` | LoginActivity | LinearLayout | Username/password EditTexts, Login Button |
| `activity_sign_up.xml` | SignUpActivity | LinearLayout | Username/password EditTexts, Signup Button |
| `activity_main.xml` | MainActivity | RelativeLayout | Toolbar, RecyclerView, BottomNavigationView |
| `activity_home.xml` | HomeActivity | RelativeLayout | Toolbar, 3 stat cards, BottomNavigationView |
| `activity_watch_history_list.xml` | WatchHistoryListActivity | RelativeLayout | Toolbar, RecyclerView, BottomNavigationView |
| `activity_rate_movie.xml` | RateMovieActivity | LinearLayout | Rating EditText, Submit Button |
| `activity_change_password.xml` | ChangePasswordActivity | LinearLayout | Old/new password EditTexts, Update Button |
| `movie_item.xml` | MovieAdapter | CardView → RelativeLayout | Poster, gradient overlay, title, genre, eye button |
| `history_movie_item.xml` | WatchHistoryAdapter | CardView → LinearLayout | Thumbnail, title, date, rating, delete button |

### Colors & Theme

| Name | Hex | Used For |
|------|-----|----------|
| `background` | `#0F172A` | App background |
| `surface` | `#1E293B` | Cards, bottom nav |
| `primary` / `accent` | `#3B82F6` | Buttons, active tab |
| `text_primary` | `#F8FAFC` | Main text |
| `text_secondary` | `#94A3B8` | Subtitles, meta |

### Known Issues

| # | Issue | Severity |
|---|-------|----------|
| 1 | HTTP only — no HTTPS, passwords sent in plain text | 🔴 Critical |
| 2 | Hardcoded emulator host `10.0.2.2` — won't work on real devices | 🟡 High |
| 3 | `WatchHistoryActivity` is dead code with hardcoded user ID | 🟡 Medium |
| 4 | Poster images break if movie title doesn't match drawable filename | 🟡 Medium |
| 5 | No client-side input validation (password strength, rating range) | 🟡 Medium |
| 6 | No offline/network error handling or retry logic | 🟡 Medium |
| 7 | `SignUpActivity` creates its own `RequestQueue` instead of using `VolleySingleton` | 🟢 Low |
| 8 | No automated unit or integration tests | 🟢 Low |

### Dependencies

| Library | Purpose |
|---------|---------|
| `androidx.appcompat` | Backward-compatible Activity base |
| `com.google.android.material` | BottomNavigationView, Material components |
| `androidx.recyclerview:1.3.0` | Scrollable lists and grids |
| `androidx.cardview:1.0.0` | Card containers |
| `com.android.volley` | HTTP networking |
| `com.github.bumptech.glide` | Image loading and caching |

**Build:** MinSDK 24 · TargetSDK 36 · Java 11 · Kotlin DSL Gradle

---

## 🗺️ App Navigation Flow

```
SplashActivity (5 sec)
        ↓
LoginActivity  ←→  SignUpActivity
        ↓
   ┌────────────────────────┐
   │   Bottom Navigation    │
   ├────────┬───────┬───────┤
  Home    Movies  History
   │        │       │
HomeActivity │  WatchHistoryListActivity
         │              ↓
     MainActivity   RateMovieActivity
```

---

## 🔐 Session Management

- **Login** saves `username` and `user_id` to `SharedPreferences ("MovieMatePrefs")`
- **VolleySingleton** manages PHP session cookies automatically via `CookieManager`
- **Logout** clears all SharedPreferences and starts `LoginActivity` with `FLAG_ACTIVITY_CLEAR_TASK` to wipe the back stack

---

*MovieMate · Group Project · 2026*
