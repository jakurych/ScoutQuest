package com.example.scoutquest.data.repositories

import android.util.Log
import com.example.scoutquest.data.models.Task
import com.example.scoutquest.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OpenTaskRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val openTasksCollection = firestore.collection("open_tasks")

    fun addOpenTask(
        task: Task,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        task.isOpenWorldTask = true
        openTasksCollection.add(task)
            .addOnSuccessListener { documentReference ->
                onSuccess(documentReference.id)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    suspend fun getAllOpenTasks(): List<Task> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("open_tasks")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Task::class.java)
            }
        } catch (e: Exception) {
            Log.e("OpenTaskRepository", "Error fetching open tasks", e)
            emptyList()
        }
    }

}
