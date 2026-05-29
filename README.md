## Product Requirements Document (PRD): Dynamic Task Catalyst

**App Name:** TaskCatalyst (Working Title)
**Platform:** Android (Kotlin)
**Target Audience:** Students, busy professionals, and anyone struggling with task paralysis or context switching.

---

### 1. Product Overview

Traditional to-do lists treat all tasks equally, leading to overwhelm when the list grows. TaskCatalyst solves this by forcing users to categorize tasks using the **Eisenhower Matrix** (Urgent vs. Important). The app visually separates tasks into four actionable quadrants and provides a "Focus Mode" to help users execute high-priority items without distraction.

### 2. Core User Journey

1. **Capture:** The user quickly adds a task, answering two simple binary questions: "Is this urgent?" and "Is this important?"
2. **Organize:** The app routes the task to one of four visual quadrants on the home screen dashboard.
3. **Execute:** The user taps a high-priority task, enters "Focus Mode," and starts a 25-minute Pomodoro timer to complete it.
4. **Complete:** The task is marked done, removed from the grid, and saved to a history log.

---

### 3. Feature Specifications (Scope)

#### Epic 1: Task Management (CRUD)

* **Task Creation:** Users can input a Title (required), Description (optional), and Due Date (optional).
* **Categorization Toggles:** Two prominent switches/checkboxes during creation: `Urgent` and `Important`.
* **Editing:** Users can tap an existing task to update its details or change its categorization, which dynamically moves it to a new quadrant.
* **Deletion/Completion:** Swipe-to-delete or a checkbox to mark a task as completed.

#### Epic 2: The Eisenhower Dashboard (Home Screen)

A responsive, 2x2 grid view displaying tasks categorized by the logic below:

| Quadrant | Urgent | Important | User Action |
| --- | --- | --- | --- |
| **Q1 (Top Left)** | Yes | Yes | **Do First** (High Priority) |
| **Q2 (Top Right)** | No | Yes | **Schedule** (Medium Priority) |
| **Q3 (Bottom Left)** | Yes | No | **Minimize** (Low Priority) |
| **Q4 (Bottom Right)** | No | No | **Eliminate** (Delete/Archive) |

* Each quadrant displays a scrollable list of minimalist task cards.
* If a quadrant is empty, it shows a subtle placeholder graphic or text (e.g., "Nothing urgent right now").

#### Epic 3: Focus Mode Timer

* **Trigger:** Accessible only from tasks in **Q1 (Do First)** or **Q2 (Schedule)**.
* **UI:** A full-screen distraction-free view showing only the task title and a countdown timer.
* **Timer Logic:** Defaults to 25 minutes (Pomodoro technique). Users can pause, resume, or abort.
* **Completion:** When the timer hits 00:00, a local alarm sounds, and the user is prompted to mark the task as complete or take a 5-minute break.

#### Epic 4: Notifications & Alarms

* **Due Date Alerts:** If a task has a specific time attached, the app triggers a local push notification 15 minutes prior.
* **Focus Mode Alerts:** An auditory chime and notification when a Focus Mode timer completes, running reliably even if the app is in the background.

---

### 4. Technical Architecture & Constraints

* **Language:** Kotlin.
* **UI Framework:** Jetpack Compose (Modern, declarative UI).
* **Architecture Pattern:** MVVM (Model-View-ViewModel) to cleanly separate UI from database logic.
* **Local Storage:** Room Database (SQLite wrapper) for offline, fast data persistence.
* **Background Processing:** `WorkManager` for scheduled notifications; `CountDownTimer` or Coroutines for the Focus Mode timer.
* **Dependencies:** `androidx.room`, `androidx.lifecycle.viewmodel.compose`, `androidx.navigation.compose`.

---

### 5. Gemini Integration Strategy (How to build it)

Since you are using Gemini directly inside Android Studio, here is exactly how to leverage it for each stage of this PRD:

1. **Database Setup:**
* *Prompt for Gemini:* "Generate a Room Database setup in Kotlin for a `Task` entity. The entity needs fields for id, title, description, isUrgent (boolean), isImportant (boolean), and dueDate (Long). Include the DAO with queries to fetch tasks grouped by their urgent/important boolean combinations."


2. **UI Layout (The Grid):**
* *Prompt for Gemini:* "Write a Jetpack Compose layout for a 2x2 grid covering the whole screen. Each quadrant should have a distinct background color and accept a list of strings to display as scrollable text cards."


3. **State Management:**
* *Prompt for Gemini:* "Create an MVVM ViewModel using Kotlin StateFlow that connects to my Room database and exposes four distinct lists of tasks (Q1, Q2, Q3, Q4) to my Jetpack Compose UI."


4. **Timer Logic:**
* *Prompt for Gemini:* "Write a Kotlin Coroutine-based countdown timer for Jetpack Compose that starts at 25 minutes, formats the output as MM:SS, and has pause and resume functions."
