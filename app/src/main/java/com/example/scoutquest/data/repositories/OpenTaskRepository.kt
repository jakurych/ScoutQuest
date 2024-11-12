package com.example.scoutquest.data.repositories

import com.example.scoutquest.data.models.Task
import com.google.firebase.firestore.FirebaseFirestore
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
