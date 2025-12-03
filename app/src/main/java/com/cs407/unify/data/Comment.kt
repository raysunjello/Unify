package com.cs407.unify.data

data class Comment(
    val id: String = "",
    val postId: String = "",
    val text: String = "",
    val authorUid: String = "",
    val authorUsername: String? = null,
    val authorUniversity: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)