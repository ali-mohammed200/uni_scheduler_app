## This is an explanation of the deep stack navigation bug which was fixed in this commit: 
8b129bfc5de5081e296d30063611043078546b45
https://github.com/ali-mohammed200/uni_scheduler_app/commit/8b129bfc5de5081e296d30063611043078546b45

My understanding:
1. The delete/edit functionality lives in the adapter
2. The adapter can not call setResult or finish()
3. Only Activities can call setResult() or finish()
4. The activity does not have access to the edit/delete functionality due to above #1
5. We must pass data between adapter and activity
6. That is done through the use of interfaces
7. An interface in Java is a contract — it defines what methods a class must implement, without specifying how they should be implemented.
8. Basically empty classes/functions that need implementation details
9. This is useful because you can use the interface as if its supposed to do something, then we can implement it somewhere else
10. How is this used in practice for this situation is inside of the delete function in the adapter 
we want something to happen, so we call the interface function
11. Then, in the activity we create the functionality of what we want and pass it as a constructor argument 
to the adapter who now has the implementation of the interface. when the delete happens, your implementation takes over
12. What we do is when deleting, we want to setresult, add intent data so that the previous screen can do something with it



Chatgpt AI Explanation:

You’re trying to **notify the Activity** when something was deleted from the adapter. That’s exactly what this setup allows.

---

## 🔧 The Problem We Solved

You wanted this flow:

> "If I delete an assessment or course from the detail screen (which uses a RecyclerView), I want the previous screen to know that, so it can close itself or refresh."

But since the deletion code is inside the **adapter**, and the logic to close the screen is in the **Activity**, you need a way to *send a signal from the adapter back to the Activity.*

That’s what the **listener interface** + **lambda** is doing!

---

## 🧩 The Key Pieces

Here’s a breakdown of how everything connects:

---

### 🧱 1. The Adapter Defines a Listener Interface

```java
public interface OnAssessmentDeletedListener {
    void onAssessmentDeleted(int assessmentId);
}
```

This is just a contract — "Hey, if something gets deleted, I promise to call this function and give you the ID that was deleted."

---

### 🔁 2. The Adapter Holds a Reference to the Listener

```java
private OnAssessmentDeletedListener deletedListener;

public AssessmentAdapter(List<Assessment> assessments, OnAssessmentDeletedListener listener) {
    this.assessments = assessments;
    this.deletedListener = listener;
}
```

The adapter saves a reference to the listener, so it can **call it later** when something is deleted.

---

### 🗑️ 3. You Call the Listener Inside the Adapter When Deleting

```java
deletedListener.onAssessmentDeleted(assessment.getId());
```

When the user confirms a delete in your AlertDialog, the adapter:

* Removes the item from the DB
* Removes the item from the list
* Notifies the RecyclerView
* **Notifies the listener** (i.e. your activity!) that something was deleted

---

### 💬 4. The Activity Provides the Listener (via a Lambda Expression)

```java
adapter = new AssessmentAdapter(assessments, deletedId -> {
    Intent resultIntent = new Intent();
    resultIntent.putExtra("deletedAssessmentId", deletedId);
    setResult(RESULT_OK, resultIntent);
});
```

This lambda is your implementation of the interface. It's the same as writing:

```java
new OnAssessmentDeletedListener() {
    @Override
    public void onAssessmentDeleted(int deletedId) {
        // Do something with the ID
    }
}
```

But in modern Java/Kotlin, lambdas are shorter and easier to read.

So, what is this doing?

* `deletedId -> { ... }` means: “When `onAssessmentDeleted(int deletedId)` is called, run this block of code.”
* It sets the result with the deleted ID.
* Your **ActivityResultLauncher** or `onActivityResult` can catch this and act on it (e.g., close the previous screen).

---

## 📡 Full Flow Recap

Here’s a real-world timeline of what’s happening:

1. User is on `DetailCourseActivity` and sees a list of assessments.
2. They long-press and delete an assessment.
3. Adapter deletes it from the DB and list.
4. Adapter calls `deletedListener.onAssessmentDeleted(id)`.
5. The Activity’s lambda runs: it prepares an intent and calls `setResult(...)`.
6. When you finish the activity (`finish()`), the previous activity gets that result.

---

## 🧠 Why This Works

* **Decouples logic**: The adapter doesn’t care what happens after deletion — it just tells whoever is listening.
* **Reusability**: You can now reuse this adapter in other places and provide different behaviors (e.g., refresh list instead of closing the screen).
* **Cleaner separation** of view logic (RecyclerView) and business logic (screen closing or DB state).

---

## 🧪 What Would You See in Practice?

Let’s say you deleted Assessment #5 on the `DetailCourseActivity`:

* A Toast might show “Assessment deleted.”
* The assessment disappears from the list.
* If `deletedId == course.id` (and you're in DetailCourseActivity), the Activity finishes and you're returned to the list.

