package com.cs407.unify.ui.components.threads

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.io.Serializable
import java.util.UUID

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

data class Thread(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val body: String,
    val hub: String,
    val comments: MutableList<Comment> = mutableListOf()
) : Serializable

object ThreadStore {
    val threads: HashMap<String, Thread> = hashMapOf()
    var selectedThread: Thread? = null
    val savedThreadIds: MutableSet<String> = mutableSetOf() // Track saved thread IDs


    // Helper function to check if a thread is saved
    fun isThreadSaved(threadId: String): Boolean {
        return savedThreadIds.contains(threadId)
    }

    // Helper function to toggle saved status
    fun toggleSaved(threadId: String) {
        if (savedThreadIds.contains(threadId)) {
            savedThreadIds.remove(threadId)
        } else {
            savedThreadIds.add(threadId)
        }
    }

    // Helper function to get saved threads
    fun getSavedThreads(): List<Thread> {
        return threads.filter { savedThreadIds.contains(it.key) }.map { it.value }
    }

    // Helper function to add a comment to a thread
    fun addComment(threadId: String, commentText: String) {
        threads[threadId]?.comments?.add(Comment(text = commentText))
    }
}