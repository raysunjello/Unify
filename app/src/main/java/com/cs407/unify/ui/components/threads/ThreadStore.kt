package com.cs407.unify.ui.components.threads

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
    val comments: MutableList<Comment> = mutableListOf(),
    val imageBase64: String? = null,

    // Market-specific fields (nullable for regular posts)
    val isMarketPost: Boolean = false,
    val price: String? = null,
    val contactInfo: String? = null
) : Serializable

object ThreadStore {
    val threads: HashMap<String, Thread> = hashMapOf()
    var selectedThread: Thread? = null
    val savedThreadIds: MutableSet<String> = mutableSetOf()

    fun isThreadSaved(threadId: String): Boolean {
        return savedThreadIds.contains(threadId)
    }

    fun toggleSaved(threadId: String) {
        if (savedThreadIds.contains(threadId)) {
            savedThreadIds.remove(threadId)
        } else {
            savedThreadIds.add(threadId)
        }
    }

    fun getSavedThreads(): List<Thread> {
        return threads.filter { savedThreadIds.contains(it.key) }.map { it.value }
    }

    fun addComment(threadId: String, commentText: String) {
        threads[threadId]?.comments?.add(Comment(text = commentText))
    }
}