package com.example.scoutquest.data.repositories

import android.util.Log
import com.example.scoutquest.data.models.Task
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
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        task.isOpenWorldTask = true
        openTasksCollection.add(task)
            .addOnSuccessListener { onSuccess() }
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

    suspend fun markTaskAsCompleted(userId: String, taskId: String) {

    }

    fun updateOpenTask(
        task: Task,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        task.isOpenWorldTask = true
        openTasksCollection.document(task.taskId.toString())
            .set(task)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getOpenTasks(
        onSuccess: (List<Task>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        openTasksCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                onSuccess(tasks)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }
}
